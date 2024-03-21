package com.ruoyi.system.utils.dataBuilder;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Test {

    public static Set<String> jiBingSet = new HashSet<>();
    public static Set<String> 症状 = new HashSet<>();
    public static Set<String> 并发症 = new HashSet<>();
    public static Map<String,Map> jiBing = new HashMap<>();

    public static Map<String,Set<String>> jiBingZhengZhuang = new HashMap<>();
    public static Map<String,Set<String>> jiBingBingFaZheng = new HashMap<>();

    public static void main(String[] args) {
        //获取resource下的文件
        File file = new File("E:\\download\\test1.html");
        //解析 HTML 数据，使用Jsoup的parse()方法，解析 HTML 字符串。 该方法返回一个 HTML 文档
        Document doc = null;
        try {
            doc = Jsoup.parse(file, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements select = doc.select(".result_item");


        Map<String, List<String>> map = new HashMap<>();


        for (Element element : select) {
            //System.out.println("result_item=====================");
            //System.out.println(element);

            Elements p = element.select(".result_item_top").select("p");
            for (Element elementP : p) {
                String title = elementP.select("a").attr("title");
//                System.out.println("title = " + title);

                String href = elementP.select("a").attr("href");
//                System.out.println("href = " + href);
                List<String> list = map.getOrDefault(title, new ArrayList<>());
                list.add(href);
                map.put(title,list);
            }
        }

        List<String> bingYin = map.get("病因");
        bingYin.addAll(map.get("症因"));

        Set<String> jiBing = map.keySet();

        Set<String> bingYinSet = new HashSet<>();


        String str = "{\"成功后抑郁症\":[\"https://jbk.39.net/cghyyz/\"],\"产后抑郁症\":[\"https://jbk.39.net/chyyz/\"],\"反复发作抑郁症\":[\"https://jbk.39.net/fffzyyz/\"],\"老年抑郁症\":[\"https://jbk.39.net/lnyyz/\"],\"老年期抑郁症\":[\"https://jbk.39.net/lnqyyz/\"],\"绝经与抑郁症\":[\"https://jbk.39.net/jjyyyz/\"],\"躁郁症\":[\"https://jbk.39.net/zkyyz/\"],\"抑郁症\":[\"https://jbk.39.net/yyz/\"],\"微笑抑郁症\":[\"https://jbk.39.net/wxyyz/\"],\"季节性情绪抑郁\":[\"https://jbk.39.net/zhengzhuang/jjxqxyy/\"],\"单次发作抑郁症\":[\"https://jbk.39.net/dcfzyyz/\"]}";
        map = JSONUtil.toBean(str,Map.class);

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " === " + entry.getValue());
            jiBingSet.add(entry.getKey());
            if(entry.getKey().equals("季节性情绪抑郁")){
                continue;
            }
            test1(entry.getKey(),map.get(entry.getKey()).get(0));
        }

        System.out.println(jiBingZhengZhuang);
        System.out.println(jiBingBingFaZheng);
        System.out.println(jiBingSet);
        String jsonStr = JSONUtil.toJsonStr(jiBingZhengZhuang);
        String jsonStr1 = JSONUtil.toJsonStr(jiBingBingFaZheng);

        System.out.println("============症状");
        System.out.println(jsonStr);
        for (String s : 症状) {
            System.out.println(s);
        }
        System.out.println("============并发症");
        System.out.println(jsonStr1);
        for (String s : 并发症) {
            System.out.println(s);
        }

    }

    public static void test1(String jiBingName, String urlStr) {
        String url = urlStr; // 指定要爬取的网址
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> href = getHref(doc);
        System.out.println(href);

        jiBing.put(jiBingName,href);

        getJiBingZhangZhuang(jiBingName,href.get("症状"));
        getJiBingBingFaZheng(jiBingName,href.get("并发症"));
    }

    public static Map<String,String> getHref(Document doc){
        Elements select = doc.select(".navigation_ul");
//        System.out.println(select);
        Elements li = select.select("li");

        Map<String,String> res = new HashMap<>();

        for (Element element : li) {
            // 获取li元素内的<a>标签
            Element aElement = element.select("a").first();
            String link = aElement.attr("href");
            String text = aElement.text();

            System.out.println("Link: " + link);
            System.out.println("Text: " + text);
            res.put(text,link);
        }

        return res;
    }

    // 提取症状
    public static void getJiBingZhangZhuang(String jiBingName, String url){
        System.out.println(jiBingName + "==症状==" + url);

        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

            Elements select = doc.select(".article_text");

            //System.out.println(select);
            Set<String> zhengZhuang = new HashSet<>();
            for (Element element : select) {
                String s = element.ownText();
                System.out.println(s);
                for (String s1 : s.split("、")) {
                    症状.add(s1);
                    zhengZhuang.add(s1);
                }
            }

            System.out.println(zhengZhuang);
            jiBingZhengZhuang.put(jiBingName,zhengZhuang);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // 并发症
    public static void getJiBingBingFaZheng(String jiBingName, String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();

            Elements select = doc.select(".article_box").select(".article_text");
//        System.out.println(select);
            Elements a = select.select("a");
//        System.out.println(a);
            Set<String> bingfazheng = new HashSet<>();
            for (Element element : a) {
                并发症.add(element.attr("title"));
                bingfazheng.add(element.attr("title"));
            }
            System.out.println(bingfazheng);
            jiBingBingFaZheng.put(jiBingName,bingfazheng);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
