package core;

import annotation.*;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class TableCreator
{

    private static String getConstraints(Constraints con)
    {
        String constraints = "";
        //判断是否为null
        if (!con.allowNull())
        {
            constraints += " NOT NULL ";
        }
        //判断是否是主键
        if (con.primaryKey())
        {
            constraints += " PRIMARY KEY ";
        }
        //是否唯一
        if (con.unique())
        {
            constraints += " UNIQUE ";
        }
        return constraints;
    }


    //创建表SQL语句
    @Test
    public static String createTable( Class<?> cl ) throws ClassNotFoundException {

        //获取DBTable注解
        Table dbTable = cl.getAnnotation(Table.class);
        System.out.println("dbTable:"+dbTable);
        //判断DBTable注解是否存在
        if (dbTable == null)
        {
            System.out.println("缺少Table注解");
            return null;
        }

        //如果@DBTable注解存在，获取表名
        String tableName = dbTable.name();
        System.out.println("tableName:"+tableName);
        String SQL="CREATE TABLE " + tableName + "(";
        System.out.println(SQL);
        //判断表名是否存在
        if (tableName.length() < 1)
        {
            //不存在，说明默认就是类名，通过 cl.getSimpleName()获取类名并且大写
            tableName = cl.getSimpleName().toUpperCase();
        }

        //定义获取column的容器
        List<String> columnDefs = new ArrayList<String>();

        //循环属性字段
        for (Field field : cl.getDeclaredFields())
        {
            //定义表字段名称变量
            String columnName = null;
            //获取字段上的注解
            Annotation[] anns = field.getDeclaredAnnotations();
            //判断属性是否存在注解
            if (anns.length < 1)
                continue;

            //判断是否是我们定义的数据类型
            if (anns[0] instanceof SQLInteger)
            {
                //强转
                SQLInteger sInt = (SQLInteger)anns[0];
                //判断是否注解的name是否有值
                if (sInt.name().length() < 1)
                {
                    //如果没有值，说明是类的属性字段，获取属性并转换大写
                    columnName = field.getName().toUpperCase();
                }
                else
                { //如果有值，获取设置的name值
                    columnName = sInt.name();
                }
                //放到属性的容器内
                columnDefs.add(columnName + " INT " + getConstraints(sInt.constraints()));
            }

            if (anns[0] instanceof SQLString)
            {
                SQLString sString = (SQLString)anns[0];
                if (sString.name().length() < 1)
                {
                    columnName = field.getName().toUpperCase();
                }
                else
                {
                    columnName = sString.name();
                }
                columnDefs.add(columnName + " VARCHAR(" + sString.value() + ")" + getConstraints(sString.constraints()));
            }

            if (anns[0] instanceof SQLByte)
            {
                SQLByte sByte = (SQLByte)anns[0];
                if (sByte.name().length() < 1)
                {
                    columnName = field.getName().toUpperCase();
                }
                else
                {
                    columnName = sByte.name();
                }
                columnDefs.add(columnName + " Byte " + getConstraints(sByte.constraints()));
            }
        }
            //定义生成创建表的SQL语句
            StringBuilder createCommand = new StringBuilder();
            //循环上面属性容器
            for (String columnDef : columnDefs)
            {
                //把属性添加到sql语句中
                createCommand.append("\n  " + columnDef + ",");
                //去掉最后一个逗号

            }
            String tableCreate="";
            if(createCommand.length()>1)
            {
                tableCreate = createCommand.substring(0, createCommand.length() - 1) + ");";
            }else
            {
                return null;
            }
            //打印
            SQL = SQL+tableCreate;
            System.out.println("Table creation SQL for " + cl.getName() + " is :\n" + SQL);
            return SQL;
    }

}
