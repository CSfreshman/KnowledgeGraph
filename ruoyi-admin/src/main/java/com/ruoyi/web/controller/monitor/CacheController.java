package com.ruoyi.web.controller.monitor;

import java.util.*;

import com.ruoyi.system.req.CacheReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysCache;

/**
 * 缓存监控
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/cache")
public class CacheController
{
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final static List<SysCache> caches = new ArrayList<SysCache>();
    {
        caches.add(new SysCache(CacheConstants.LOGIN_TOKEN_KEY, "用户信息"));
        caches.add(new SysCache(CacheConstants.SYS_CONFIG_KEY, "配置信息"));
        caches.add(new SysCache(CacheConstants.SYS_DICT_KEY, "数据字典"));
        caches.add(new SysCache(CacheConstants.CAPTCHA_CODE_KEY, "验证码"));
        caches.add(new SysCache(CacheConstants.REPEAT_SUBMIT_KEY, "防重提交"));
        caches.add(new SysCache(CacheConstants.RATE_LIMIT_KEY, "限流处理"));
        caches.add(new SysCache(CacheConstants.PWD_ERR_CNT_KEY, "密码错误次数"));
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping()
    public AjaxResult getInfo() throws Exception
    {
        Properties info = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info());
        Properties commandStats = (Properties) redisTemplate.execute((RedisCallback<Object>) connection -> connection.info("commandstats"));
        Object dbSize = redisTemplate.execute((RedisCallback<Object>) connection -> connection.dbSize());

        Map<String, Object> result = new HashMap<>(3);
        result.put("info", info);
        result.put("dbSize", dbSize);

        List<Map<String, String>> pieList = new ArrayList<>();
        commandStats.stringPropertyNames().forEach(key -> {
            Map<String, String> data = new HashMap<>(2);
            String property = commandStats.getProperty(key);
            data.put("name", StringUtils.removeStart(key, "cmdstat_"));
            data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
            pieList.add(data);
        });
        result.put("commandStats", pieList);
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getNames")
    public AjaxResult cache()
    {
        return AjaxResult.success(caches);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getKeys/{cacheName}")
    public AjaxResult getCacheKeys(@PathVariable String cacheName)
    {
        Set<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        return AjaxResult.success(cacheKeys);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/getValue/{cacheName}/{cacheKey}")
    public AjaxResult getCacheValue(@PathVariable String cacheName, @PathVariable String cacheKey)
    {
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        SysCache sysCache = new SysCache(cacheName, cacheKey, cacheValue);
        return AjaxResult.success(sysCache);
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheName/{cacheName}")
    public AjaxResult clearCacheName(@PathVariable String cacheName)
    {
        Collection<String> cacheKeys = redisTemplate.keys(cacheName + "*");
        redisTemplate.delete(cacheKeys);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheKey/{cacheKey}")
    public AjaxResult clearCacheKey(@PathVariable String cacheKey)
    {
        redisTemplate.delete(cacheKey);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @DeleteMapping("/clearCacheAll")
    public AjaxResult clearCacheAll()
    {
        Collection<String> cacheKeys = redisTemplate.keys("*");
        redisTemplate.delete(cacheKeys);
        return AjaxResult.success();
    }

    // 获得全部的key
    @PostMapping("/getAllCaches")
    public AjaxResult getAllCaches(){
        ScanOptions options = ScanOptions.scanOptions().count(1000).build();

        // 存储所有键的列表
        List<String> allKeys = new ArrayList<>();

        redisTemplate.execute((RedisCallback<Void>) connection -> {
            Cursor<byte[]> cursor = connection.scan(options);

            while (cursor.hasNext()) {
                byte[] next = cursor.next();
                allKeys.add(new String(next));
            }

            return null;
        });

        System.out.println(allKeys);

        return AjaxResult.success(allKeys);
    }

    public static final String ZzDictKey = "segmentWord";

    // 症状词典管理
    @PostMapping("/getZzDict")
    public AjaxResult getZzDict(@RequestBody CacheReq req){
        // 得到全部的键值对
        Map<Object, Object> segmentWord = redisTemplate.opsForHash().entries(ZzDictKey);
        Map<String,String> map = new HashMap<>();
        for (Map.Entry<Object, Object> entry : segmentWord.entrySet()) {
            String s = entry.getValue().toString();
            map.put(entry.getKey().toString(),s.substring(1,s.length() - 1));
        }
        List<Map.Entry<String, String>> list = new ArrayList<>(map.entrySet());
        list.sort(new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        // 分页
        int pageNum = req.getPageNum(); // 页码，从1开始
        int pageSize = req.getPageSize(); // 每页数据量

        // 计算起始索引和结束索引
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, list.size());

        // 获取指定页的数据
        List<Map.Entry<String, String>> pageData = list.subList(startIndex, endIndex);


        // 结果处理，变成方便前端处理的结构
        List<Map<String,String>> res = new ArrayList<>();
        for (Map.Entry<String, String> entry : pageData) {
            Map<String,String> map1 = new HashMap<>();
            map1.put("key",entry.getKey());
            map1.put("value",entry.getValue());
            res.add(map1);
        }

        return AjaxResult.success(res);
    }

    // 新增词典
    @PostMapping("/addZzDict")
    public AjaxResult addZzDict(@RequestBody CacheReq req){
        Boolean aBoolean = redisTemplate.opsForHash().putIfAbsent(ZzDictKey, req.getKey(), req.getValue());

        return AjaxResult.success();
    }

    // 删除
    @PostMapping("/delZzDict")
    public AjaxResult delZzDict(@RequestBody CacheReq req){
        redisTemplate.opsForHash().delete(ZzDictKey,req.getKey());
        return AjaxResult.success();
    }

    // 修改
    @PostMapping("/updateZzDict")
    public AjaxResult updateZzDict(@RequestBody CacheReq req){
        // 先删再加
        redisTemplate.opsForHash().delete(ZzDictKey,req.getKey());
        Boolean aBoolean = redisTemplate.opsForHash().putIfAbsent(ZzDictKey, req.getKey(), req.getValue());
        return AjaxResult.success();
    }
}
