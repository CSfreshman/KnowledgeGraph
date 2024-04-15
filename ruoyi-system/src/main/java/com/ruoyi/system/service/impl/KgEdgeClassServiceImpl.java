package com.ruoyi.system.service.impl;

import java.util.*;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KgEdgeClassProperties;
import com.ruoyi.system.domain.KgNodeClass;
import com.ruoyi.system.mapper.KgEdgeClassPropertiesMapper;
import com.ruoyi.system.mapper.KgEdgeInstacePropertiesMapper;
import com.ruoyi.system.mapper.KgNodeClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgEdgeClassMapper;
import com.ruoyi.system.domain.KgEdgeClass;
import com.ruoyi.system.service.IKgEdgeClassService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-08
 */
@Service
public class KgEdgeClassServiceImpl implements IKgEdgeClassService
{
    @Autowired
    private KgEdgeClassMapper kgEdgeClassMapper;

    @Autowired
    private KgNodeClassMapper kgNodeClassMapper;

    @Autowired
    private KgEdgeClassPropertiesMapper edgeClassPropertiesMapper;
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgEdgeClass selectKgEdgeClassById(Long id)
    {
        KgEdgeClass kgEdgeClass = kgEdgeClassMapper.selectKgEdgeClassById(id);
        if(ObjectUtil.isNotNull(kgEdgeClass.getFromNodeId())){
            KgNodeClass fromNodeClass = kgNodeClassMapper.selectKgNodeClassById(kgEdgeClass.getFromNodeId());
            if(ObjectUtil.isNotNull(fromNodeClass)){
                kgEdgeClass.setFromNodeClassName(fromNodeClass.getName());
            }
        }
        if(ObjectUtil.isNotNull(kgEdgeClass.getToNodeId())){
            KgNodeClass toNodeClass = kgNodeClassMapper.selectKgNodeClassById(kgEdgeClass.getToNodeId());
            if(ObjectUtil.isNotNull(toNodeClass)){
                kgEdgeClass.setToNodeClassName(toNodeClass.getName());
            }
        }
        return kgEdgeClass;
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgEdgeClass 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgEdgeClass> selectKgEdgeClassList(KgEdgeClass kgEdgeClass)
    {
        List<KgEdgeClass> kgEdgeClasses = kgEdgeClassMapper.selectKgEdgeClassList(kgEdgeClass);

        kgEdgeClasses.forEach(it->{
            if(ObjectUtil.isNotNull(it.getFromNodeId())){
                KgNodeClass fromNodeClass = kgNodeClassMapper.selectKgNodeClassById(it.getFromNodeId());
                if(ObjectUtil.isNotNull(fromNodeClass)){
                    it.setFromNodeClassName(fromNodeClass.getName());
                }
            }
            if(ObjectUtil.isNotNull(it.getToNodeId())){
                KgNodeClass toNodeClass = kgNodeClassMapper.selectKgNodeClassById(it.getToNodeId());
                if(ObjectUtil.isNotNull(toNodeClass)){
                    it.setToNodeClassName(toNodeClass.getName());
                }
            }
            KgEdgeClassProperties req = new KgEdgeClassProperties();
            req.setEdgeId(it.getId());
            req.setValid(1L);
            it.setProps(edgeClassPropertiesMapper.selectKgEdgeClassPropertiesList(req));

        });
        return kgEdgeClasses;
    }



    // 全部环路
    public static List<List<Long>> allPath = new ArrayList<>();
    // 环路去重
    public static HashSet<List<Long>> allPathSet = new HashSet<>();

    // 路径
    public static Deque<Long> path = new LinkedList<>();

    /**
     * 新增【请填写功能名称】
     *
     * @param kgEdgeClass 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgEdgeClass(KgEdgeClass kgEdgeClass)
    {

        KgEdgeClass test = new KgEdgeClass();
        test.setLabel(kgEdgeClass.getLabel());
        test.setValid(1l);
        List<KgEdgeClass> kgEdgeClassList = selectKgEdgeClassList(test);
        if(ObjectUtil.isNotEmpty(kgEdgeClassList)){
            throw new RuntimeException("关系名称重复");
        }

        // 结束之后清空静态变量
        allPathSet = new HashSet<>();
        allPath = new ArrayList<>();
        path = new LinkedList<>();
        // 重置查询条件
        test = new KgEdgeClass();
        test.setValid(1l);
        kgEdgeClassList = selectKgEdgeClassList(test);
        kgEdgeClassList.add(kgEdgeClass);
        // 新增关系之前需要检查是否出现环
        Map<Long, List<KgEdgeClass>> adjacencyList = new HashMap<>();
        // nodeId : index 键值对
        Map<Long,Integer> nodeIndex = new HashMap<>();
        Set<Long> nodeIdSet = new HashSet<>();
        for (KgEdgeClass edge : kgEdgeClassList) {
            nodeIdSet.add(edge.getFromNodeId());
            nodeIdSet.add(edge.getToNodeId());

            adjacencyList.computeIfAbsent(edge.getFromNodeId(), k -> new ArrayList<>()).add(edge);
        }

        List<Long> nodeIdList = new ArrayList<>(nodeIdSet);
        for (int i = 0; i < nodeIdList.size(); i++) {
            nodeIndex.put(nodeIdList.get(i),i);
        }
        findCyclePath(nodeIdList,kgEdgeClassList,nodeIndex,adjacencyList);
        allPathSet = new HashSet<>(allPath);
        if(!(allPathSet == null || allPathSet.size() == 0)){
            // 出现了环
            System.out.println("出现环路：" + allPathSet);
            throw new RuntimeException("出现了环路:" + allPathSet);
        }
        // 结束之后清空静态变量
        allPathSet = new HashSet<>();
        allPath = new ArrayList<>();
        path = new LinkedList<>();



        kgEdgeClass.setCreateTime(DateUtils.getNowDate());
        kgEdgeClass.setId(IdUtil.getSnowflakeNextId());
        kgEdgeClass.setCreateUser(SecurityUtils.getUserId());
        return kgEdgeClassMapper.insertKgEdgeClass(kgEdgeClass);
    }


    public static void findCyclePath(List<Long> nodeIdList, List<KgEdgeClass> edges, Map<Long,Integer> nodeIndex, Map<Long,List<KgEdgeClass>> adjacencyList){
        boolean[] visited = new boolean[nodeIdList.size()];

        for (KgEdgeClass edge : edges) {
            long fromId = edge.getFromNodeId();
            // 节点没有访问
            if(!visited[nodeIndex.get(fromId)]){
                dfsUtil(nodeIdList,edges,nodeIndex,visited,fromId,adjacencyList);
            }
        }
    }

    // nodeId为当前访问的节点
    public static void dfsUtil(List<Long> nodeIdList,List<KgEdgeClass> edges,Map<Long,Integer> nodeIndex,boolean[] visited,long nodeId,Map<Long, List<KgEdgeClass>> adjacencyList){
        // 标记访问
        visited[nodeIndex.get(nodeId)] = true;
        // 添加路径
        path.addLast(nodeId);

        // 获得该节点的边
        List<KgEdgeClass> edges1 = adjacencyList.get(nodeId);

        // 该节点没有外出边
        if(edges1 == null || edges1.size() == 0){
            // 结束
            // 取消访问标记
            visited[nodeIndex.get(nodeId)] = false;
            // 路径中删除该节点
            path.removeLast();
            return;
        }

        // 遍历这些边
        for (KgEdgeClass edge : edges1) {
            // 邻节点id
            long toId = edge.getToNodeId();
            // 如果被访问过，就说明出现了环
            if(visited[nodeIndex.get(toId)]){
                // 将当前路径添加在allPath中
                path.addLast(toId);
//                allPath.add(new LinkedList<>(path));
                handleCyclePath(new ArrayList<>(path));
                path.removeLast();
            }else{
                // 递归
                dfsUtil(nodeIdList,edges,nodeIndex,visited,toId,adjacencyList);
            }

        }

        // 该节点访问结束
        // 取消访问标记
        visited[nodeIndex.get(nodeId)] = false;
        // 路径中删除该节点
        path.removeLast();

    }

    // 处理环路路径
    public static void handleCyclePath(List<Long> path){
        Map<Long,Integer> map = new HashMap<>();
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < path.size(); i++) {
            if (map.containsKey(path.get(i))) {
                startIndex = map.get(path.get(i));
                endIndex = i;
                break;
            }

            map.put(path.get(i),i);
        }

        List<Long> subList = path.subList(startIndex,endIndex);
        subList.sort((o1, o2) -> o1.compareTo(o2));
        allPath.add(subList);
        //System.out.println(subList);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgEdgeClass 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgEdgeClass(KgEdgeClass kgEdgeClass)
    {
        return kgEdgeClassMapper.updateKgEdgeClass(kgEdgeClass);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgEdgeClassByIds(Long[] ids)
    {
        return kgEdgeClassMapper.deleteKgEdgeClassByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgEdgeClassById(Long id)
    {
        return kgEdgeClassMapper.deleteKgEdgeClassById(id);
    }
}
