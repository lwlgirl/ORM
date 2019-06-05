package entity;

import annotation.*;

@Table(name = "Demo01")
public class Demo {

    @SQLInteger(name = "Sid")
    private int id;

    @SQLString(name = "Sname")
    private String name;

    @SQLInteger(name = "Sage")
    private  int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

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
