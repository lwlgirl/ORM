package utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.print.Doc;
import java.io.File;
import java.util.*;


public class Dom4jUtil {
//    通过文件的路径获取xml的document对象
    public static Document getXMLDocument(String path) {
        System.out.println("path:"+path);
        if(null == path){
            return null;
        }
        Document doc = null;
        try {
            SAXReader reader = new SAXReader();
            doc = reader.read(new File(path));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return doc;
    }

    //获得文档中某元素内某属性的值和元素的文本信息
    public static Map<String,String> ElementsMap(Document doc, String elemName, String proprName){
        List<Element> propList = doc.getRootElement().elements(elemName);//得到根标签
        Map<String,String> propConfig = new HashMap<String, String>();
        for (Element element:propList){
            String key = element.attribute(proprName).getValue();
            String value = element.getTextTrim();
            propConfig.put(key,value);
        }
        return propConfig;
    }

    //针对mapper.xml文件，获得映射信息并存到Map集合中
    public static Map<String,String> ElementsMap(Document doc){
        Element classElement = doc.getRootElement().element("class");
        Map<String,String> mapping = new HashMap<String, String>();

        //把id的映射信息放入mapping
        List<Element> idElements = classElement.elements("id");
        for(Element element:idElements){
            String idKey = element.attribute("name").getValue();
            String idValue = element.attribute("column").getValue();
            mapping.put(idKey,idValue);
        }
        //把普通属性的映射信息放入mapping
        List<Element> propElements = classElement.elements("property");
        for(Element element : propElements){
            String propKey = element.attribute("name").getValue();
            String propValue = element.attribute("column").getValue();
            mapping.put(propKey,propValue);
        }
        return mapping;
    }

    //获取id信息
    public static Map<String,String> ElementsIDMap(Document doc){
        Element classElement = doc.getRootElement().element("class");
        Map<String,String> mapping = new HashMap<String, String>();

        //把id的映射信息放入mapping
        List<Element> idElements = classElement.elements("id");
        for(Element element:idElements){
            String idKey = element.attribute("name").getValue();
            String idValue = element.attribute("column").getValue();
            mapping.put(idKey,idValue);
        }
        return mapping;
    }

    //获取带有@Select注解的属性和对应的字段
    public static Map<String,String> ElementsSelectMap(Document doc){
        Element classElement = doc.getRootElement().element("class");
        Map<String,String> mapping = new HashMap<String, String>();

        //把select的映射信息放入mapping
        List<Element> selectElements = classElement.elements("select");
        for(Element element:selectElements){
            String Key = element.attribute("name").getValue();
            String Value = element.attribute("column").getValue();
            mapping.put(Key,Value);
        }
        return mapping;
    }

    //获取某文档中某元素内某属性的值
    public static Set<String> ElementsSet(Document doc, String elemName, String propName){
        List<Element> mappingList = doc.getRootElement().elements(elemName);
        Set<String> mappingSet = new HashSet<String>();
        for(Element element:mappingList){
            String value = element.attribute(propName).getValue();
            mappingSet.add(value);
        }
        return mappingSet;
    }

    //获取某文档中某元素内某属性的值
    public static String getPropValue(Document doc,String elemName,String propName){
        Element element = (Element) doc.getRootElement().elements(elemName).get(0);
        return element.attribute(propName).getValue();
    }
}
