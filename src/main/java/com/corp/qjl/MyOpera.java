package com.corp.qjl;

import java.util.List;
import java.util.Map;

public class MyOpera {

    public static void main(String[] args) throws Exception {

        //将所有人名转化为结巴的用户字典
//        String namePath = "name.xml";
//        List<String> names = Utils.readNames(namePath);
//        Utils.convertNamesToJiebaDic(namePath, "dict.txt");

        String documentsPath = "爬虫.xml";
        //key是人名，value是人物介绍
        Map<String, String> documents = Utils.readDiss(documentsPath);

        String entityPath = "命名实体识别.xml";
        Map<String, List<String>> referedNames = Utils.extractReferedNames(entityPath);
        Utils.extractRelationship(referedNames, documents);

    }




}
