package core;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static core.Config.mapperList;

public class Session {

    private static Map<String, List<Object>> cache  = new HashMap<String, List<Object>>();

    private Connection connection;

    public Session(Connection conn){
        this.connection = conn;
    }

    public void insert(Object entity) throws IllegalAccessException, SQLException {
        //从Config类里面获得保存有映射信息的集合
        List<Mapper> mapperList = Config.mapperList;
        String SQL = "";
        //遍历集合，找到entity对象
        for(Mapper mapper:mapperList){
            if(mapper.getClassName().equals(entity.getClass().getName())){
                String SQL1 = "insert into "+mapper.getTableName()+" ( ";
                String SQL2 = " ) values ( ";

                //根据对象获取所属类从而获取类中所有属性
                Field[] fields = entity.getClass().getDeclaredFields();
                for (Field field:fields){

                    //设置私有属性的权限
                    field.setAccessible(true);
                    //根据属性名从map中得到字段名
                    String columnName = mapper.getPropMap().get(field.getName());

                    if (field.get(entity) == null){
                        continue;
                    }

                    //根据属性得到值
                    String columnValue = field.get(entity).toString();
                    SQL1 += columnName+",";
                    SQL2 += "'"+columnValue+"',";
                }

                SQL = SQL1.substring(0,SQL1.length()-1)+SQL2.substring(0,SQL2.length()-1)+" ) ";
                break;
            }
        }
        System.out.println("insert:"+SQL);
        insertCache(SQL);

        //通过JDBC执行语句
        PreparedStatement statement = connection.prepareStatement(SQL);
        statement.executeUpdate();
        statement.close();
    }

    //自定义SQL实现insert
    public void insert(String SQL) throws SQLException {
        insertCache(SQL);
        //通过JDBC执行语句
        PreparedStatement statement = connection.prepareStatement(SQL);
        statement.executeUpdate();
        statement.close();
        System.out.println("insert:"+SQL);
    }

    //根据主键进行删除
    //delete from 表名 where 主键 = 值
    public void delete(Object entity) throws NoSuchFieldException, IllegalAccessException, SQLException {
        //从Config类里面获得保存有映射信息的集合
        List<Mapper> mapperList = Config.mapperList;

        //遍历集合，找到entity对象
        for(Mapper mapper:mapperList){
            if(mapper.getClassName().equals(entity.getClass().getName())){
                String SQL ="delete from "+ mapper.getTableName()+" where ";

                //得到主键字段名和属性名
                Map<String,String> idMap= mapper.getIdMap();

                for(Map.Entry<String,String> id : idMap.entrySet()){
                    System.out.println(id.getValue());
                    //获取所属类，根据id.getkey得到主键属性
                    Field field = entity.getClass().getDeclaredField(id.getKey().toString());
                    field.setAccessible(true);
                    //根据属性获取属性的值
                    String idVal = field.get(entity).toString();

                    //拼接字符串
                    String SQL1 = SQL+id.getValue()+" = "+idVal;

                    //修改缓存信息
                    delCache(SQL1);

                    System.out.println("delete:"+SQL1+"\n");
                    //通过JDBC执行语句
                    PreparedStatement statement = connection.prepareStatement(SQL1);
                    statement.executeUpdate();
                    statement.close();
                }
                break;
            }
        }
    }

    //自定义SQL实现delete
    public void delete(String SQL) throws SQLException {
        delCache(SQL);
        //通过JDBC执行语句
        PreparedStatement statement = connection.prepareStatement(SQL);
        statement.executeUpdate();
        statement.close();
    }

    //根据主键进行查询
    // select * from 表名 where 主键字段 = 值
    public List<Object> selectId(Object obj) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        //从Config类里面获得保存有映射信息的集合
        List<Mapper> mapperList = Config.mapperList;
        String SQL = "";

