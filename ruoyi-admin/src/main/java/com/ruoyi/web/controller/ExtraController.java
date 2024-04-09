package com.ruoyi.web.controller;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.req.ExtraReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/extra")
public class ExtraController {
    private static JiebaSegmenter segmenter = new JiebaSegmenter();
    private static final String SegmentWordKey = "segmentWord";
    private static Set<String> punctuationMarksSet = new HashSet<>();
    // 标点符号集合
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
    @Autowired
    private RedisCache redisCache;

    // 静态代码块，加载资源
    static {
        WordDictionary.getInstance().init(Paths.get("E:\\data\\GraduateDesign\\RuoYi-Vue-master\\ruoyi-system\\conf"));

        for (String punctuationMark : punctuationMarks) {
            punctuationMarksSet.add(punctuationMark);
        }

    }

    @PostMapping("/diagnose")
    public Object diagnose(@RequestBody ExtraReq req){
        System.out.println(req);
        Map<String,String> segmentWordKey = redisCache.redisTemplate.opsForHash().entries(SegmentWordKey);
        List<String> strings = segmenter.sentenceProcess(req.getSymptomsDesc());

        Set<String> match = new HashSet<>();

        for (String str : strings) {
            // 跳过标点符号
            if(punctuationMarksSet.contains(str)){
                continue;
            }

            if(segmentWordKey.keySet().contains(str)){
                System.out.println(str + " ============= " + segmentWordKey.get(str));
                match.add(segmentWordKey.get(str));
            }else {
                System.out.println(str);
            }

        }

        // 匹配的症状
        System.out.println(match);

        return null;
    }

}
