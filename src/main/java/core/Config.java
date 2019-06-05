package core;

import org.dom4j.Document;
import utils.AnnotationUtil;
import utils.Dom4jUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;


//解析并封装框架的核心配置文件中的数据
public class Config {
    private static String classpath;//核心配置文件的路径

    private static File configFile;//核心配置文件

    private static Map<String,String> propConfig;//property标签中的数据

    private static Set<String>mappingPath;//映射配置文件的路径

    private static Set<String>entityPath;//实体类

    public static List<Mapper>mapperList;//映射信息

    static {
         mapperList = new ArrayList<Mapper>();

        //得到核心配置文件的路径
        classpath = Thread.currentThread().getContextClassLoader().getResource("./").getPath();

        //针对中文路径进行转码
        try {
            classpath = URLDecoder.decode(classpath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("classpath:"+classpath);
        //得到核心配置文件
        configFile = new File(classpath + "MiniORM.cfg.xml");

        //解析核心配置文件中的数据
        if(configFile.exists()){
            System.out.println("找到核心配置文件！");
            //核心配置文件
            Document doc = Dom4jUtil.getXMLDocument(configFile.getPath());
            //解析property标签
            propConfig = Dom4jUtil.ElementsMap(doc,"property","name");
            //解析mapping标签
            mappingPath = Dom4jUtil.ElementsSet(doc,"mapping","resource");
            //解析entity标签
            entityPath = Dom4jUtil.ElementsSet(doc,"entity","package");

        }else{
            configFile = null;
            System.out.println("缺少核心配置文件：MiniORM.cfg.xml");
        }
    }
    //连接数据库
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        String url = propConfig.get("url");
        String driverClass = propConfig.get("driver");
        String username = propConfig.get("username");
        String password = propConfig.get("password");

        System.out.println(password);
        Class.forName(driverClass);
        Connection connection = DriverManager.getConnection(url,username,password);
        connection.setAutoCommit(true);
        return connection;
    }

    //解析映射信息
    private void getMapping() throws ClassNotFoundException {

        //解析***.mapper.xml文件拿到映射数据
        for(String xmlPath:mappingPath){
            Document doc = Dom4jUtil.getXMLDocument(classpath + xmlPath);
            String className = Dom4jUtil.getPropValue(doc,"class","name");
            String tableName = Dom4jUtil.getPropValue(doc,"class","table");
            Map<String,String> id = Dom4jUtil.ElementsIDMap(doc);
            Map<String,String> prop = Dom4jUtil.ElementsMap(doc);
            Map<String,String> select = Dom4jUtil.ElementsSelectMap(doc);

            Mapper mapper = new Mapper();
            mapper.setClassName(className);
            mapper.setIdMap(id);
            mapper.setPropMap(prop);
            mapper.setTableName(tableName);
            mapper.setSelectMap(select);

            System.out.println(mapper.toString());
            mapperList.add(mapper);
        }

        //解析注解
        for(String packagePath:entityPath){
            Set<String> nameSet = AnnotationUtil.getClassName(packagePath);
            for(String name:nameSet){
                Class cla = Class.forName(name);
                String className = AnnotationUtil.getClassName(cla);
                String tableName = AnnotationUtil.getTableName(cla);
                Map<String,String> id = AnnotationUtil.getIDMapping(cla);
                Map<String,String> prop = AnnotationUtil.getPropMapping((cla));
                Map<String,String> select = AnnotationUtil.getSelectMapping(cla);

                Mapper mapper = new Mapper();
                mapper.setClassName(className);
                mapper.setIdMap(id);
                mapper.setPropMap(prop);
                mapper.setTableName(tableName);
                mapper.setSelectMap(select);
                mapperList.add(mapper);
            }
        }

    }

    public Session buildSession() throws SQLException, ClassNotFoundException {
        //连接数据库
        Connection connection = getConnection();

        //得到映射数据
        getMapping();

        //创建Session对象
        return new Session(connection);
    }
}
