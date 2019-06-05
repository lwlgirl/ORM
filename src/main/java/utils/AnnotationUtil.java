package utils;

import annotation.Column;
import annotation.Id;
import annotation.Select;
import annotation.Table;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//使用反射解析实体类中的注解
public class AnnotationUtil {
    //得到类名
    public static String getClassName(Class cla){
        return cla.getName();
    }

    //得到Table注解中的表名
    public static String getTableName(Class cla){
        if(cla.isAnnotationPresent(Table.class)){
            Table ormTable = (Table)cla.getAnnotation(Table.class);
            return ormTable.name();
        }else{
            System.out.println("getTableName : 表名缺少注解！");
            return null;
        }
    }

    //获取主键属性和对应的字段
    public static Map<String,String> getIDMapping (Class cla){
       // boolean flag = true;
        Map<String,String> mapping = new HashMap<String, String>();
        Field[] fields = cla.getDeclaredFields();
        for(Field field:fields){
            if(field.isAnnotationPresent(Id.class)){
               String fieldName = field.getName();
               if(field.isAnnotationPresent(Column.class)){
                   Column column = field.getAnnotation(Column.class);
                   String columnName = column.name();
                   mapping.put(fieldName,columnName);
               }else{
                   System.out.println("getIDMapping : 缺少column注解！");
               }
            }
        }
        return mapping;
    }

    //获取带有@Select注解的属性和对应的字段
    public static Map<String,String> getSelectMapping (Class cla){
        // boolean flag = true;
        Map<String,String> mapping = new HashMap<String, String>();
        Field[] fields = cla.getDeclaredFields();
        for(Field field:fields){
            if(field.isAnnotationPresent(Select.class)){
                String fieldName = field.getName();
                if(field.isAnnotationPresent(Column.class)){
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column.name();
                    mapping.put(fieldName,columnName);
                }else{
                    System.out.println("getSelectMapping : 缺少column注解！");
                }
            }
        }
        return mapping;
    }
    //获取所有属性和对应的字段
    public static Map<String,String> getPropMapping (Class cla){
        Map<String,String> mapping = new HashMap<String, String>();
        mapping.putAll(getIDMapping(cla));
        Field[] fields = cla.getDeclaredFields();
        for (Field field:fields){
            if(field.isAnnotationPresent(Column.class)){
                Column ormColumn = field.getAnnotation(Column.class);
                String fieldName = field.getName();
                String columnName = ormColumn.name();
                mapping.put(fieldName,columnName);
            }
        }
        return mapping;
    }

    //获取包下所有类名
    public static Set<String> getClassName(String packagePath){
        Set<String> names = new HashSet<String>();
        String packageFile = packagePath.replace(".","/");
        String classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        if (classpath == null){
            classpath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        }try{
                classpath = java.net.URLDecoder.decode(classpath,"utf-8");//如果有汉字，做转码处理
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        File dir = new File(classpath + packageFile);
        if(dir.exists()){
            File[] files = dir.listFiles();
            for (File f : files){
                String name = f.getName();
                if(f.exists() && name.endsWith(".class")){
                    name = packagePath + '.' + name.substring(0,name.lastIndexOf("."));
                    names.add(name);

                }
            }
        }else{
            System.out.println("getClassName ：包路径不存在！");
        }
        return names;
    }
}