        //遍历集合，找到entity对象
        for(Mapper mapper:mapperList) {
            //System.out.println(mapper.getClassName()+" "+obj.getClass().getName());
            if (mapper.getClassName().equals(obj.getClass().getName())) {

                //得到map集合
                Map<String,String> idMap= mapper.getIdMap();

                for(Map.Entry<String,String> id : idMap.entrySet()) {
                    //得到类，根据map中的属性名得到主键属性
                    Field field = obj.getClass().getDeclaredField(id.getKey().toString());
                    field.setAccessible(true);
                    //根据属性和对象得到属性值
                    String idVal = field.get(obj).toString();

                    //拼接字符串
                    SQL = "select * from " + mapper.getTableName() + " where ";
                    //System.out.println(SQL);

                    //获得主键字段名
                    Object[] idColumn = mapper.getIdMap().values().toArray();

                    SQL += idColumn[0].toString() + " = "+ idVal;

                    System.out.println("select#:"+SQL);
                    break;
                }

                break;
            }
        }

        List<Object> objList = new ArrayList<Object>();

        if(cache.get(SQL) != null){
            System.out.println("cache");
            objList = cache.get(SQL);
            return objList;
        }

        //通过JDBC执行语句,得到结果集
        PreparedStatement statement = connection.prepareStatement(SQL);
        ResultSet rs = statement.executeQuery();


        //封装结果集，返回对象数组
        while(rs.next())
        {
            Object obj1 = obj.getClass().newInstance();
//            System.out.println("obj1:"+obj1.getClass().getName());
            for (Mapper mapper:mapperList){
                if (mapper.getClassName().equals(obj1.getClass().getName())) {
                    //获取prop集合
                    Map<String,String> propMap =  mapper.getPropMap();
                    //获取propMap键集合set
                    Set<String> keySet = propMap.keySet();

                    for (String prop:keySet){
//                        System.out.println("prop:"+prop);
                        //根据属性名称在map获取字段名称
                        String column = propMap.get(prop);
//                        System.out.println("column:"+column);

                        //得到该对象的类，获取prop的属性
                        Field field = obj1.getClass().getDeclaredField(prop);
                        field.setAccessible(true);
                        //获取属性的类型
                        Type type = field.getType();
//                          System.out.println("type:"+type.toString());
                        if("class java.lang.String".equals(type.toString()))
                        {
//                            System.out.println(rs.getString(column));
                            field.set( obj1,rs.getString(column));
                        }
                        else if("int".equals(type.toString()))
                        {
//                            System.out.println(rs.getInt(column));
                            field.set(obj1,rs.getInt(column));
                        }
                        else if("double".equals(type.toString()))
                        {
//                            System.out.println(rs.getDouble(column));
                            field.set(obj1,rs.getDouble(column));
                        }
                        else if("float".equals(type.toString()))
                        {
                            field.set(obj1,rs.getFloat(column));
                        }

                    }
                    break;
                }
            }
//            System.out.println(obj1.toString());
            objList.add(obj1);
        }
//        System.out.println(objList.size());
        //释放资源
        statement.close();
        rs.close();

