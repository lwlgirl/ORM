package Test;

import core.Config;
import core.Session;
import entity.Demo;
import entity.Stu;
import entity.Stu1;
import org.junit.Test;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Test1 {
    @Test
    public  void test() throws SQLException, ClassNotFoundException, IllegalAccessException, NoSuchFieldException, InstantiationException {

        //解析核心配置文件，生成session
        Config config = new Config();
        Session session = config.buildSession();

        Demo demo = new Demo();
        //建表
        session.createTable(demo.getClass());

        Stu stu = new Stu();
        stu.setAge(18);
        stu.setName("lwl");
        stu.setId(1);
        //插入,两张插入方式
//        session.insert(stu);
//        session.insert("insert into Demo01 values( 2,'lwl1',20)");

        List<Object> list = new ArrayList<Object>();
        //查询，三种查询方式
//        list = session.select(stu,"select * from Demo01 where Sid=1");
//        list = session.select(stu);
//        list = session.selectId(stu);
        System.out.println("size="+list.size());

        //修改
//        session.update("update Demo01 set Sid = 2 where Sname = 'lwl'");
//        session.update("Demo01","Sid = 1","Sname = 'lwl'");
//        session.update(stu,"Sname = 'lwl1'");

        //删除
//        session.delete(stu);
//        session.delete("delete from Demo01 where Sid = 2");

    }
}
