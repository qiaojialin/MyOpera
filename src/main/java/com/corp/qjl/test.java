package com.corp.qjl;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/10/5.
 */
public class test {
    public static void main(String[] args) throws IOException {
        String doc = "刘德一出生：1945年1月，农历乙酉年？逝世：20行演员\n刘德一，男，川剧丑角。" +
                "1975年考入重庆市川剧院训练班学！艺。曾主演《凌\n汤圆》《三喜临门》、《".replaceAll("\n", "。");
        List<String> a = new ArrayList<String>();
        a.add("师从李盛佐、阎世喜。");
        a.add("1980年到中国京剧院进修，先后从师李可先生、茹元俊先生，剧目有：《杀四门》、《夜奔》、《武文华》。");
        a.add("后向天津著名武生苏德贵先生学习《艳阳楼》、《一箭仇》、《雅观楼》、《锺馗嫁妹》等剧。");
        a.add("还曾受教于李万春等");
        a.add("先后拜常宝臣、朱阔泉为师。");
        a.add("启蒙老师为郭玉芹");
        a.add("1995年被余派专家从鸿逵先生收为弟子。");
        a.add("2000年拜尤继舜老师门下，1999年至2004年得到孟小冬弟子蔡国蘅先生的余派唱腔技巧的传授，1999年至2005年与余派坤生王珮瑜合作，被业内外公认的绝配合作搭档。");
        a.add("");
        for(String b: a) {
            Pattern teacher = Pattern.compile(".*师从.*|.*从师.*|.*学.*|.*受教于.*|.*拜.*|.*老师.*|" +
                    ".*被.*收为.*|.*传授.*|.*为师.*|.*得.*真传.*|.*为.*弟子.*");
            Pattern student = Pattern.compile(".*收.*为.*|.*学员.*");
            Pattern teach_student = Pattern.compile(".*培养.*");
            Matcher matcher = teacher.matcher(b);
            if (matcher.matches()) {
                System.out.println(b);
            }
        }
    }
}
