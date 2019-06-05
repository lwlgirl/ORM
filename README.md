//  这是一个手写的ORM框架，可以对数据库进行建表、增、删、查、改操作，每个功能都重载了多种方法，以满足用户需求。

//  此外，还有缓存功能，第二次进行同样的查找操作时将不会调用数据库，而是在缓存器里面直接调出，可以有效增加数据查找的速度。


一、建表：

1.创建一个类（一个类对应一张表）

（1）必须有@Table注解，name="表名"。

（2）字段上面必须用提供的注解注明类型。

（3）声明外键的格式参考@SQLInterger注解。

@Table(name = "tableName")
public class Test3 {
    @SQLString()
    public String name;
    @SQLInteger(constraints = @Constraints(unique = true))
    public Integer age;
}

2.使用

Config config = new Config();//解析配置信息
Session session = config.buildSession();//创建session
session.createTable(Test3.class);//session封装了对数据库操作的所有功能
二、插入（session.insert(Object))

1.创建一个实体类（属性对应表中的字段）


public class Demo {
 
    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
ps:下面第二步和第三步配置任意一个即可。

2.新建配置文件

（1）class标签里面:name对应实体类全名，table对应表名

（2）id和property标签里面:name对应属性名，column对应表中字段名

<?xml version="1.0" encoding="UTF-8" ?>
<orm-mapping>
    <class name="entity.Demo" table="Student">
        <id name="id" column="SId"/>
        <property name="name" column="Sname"/>
    </class>
</orm-mapping>
3.设置注解

@Table(name = "Student")//设置表名
public class Demo {

    @Id//设置主键     ？？？
    @Column(name = "SId")//设置属性对应的字段名
    private int id;
    @Column(name = "Sname")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

4.设置核心配置文件

<?xml version="1.0" encoding="UTF-8" ?>
<orm-factory>
    <property name="url">jdbc:sqlserver://127.0.0.1:1433;databasename=stu1</property>
    <property name="driver">com.microsoft.sqlserver.jdbc.SQLServerDriver</property>
    <property name="username">账号名</property>
    <property name="password">数据库密码</property>

    <mapping resource="entity/Demo.mapper.xml"/>//如果用的是xml配置则配置此行
    <entity package="entity"/>//如果用的是注解方式，则配置此行
</orm-factory>
三、插入（session.insert(String))

1.使用方法

例：

 session.insert("insert into Student values('10','12','2','女')");//values的值必须与数据库中表的属性一一对应
四、删除（session.delete(object))

1.设置一个类，将条件属性加上@Id注解，删除前给有@Id属性的注解赋好值，则属性（有@Id注解） = 值将成为删除的条件。

package entity;

import annotation.Column;
import annotation.Id;
import annotation.Table;

import java.util.Arrays;
import java.util.HashMap;

@Table(name = "Student")
public class Stu {

    @Column(name="name")
    private String name;
    @Column(name = "age")
    private int age;
    
    @Column(name = "SId")
    @Id
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String[] queryFileds;
    private HashMap mapper;

    public HashMap getMapper() {
        return mapper;
    }

    public void setMapper(HashMap mapper) {
        this.mapper = mapper;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setQueryFileds(String...args){
        queryFileds = args;
    }
    public String[] getQueryFileds(){
        return queryFileds;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                ", queryFileds=" + Arrays.toString(queryFileds) +
                ", mapper=" + mapper +
                '}';
    }
}

Stu stu = new Stu();
stu.setId(1);
session.delete(stu);
五、删除（session.delete(String sql))

 session.delete("delete from Student where SId = 2");
六、查找（session.selectId(Object entity))

1.设置一个对象，将条件属性设置@Id注解，给该属性赋值，则该属性 = 值则是查找条件。返回一个类型为跟entity类型相同的集合。

package entity;

import annotation.Column;
import annotation.Id;
import annotation.Table;

import java.util.Arrays;
import java.util.HashMap;

@Table(name = "Student")
public class Stu {

    @Column(name="Sname")
    private String name;
    @Column(name = "Sage")
    private int age;

    @Column(name = "SId")
    @Id
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String[] queryFileds;
    private HashMap mapper;

    public HashMap getMapper() {
        return mapper;
    }

    public void setMapper(HashMap mapper) {
        this.mapper = mapper;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setQueryFileds(String...args){
        queryFileds = args;
    }
    public String[] getQueryFileds(){
        return queryFileds;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                ", queryFileds=" + Arrays.toString(queryFileds) +
                ", mapper=" + mapper +
                '}';
    }
}

    List<Object> list= new ArrayList<Object>();
    Stu stu = new Stu();
    stu.setId(2);
    list = session.select(stu);
七、查找（session.select(Object obj))

1.使用注解标记查找条件

Stu.java:

package entity;

import annotation.Column;
import annotation.Id;
import annotation.Select;
import annotation.Table;

import java.util.Arrays;
import java.util.HashMap;

@Table(name = "Student")
public class Stu {

    @Select//添加此行
    @Column(name="Sname")
    private String name;

    @Column(name = "Sage")
    private int age;

    @Column(name = "SId")
    @Id
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String[] queryFileds;

    private HashMap mapper;

    public HashMap getMapper() {
        return mapper;
    }

    public void setMapper(HashMap mapper) {
        this.mapper = mapper;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setQueryFileds(String...args){
        queryFileds = args;
    }
    public String[] getQueryFileds(){
        return queryFileds;
    }

    @Override
    public String toString() {
        return "Stu{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                ", queryFileds=" + Arrays.toString(queryFileds) +
                ", mapper=" + mapper +
                '}';
    }
}

2.使用xml配置

Stu.mapper.xml:

<?xml version="1.0" encoding="UTF-8" ?>
        <orm-mapping>
            <class name="entity.Stu" table="Student">
                <id name="id" column="SId"/>
                <select name = "name" column="Sname"/>//select标签里的字段和值将成为查找条件
                <property name="age" column="Sage"/>
            </class>
</orm-mapping>
3.使用

    List<Object> list = new ArrayList<Object>();
    Stu stu = new Stu();
    stu.setName("wqwq");//查找条件：Sname = 'wqwq'
    list = session.select(stu);
八、查找（session.select(String sql))

list = session.select(stu,"select * from Student where SId = '2'" );
九、修改（session.update(String tableName,String setSql,String conditionSql))

参数分别是表名、修改语句、条件语句

session.update("Student","Sname = 'ss',Sage = 18","Sname = '李云'");
十、修改（session.update(Object entity,String condition)

 Stu stu = new Stu();
 stu.setId(2);
 session.update(stu,"SId = '10'");//根据stu里@Id注解过的属性值来进行修改
十一、修改（session.update(String sql))

session.update("update Student set SId = 1 where SId = 2");
