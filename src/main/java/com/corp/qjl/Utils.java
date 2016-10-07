package com.corp.qjl;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.ArrayCoreMap;
import edu.stanford.nlp.util.CoreMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/10/5.
 */
class Utils {

    //人物关系抽取
    static void extractRelationship(Map<String, List<String>> referedNames, Map<String, String> documents) throws IOException {
        //DocumentHelper提供了创建Document对象的方法
        Document document = DocumentHelper.createDocument();
        //添加节点信息
        Element xml_root = document.addElement("relations");

//        int a = 0;

        //处理每一段文档
        for(String name: documents.keySet()) {
            String doc = documents.get(name).replaceAll("\n", "。");
//            if(a == 10)
//                break;
//            a++;

            System.out.println();
            System.out.println(doc);

            List<String> refers = referedNames.get(name);

            String[] my_sentences = doc.split("[.]|[!?]+|[。]|[！？]+|[，]");

            for(int i=0; i<my_sentences.length; i++) {
                String my_sentence = my_sentences[i];
                if(my_sentence.equals(""))
                    continue;

                Pattern teacher = Pattern.compile(".*师从.*|.*从师.*|.*向.*学.*|.*受教于.*|.*拜.*|.*老师.*|" +
                        ".*被.*收为.*|.*传授.*|.*为师.*|.*得.*真传.*|.*为.*弟子.*");
                Matcher match_teacher = teacher.matcher(my_sentence);
                if(match_teacher.matches()) {
                    for(String refer: refers) {
                        if(my_sentence.contains(refer)) {
                            Element xml_relation = xml_root.addElement("relation");
                            Element xml_name1 = xml_relation.addElement("name1");
                            xml_name1.setText(refer);
                            Element xml_type = xml_relation.addElement("type");
                            xml_type.setText("师徒");
                            Element xml_name2 = xml_relation.addElement("name2");
                            xml_name2.setText(name);
                        }
                    }
                    System.out.println("本句话中有师傅：" + my_sentence);
                }

                Pattern student = Pattern.compile(".*收.*为.*|.*学员.*|.*培养.*");
                Matcher match_student = student.matcher(my_sentence);
                if(match_student.matches()) {
                    for(String refer: refers) {
                        if(my_sentence.contains(refer)) {
                            Element xml_relation = xml_root.addElement("relation");
                            Element xml_name1 = xml_relation.addElement("name1");
                            xml_name1.setText(name);
                            Element xml_type = xml_relation.addElement("type");
                            xml_type.setText("师徒");
                            Element xml_name2 = xml_relation.addElement("name2");
                            xml_name2.setText(refer);
                        }
                    }
                    System.out.println("本句话中有徒弟："+ my_sentence);
                }

                Pattern teach_student = Pattern.compile(".*培养.*");
                Matcher match_teach_student = teach_student.matcher(my_sentence);
                if(match_teach_student.matches()) {
                    for(String refer: refers) {
                        if(my_sentences[i+1].contains(refer)) {
                            Element xml_relation = xml_root.addElement("relation");
                            Element xml_name1 = xml_relation.addElement("name1");
                            xml_name1.setText(name);
                            Element xml_type = xml_relation.addElement("type");
                            xml_type.setText("师徒");
                            Element xml_name2 = xml_relation.addElement("name2");
                            xml_name2.setText(refer);
                        }
                    }
                    System.out.println("下句话中有徒弟：" + my_sentence);
                }

            }
        }
        Writer fileWriter = new FileWriter("实体关系抽取.xml");
        XMLWriter xmlWriter = new XMLWriter(fileWriter);
        xmlWriter.write(document);
        xmlWriter.flush();
        xmlWriter.close();

    }

    static Map<String, List<String>> extractReferedNames(String path) throws DocumentException {
        Map<String, List<String>> referedNames = new HashMap<String, List<String>>();
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element root = document.getRootElement();

        for (Iterator i = root.elementIterator("doc"); i.hasNext();) {
            Element doc = (Element) i.next();
            String name = doc.attributeValue("name");
            List<String> refers = new ArrayList<String>();
            for (Iterator j = doc.elementIterator("refer"); j.hasNext();) {
                Element refer = (Element) j.next();
                refers.add(refer.getText());
            }
            referedNames.put(name, refers);
        }
        return referedNames;
    }


    //命名实体识别
    static void extractNames(Map<String, String> documents, List<String> names) throws IOException {
        //DocumentHelper提供了创建Document对象的方法
        Document document = DocumentHelper.createDocument();
        //添加节点信息
        Element xml_root = document.addElement("documents");

        for (String person : documents.keySet()) {
            String doc = documents.get(person);
            Element xml_doc = xml_root.addElement("doc");
            xml_doc.addAttribute("name", person);
            for(String name: names) {
                if(name.equals(person))
                    continue;
                if(doc.contains(name)) {
                    Element xml_name = xml_doc.addElement("refer");
                    xml_name.setText(name);
                }
            }
        }
        Writer fileWriter = new FileWriter("命名实体识别.xml");
        XMLWriter xmlWriter = new XMLWriter(fileWriter);
        xmlWriter.write(document);
        xmlWriter.flush();
        xmlWriter.close();
    }

    static List<String> readNames(String path) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element root = document.getRootElement();
        String tag_name = "name";
        List<String> names = new ArrayList<String>();
        for (Iterator i = root.elementIterator(tag_name); i.hasNext();) {
            Element name = (Element) i.next();
            names.add(name.getText());
        }
        return names;
    }

    static Map<String, String> readDiss(String path) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(path));
        Element root = document.getRootElement();
        String tag_person = "person";
        Map<String, String> discriptions = new HashMap<String, String>();
        for (Iterator i = root.elementIterator(tag_person); i.hasNext();) {
            Element person = (Element) i.next();
            Element name = person.element("name");
            Element dis = person.element("dis");
            discriptions.put(name.getText(), dis.getText());
        }
        return discriptions;
    }
}
