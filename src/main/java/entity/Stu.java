package entity;

import annotation.Column;
import annotation.Id;
import annotation.Select;
import annotation.Table;

import java.util.Arrays;
import java.util.HashMap;

@Table(name = "Demo01")
public class Stu {

    @Select
    @Column(name="Sname")
    private String name;

    @Column(name = "Sage")
    private int age;

    @Id
    @Column(name = "SId")
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
