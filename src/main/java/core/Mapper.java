package core;

import java.util.HashMap;
import java.util.Map;

//存储映射信息
public class Mapper {
    //类名
    private String className;

    //表名
    private String tableName;

    //主键信息
    private Map<String,String> idMap = new HashMap<String,String>();

    private  Map<String,String> selectMap = new HashMap<String, String>();

    public Map<String, String> getSelectMap() {
        return selectMap;
    }

    public void setSelectMap(Map<String, String> selectMap) {
        this.selectMap = selectMap;
    }

    //其他属性信息
    private Map<String,String> propMap = new HashMap<String, String>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getIdMap() {
        return idMap;
    }

    public void setIdMap(Map<String, String> idMap) {
        this.idMap = idMap;
    }

    public Map<String, String> getPropMap() {
        return propMap;
    }

    public void setPropMap(Map<String, String> propMap) {
        this.propMap = propMap;
    }

    @Override
    public String toString() {
        return "Mapper{" +
                "className='" + className + '\'' +
                ", tableName='" + tableName + '\'' +
                ", idMap=" + idMap +
                ", propMap=" + propMap +
                '}';
    }
}
