package com.ruoyi.system.utils;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;
import com.ruoyi.common.core.redis.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/test")
public class Test {
    private static JiebaSegmenter segmenter = new JiebaSegmenter();
    private static String sentences = "我时常感觉到焦虑不安，恐惧害怕，而且伴有食欲不佳、体重下降等症状，我的认知功能出现问题";

    private static String[] words = {
            "乏力",
            "低声细语",
            "体重减轻",
            "体重降低",
            "便秘",
            "健忘",
            "全身性酸痛乏力",
            "兴趣丧失",
            "厌烦情绪",
            "反复往自我贬低与谴责",
            "反应缓慢",
            "周围环境漠不关心",
            "失望感",
            "妄想",
            "季节性情绪抑郁",
            "害怕",
            "幻觉或妄想",
            "强颜欢笑",
            "影响睡眠",
            "心境低落",
            "心情压抑",
            "怀疑自己能力",
            "思维活动受限",
            "思维迟缓",
            "思维迟钝",
            "恐慌",
            "悲观厌世情绪",
            "悲观失望",
            "情绪不稳易激动",
            "情绪低落",
            "情绪失调",
            "情绪问题",
            "意志活动减退",
            "意志活动抑制",
            "抑郁发作",
            "易激惹",
            "注意力不集中",
            "测试症状",
            "测试症状1",
            "混合发作",
            "焦虑",
            "焦虑不安",
            "疲倦",
            "精力不足",
            "精神活动减低",
            "精神症状",
            "终日忧心忡忡",
            "绝望",
            "缺乏信心",
            "缺乏动力",
            "缺乏愉快感",
            "老人性格孤僻",
            "自卑",
            "自尊心减低",
            "自我评价过低",
            "自杀企图",
            "自觉无用感",
            "落泪",
            "行为问题",
            "被害妄想",
            "认知功能损害",
            "躁狂发作",
            "身体功能减退",
            "躯体症状",
            "退离休综合症",
            "食欲低下",
            "食欲不佳",
            "食欲不振",
            "食欲降低",
            "没有食欲",
            "食欲",
    };

    private static String[] punctuationMarks = {
            ",",
            ".",
            " ",
            "?",
            "!",
            "，",
            "。",
            "、",
    };

    private static Set<String> wordSet = new HashSet<>();

    private static Set<String> punctuationMarksSet = new HashSet<>();

    private static Map<String,String> map = new HashMap<>();

    static {
        WordDictionary.getInstance().init(Paths.get("E:\\data\\GraduateDesign\\RuoYi-Vue-master\\ruoyi-system\\conf"));

        for (String word : words) {
            wordSet.add(word);
        }

        for (String punctuationMark : punctuationMarks) {
            punctuationMarksSet.add(punctuationMark);
        }


    }

