package com.corp.qjl;
import org.dom4j.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MyOpera {

    public static void main(String[] args) throws DocumentException, IOException {

//        String namePath = "name.xml";
//        List<String> names = Utils.readNames(namePath);

        String documentsPath = "爬虫.xml";
        Map<String, String> documents = Utils.readDiss(documentsPath);
//        Utils.extractNames(documents, names);

        String entityPath = "命名实体识别.xml";
        Map<String, List<String>> referedNames = Utils.extractReferedNames(entityPath);
        Utils.extractRelationship(referedNames, documents);

    }




}