        cache.put(SQL,objList);
        //返回对象
        return objList;
    }

    //自定义SQL实现select
    public List<Object> select(Object object,String SQL) throws SQLException, IllegalAccessException, InstantiationException, NoSuchFieldException {

        //通过JDBC执行语句,得到结果集
        PreparedStatement statement = connection.prepareStatement(SQL);
        ResultSet rs = statement.executeQuery();

        List<Object> objList = new ArrayList<Object>();

        if(cache.get(SQL) != null){
            System.out.println("cache");
            objList = cache.get(SQL);
            return objList;
        }

        //封装结果集，返回对象数组
        while(rs.next())
        {
            Object obj = object.getClass().newInstance();
            System.out.println("obj:"+obj.getClass().getName());
            for (Mapper mapper:mapperList){
                if (mapper.getClassName().equals(obj.getClass().getName())) {
                    //得到prop属性的map集合
                    Map<String,String> propMap =  mapper.getPropMap();
                    //在map中提取出key
                    Set<String> keySet = propMap.keySet();
                    for (String prop:keySet){
                        //根据键（属性名）获取值（字段名）
                        String column = propMap.get(prop);
                        //获取类名，获取prop属性
                        Field field = obj.getClass().getDeclaredField(prop);
                        field.setAccessible(true);
                        Type type = field.getType();
                        if("class java.lang.String".equals(type.toString()))
                        {
                            field.set( obj,rs.getString(column));
                        }
                        else if("int".equals(type.toString()))
                        {
                            field.set(obj,rs.getInt(column));
                        }
                        else if("double".equals(type.toString()))
                        {
                            field.set(obj,rs.getDouble(column));
                        }
                        else if("float".equals(type.toString()))
                        {
                            field.set(obj,rs.getFloat(column));
                        }

                    }
                    break;
                }
            }

            objList.add(obj);
        }
        //释放资源
        statement.close();
        rs.close();
        //返回对象
        cache.put(SQL,objList);
        return objList;
    }

    //根据Select注解查询
    public List<Object> select(Object object) throws NoSuchFieldException, IllegalAccessException, SQLException, InstantiationException {
        //从Config类里面获得保存有映射信息的集合
        List<Mapper> mapperList = Config.mapperList;
        String SQL = "";

        //遍历集合，找到entity对象
        for(Mapper mapper:mapperList) {
            if (mapper.getClassName().equals(object.getClass().getName())) {

                //调用mapper.SelectMap()获取select的map集合
                Map<String,String> selectMap= mapper.getSelectMap();

                for(Map.Entry<String,String> select : selectMap.entrySet()) {

                    //获取类名，根据map中属性名获取属性
                    Field field = object.getClass().getDeclaredField(select.getKey().toString());
                    field.setAccessible(true);
                    //通过反射获取属性的值
                    String Val = field.get(object).toString();

                    //拼接表名
                    SQL = "select * from " + mapper.getTableName() + " where ";

                    //获得select字段名，转换成数组
                    Object[] Column = mapper.getSelectMap().values().toArray();

                    //取第一个
                    SQL += Column[0].toString() + " = '"+  Val +"';";

                    System.out.println("select:"+SQL);
                    break;
                }

                break;
            }
        }

        List<Object> objList = new ArrayList<Object>();

        if(cache.get(SQL) != null){
            System.out.println("cache");
            objList = cache.get(SQL);
            return objList;
        }

        //通过JDBC执行语句,得到结果集
        PreparedStatement statement = connection.prepareStatement(SQL);
        ResultSet rs = statement.executeQuery();


        //封装结果集，返回对象数组
        while(rs.next())
        {
            Object obj1 = object.getClass().newInstance();
//            System.out.println("obj1:"+obj1.getClass().getName());
            for (Mapper mapper:mapperList){
                if (mapper.getClassName().equals(obj1.getClass().getName())) {
                    Map<String,String> propMap =  mapper.getPropMap();
                    Set<String> keySet = propMap.keySet();
                    for (String prop:keySet){
//                        System.out.println("prop:"+prop);
                        String column = propMap.get(prop);
//                        System.out.println("column:"+column);
                        Field field = obj1.getClass().getDeclaredField(prop);
                        field.setAccessible(true);
                        Type type = field.getType();
//                          System.out.println("type:"+type.toString());
                        if("class java.lang.String".equals(type.toString()))
                        {
//                            System.out.println(rs.getString(column));
                            field.set( obj1,rs.getString(column));
                        }
                        else if("int".equals(type.toString()))
                        {
//                            System.out.println(rs.getInt(column));
                            field.set(obj1,rs.getInt(column));
                        }
                        else if("double".equals(type.toString()))
                        {
//                            System.out.println(rs.getDouble(column));
                            field.set(obj1,rs.getDouble(column));
                        }
                        else if("float".equals(type.toString()))
                        {
                            field.set(obj1,rs.getFloat(column));
                        }
                    }
                    break;
                }
            }
//            System.out.println(obj1.toString());

            objList.add(obj1);
        }
//        System.out.println(objList.size());
        //释放资源
        statement.close();
        rs.close();
        //返回对象
        System.out.println(SQL);
        cache.put(SQL,objList);
//        System.out.println("cache"+cache.size());
        return objList;
    }

    //根据主键进行修改
    //UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
     public void update(Object entity,String condition ) throws NoSuchFieldException, SQLException, IllegalAccessException {

        List<Mapper> mapperList = Config.mapperList;
        String SQL="";
        //遍历集合，找到entity对象
        for(Mapper mapper:mapperList) {
            if (mapper.getClassName().equals(entity.getClass().getName())) {
                //拼接表名
                SQL = "update "+mapper.getTableName()+" set ";

                //得到主键字段名和属性名
                Map<String,String> idMap= mapper.getIdMap();

                for(Map.Entry<String,String> id : idMap.entrySet()){
                    //得到类，得到主键属性，得到主键的值
                    Field field = entity.getClass().getDeclaredField(id.getKey().toString());
                    field.setAccessible(true);
                    //取出主键的值
                    String idVal = field.get(entity).toString();

                    SQL = SQL +id.getValue()+" = "+idVal;
                }
                break;
            }
        }

        SQL += " where "+condition;
        updateCache(SQL);
        System.out.println("update:"+SQL);
        //通过JDBC执行语句
        PreparedStatement statement = connection.prepareStatement(SQL);
        statement.executeUpdate();
        statement.close();
    }
    //关闭连接，释放资源
    public void update(String tableName,String setSql,String conditionSql) throws SQLException {
        String SQL = "update "+tableName+" set "+setSql+" where "+conditionSql;
        updateCache(SQL);
        System.out.println("update:"+SQL);
        //通过JDBC执行语句
        PreparedStatement statement = connection.prepareStatement(SQL);
        statement.executeUpdate();
        statement.close();
    }

    public void update(String sql) throws SQLException {
         updateCache(sql);
//        //通过JDBC执行语句
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
        statement.close();
    }

    public void updateCache(String sql){
        List<String> list = Arrays.asList(sql.split(" "));

        //修改操作所涉及到的表名和列名
        String tName = "";//表名
        String cName = "";//列名

        for (int i=0;i<list.size();i++){
            if("update".equals(list.get(i)) ||  "UPDATE".equals(list.get(i))){
                tName += list.get(i+1);
            }
            if("where".equals(list.get(i)) ||  "WHERE".equals(list.get(i))){
                cName += list.get(i+1);
            }
        }

        System.out.println("tName:"+tName+" cName:"+cName);

        for(Map.Entry<String,List<Object>> c : cache.entrySet()){
            String key = c.getKey();
            if( key.contains(tName) && key.contains(cName)){
                cache.remove(key);
                System.out.println("cache.remove:"+key);
            }
        }
    }

    public void delCache(String sql){
        List<String> list = Arrays.asList(sql.split(" "));

        //修改操作所涉及到的表名和列名
        String tName = "";//表名

        for (int i=0;i<list.size();i++){
            if("from".equals(list.get(i)) ||  "FROM".equals(list.get(i))){
                tName += list.get(i+1);
            }
        }

        System.out.println("tName:"+tName);

        for(Map.Entry<String,List<Object>> c : cache.entrySet()){
            String key = c.getKey();
            if( key.contains(tName)){
                cache.remove(key);
                System.out.println("cache.remove:"+key);
            }
        }
    }

    public void insertCache(String sql){
        List<String> list = Arrays.asList(sql.split(" "));

        //修改操作所涉及到的表名和列名
        String tName = "";//表名

        for (int i=0;i<list.size();i++){
            if("into".equals(list.get(i)) ||  "INTO".equals(list.get(i))){
                tName += list.get(i+1);
            }
        }

        System.out.println("tName:"+tName);

        for(Map.Entry<String,List<Object>> c : cache.entrySet()){
            String key = c.getKey();
            if( key.contains(tName)){
                cache.remove(key);
                System.out.println("cache.remove:"+key);
            }
        }
    }

    public void close() throws SQLException {
        if(connection != null)
        {
            connection.close();
            connection = null;
        }
    }

    public void createTable(Class<?> cla) throws ClassNotFoundException, SQLException {
        String sql = TableCreator.createTable(cla);
        System.out.println("createTable:"+sql);
        //通过JDBC执行语句
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
        statement.close();

    }
}