    static {
        map.put("乏力","乏力");
        map.put("没劲","乏力");

        map.put("低声细语","低声细语");

        map.put("体重减轻","体重减轻");
        map.put("体重降低","体重减轻");

        map.put("便秘","便秘");
        map.put("健忘","健忘");

        map.put("记性差","健忘");
        map.put("经常忘事","健忘");

        map.put("全身性酸痛乏力","全身性酸痛乏力");
        map.put("浑身没劲","全身性酸痛乏力");
        map.put("浑身酸痛","全身性酸痛乏力");

        map.put("兴趣丧失","兴趣丧失");
        map.put("不感兴趣","兴趣丧失");
        map.put("没兴趣","兴趣丧失");
        map.put("失去兴趣","兴趣丧失");
        map.put("兴趣","兴趣丧失");

        map.put("厌烦情绪","厌烦情绪");
        map.put("讨厌","厌烦情绪");
        map.put("厌烦","厌烦情绪");

        map.put("反复往自我贬低与谴责","反复往自我贬低与谴责");
        map.put("自我贬低","反复往自我贬低与谴责");
        map.put("贬低","反复往自我贬低与谴责");
        map.put("谴责","反复往自我贬低与谴责");

        map.put("反应缓慢","反应缓慢");
        map.put("反应迟钝","反应缓慢");
        map.put("不灵光","反应缓慢");

        map.put("周围环境漠不关心","周围环境漠不关心");

        map.put("失望感","失望感");
        map.put("失望","失望感");

        map.put("妄想","妄想");

        map.put("季节性情绪抑郁","季节性情绪抑郁");

        map.put("害怕","害怕");

        map.put("幻觉或妄想","幻觉或妄想");
        map.put("幻觉","幻觉或妄想");

        map.put("强颜欢笑","强颜欢笑");

        map.put("影响睡眠","影响睡眠");
        map.put("睡眠不好","影响睡眠");
        map.put("失眠","影响睡眠");
        map.put("睡不着","影响睡眠");
        map.put("睡眠障碍","影响睡眠");

        map.put("心境低落","心境低落");
        map.put("低落","心境低落");

        map.put("心情压抑","心情压抑");
        map.put("压抑","心情压抑");

        map.put("怀疑自己能力","怀疑自己能力");
        map.put("自我怀疑","怀疑自己能力");

        map.put("思维活动受限","思维活动受限");
        map.put("思维迟缓","思维迟缓");
        map.put("思维迟钝","思维迟钝");

        map.put("恐慌","恐慌");

        map.put("悲观厌世情绪","悲观厌世情绪");

        map.put("悲观失望","悲观失望");
        map.put("悲观","悲观失望");
        map.put("失望","悲观失望");

        map.put("情绪不稳易激动","情绪不稳易激动");
        map.put("情绪不稳定","情绪不稳易激动");
        map.put("易激动","情绪不稳易激动");
        map.put("激动","情绪不稳易激动");


        map.put("情绪低落","情绪低落");
        map.put("情绪失落","情绪低落");
        map.put("情绪不好","情绪低落");
        map.put("心情不好","情绪低落");
        map.put("心情失落","情绪低落");
        map.put("心情低落","情绪低落");
        map.put("沮丧","情绪低落");
        map.put("没心情","情绪低落");
        map.put("没啥心情","情绪低落");
        map.put("没什么心情","情绪低落");
        map.put("没有心情","情绪低落");

        map.put("情绪失调","情绪失调");

        map.put("情绪问题","情绪问题");

        map.put("意志活动减退","意志活动减退");

        map.put("意志活动抑制","意志活动抑制");


        map.put("抑郁发作","抑郁发作");

        map.put("易激惹","易激惹");
        map.put("激惹","易激惹");

        map.put("注意力不集中","注意力不集中");
        map.put("注意力","注意力不集中");

        map.put("测试症状","测试症状");
        map.put("测试症状1","测试症状1");

        map.put("混合发作","混合发作");

        map.put("焦虑","焦虑");
        map.put("焦虑不安","焦虑");

        map.put("疲倦","疲倦");
        map.put("疲惫","疲倦");
        map.put("累","疲倦");

        map.put("精力不足","精力不足");

        map.put("精神活动减低","精神活动减低");

        map.put("精神症状","精神症状");

        map.put("终日忧心忡忡","终日忧心忡忡");
        map.put("忧心忡忡","终日忧心忡忡");
        map.put("担忧","终日忧心忡忡");
        map.put("担心","终日忧心忡忡");

        map.put("绝望","绝望");

        map.put("缺乏信心","缺乏信心");
        map.put("不自信","缺乏信心");

        map.put("缺乏动力","缺乏动力");
        map.put("没动力","缺乏动力");
        map.put("动力","缺乏动力");
        map.put("缺乏活力","缺乏动力");
        map.put("没活力","缺乏动力");
        map.put("活力","缺乏动力");

        map.put("缺乏愉快感","缺乏愉快感");
        map.put("不开心","缺乏愉快感");
        map.put("不快乐","缺乏愉快感");
        map.put("不高兴","缺乏愉快感");

        map.put("老人性格孤僻","老人性格孤僻");
        map.put("性格孤僻","老人性格孤僻");

        map.put("自卑","自卑");

        map.put("自尊心减低","自尊心减低");
        map.put("自尊心","自尊心减低");

        map.put("自我评价过低","自我评价过低");
        map.put("自我评价","自我评价过低");

        map.put("自杀企图","自杀企图");
        map.put("自杀","自杀企图");

        map.put("自觉无用感","自觉无用感");
        map.put("无用","自觉无用感");

        map.put("落泪","落泪");
        map.put("流泪","落泪");
        map.put("流眼泪","落泪");
        map.put("眼泪","落泪");

        map.put("行为问题","行为问题");

        map.put("被害妄想","被害妄想");

        map.put("认知功能损害","认知功能损害");

        map.put("躁狂发作","躁狂发作");
        map.put("躁狂","躁狂发作");
        map.put("狂躁","躁狂发作");

        map.put("身体功能减退","身体功能减退");

        map.put("躯体症状","躯体症状");
        map.put("身体不适","躯体症状");
        map.put("身体不舒服","躯体症状");
        map.put("不舒服","躯体症状");

        map.put("退离休综合症","退离休综合症");

        map.put("食欲低下","食欲低下");
        map.put("食欲不佳","食欲低下");
        map.put("食欲不振","食欲低下");
        map.put("食欲降低","食欲低下");
        map.put("没有食欲","食欲低下");
        map.put("食欲","食欲低下");
    }

    public static void main(String[] args) {
        sentences =
                "身体不适，感觉不舒服，比较焦虑，心情不好，比较低落，没啥动力，也没心情";
        List<String> strings = segmenter.sentenceProcess(sentences);
        for (String str : strings) {
            // 跳过标点符号
            if(punctuationMarksSet.contains(str)){
                continue;
            }

            if(map.keySet().contains(str)){
                System.out.println(str + " ============= " + map.get(str));
            }else {
                System.out.println(str);
            }


        }


//        System.out.println(map.keySet().size());
//
//        String filePath = "ruoyi-system/conf/user.dict";
//
//        // 先清空文件内容
//        clearFile(filePath);
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
//            for (String str : map.keySet()) {
//                // 拼接词频
//                str+=" 3";
//                writer.write(str);
//                writer.newLine(); // 每写入一个字符串后换行
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Autowired
    private RedisCache redisCache;

    @GetMapping("/word")
    public void importWordToRedis(){
        System.out.println("开始删除redis原有数据");
        redisCache.redisTemplate.delete("segmentWord");

        System.out.println("开始插入新数据");
        redisCache.redisTemplate.opsForHash().putAll("segmentWord",map);

        System.out.println("词库共:" + map.keySet().size() + "个词语");

        String filePath = "ruoyi-system/conf/user.dict";

        System.out.println("开始清空源词典");
        // 先清空文件内容
        clearFile(filePath);

        System.out.println("开始写入新词典");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            int count = 0;
            for (String str : map.keySet()) {
                System.out.println("正在写入:" + str + " == index " + count++);
                // 拼接词频
                str+=" 3";
                writer.write(str);
                writer.newLine(); // 每写入一个字符串后换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            // 清空文件内容
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public List<String> orderWords(){
        Arrays.sort(words);

        for (String word : words) {
            System.out.println(word + " 3");
        }

        return null;
    }

}
