package Test;

import entity.Stu;

import java.util.HashMap;

interface Base{

}

public interface Test {
    public Base query(Object o);
    public Boolean insert();
    public Boolean delete();
    public Boolean update();
}

class Test2 implements Test{

    public Base query(Object o) {
        return null;
    }

    public Boolean insert() {
        return null;
    }

    public Boolean delete() {
        return null;
    }

    public Boolean update() {
        return null;
    }

    public static void main(String[] args) {
        Stu stu = new Stu();
        stu.setQueryFileds("name","age");
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        map.put("name",new String[]{"张三", "李四"});
        stu.setMapper(map);
    }
}