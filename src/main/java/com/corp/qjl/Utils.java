package com.corp.qjl;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qiaojialin on 2016/10/5.
 */
class Utils {

    //师徒关系抽取
    static void extractRelationship(Map<String, List<String>> referedNames, Map<String, String> documents) throws IOException {
        //DocumentHelper提供了创建Document对象的方法
        Document document = DocumentHelper.createDocument();
        //添加节点信息
        Element xml_root = document.addElement("relations");

        //处理每一段文档
        for(String name: documents.keySet()) {
            String doc = documents.get(name).replaceAll("\n", "。");

            System.out.println();
            System.out.println(doc);

            List<String> refers = referedNames.get(name);
            String[] my_sentences = doc.split("[.]|[!?]+|[。]|[！？]+|[，]");

            for(int i=0; i<my_sentences.length; i++) {
                String my_sentence = my_sentences[i];
                if(my_sentence.equals(""))
                    continue;

                Pattern teacher = Pattern.compile(".*师从.*|.*从师.*|.*向.*学.*|.*受教于.*|.*拜.*|.*老师.*|" +
                        ".*被.*收为.*|.*传授.*|.*为师.*|.*得.*真传.*|.*为.*弟子.*|.*受.*指导.*");
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
                    System.out.println(my_sentence);
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

            }
        }
        Writer fileWriter = new FileWriter("实体关系抽取.xml");
        XMLWriter xmlWriter = new XMLWriter(fileWriter);
        xmlWriter.write(document);
        xmlWriter.flush();
        xmlWriter.close();

    }

    //将命名实体识别xml中的信息提取出来构造成一个name-list<refer>的map结构
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


    //将所有京剧名家姓名转化为jieba用户字典
    static void convertNamesToJiebaDic(String sourcePath, String desPath) throws DocumentException, IOException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(sourcePath));
        Element root = document.getRootElement();
        String tag_name = "name";
        FileWriter writer = new FileWriter(desPath);
        for (Iterator i = root.elementIterator(tag_name); i.hasNext();) {
            Element name = (Element) i.next();
            String name_text = name.getText();
            writer.write(name_text + " " + "nr\n");
        }
        writer.close();
    }

    static Map<String, String> readFileFolder(String path) throws Exception {

        Map<String, String> documents = new HashMap<String, String>();
        File folder = new File(path);
        File[] files = folder.listFiles();
        for(File file: files) {
            String name = file.getName().substring(0, file.getName().indexOf('.'));
            String text = "";
            InputStreamReader read = new InputStreamReader(new FileInputStream(file), "GBK");//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                text += lineTxt;
            }
            read.close();
            documents.put(name, text);
        }
        return documents;
    }


    //读取所有爬虫爬到的内容，将结果保存到一个name-dis的map
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
