package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.service.IKgEdgeInstacePropertiesService;
import com.ruoyi.system.service.IKgHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgEdgeInstanceMapper;
import com.ruoyi.system.domain.KgEdgeInstance;
import com.ruoyi.system.service.IKgEdgeInstanceService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-17
 */
@Service
public class KgEdgeInstanceServiceImpl implements IKgEdgeInstanceService
{
    @Autowired
    private KgEdgeInstanceMapper kgEdgeInstanceMapper;
    @Autowired
    private IKgHistoryService historyService;
    @Autowired
    private IKgEdgeInstacePropertiesService edgeInstacePropertiesService;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgEdgeInstance selectKgEdgeInstanceById(Long id)
    {
        return kgEdgeInstanceMapper.selectKgEdgeInstanceById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgEdgeInstance 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgEdgeInstance> selectKgEdgeInstanceList(KgEdgeInstance kgEdgeInstance)
    {
        return kgEdgeInstanceMapper.selectKgEdgeInstanceList(kgEdgeInstance);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgEdgeInstance 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgEdgeInstance(KgEdgeInstance kgEdgeInstance)
    {
        kgEdgeInstance.setCreateTime(DateUtils.getNowDate());
        return kgEdgeInstanceMapper.insertKgEdgeInstance(kgEdgeInstance);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgEdgeInstance 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgEdgeInstance(KgEdgeInstance kgEdgeInstance)
    {
        return kgEdgeInstanceMapper.updateKgEdgeInstance(kgEdgeInstance);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgEdgeInstanceByIds(Long[] ids)
    {
        return kgEdgeInstanceMapper.deleteKgEdgeInstanceByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgEdgeInstanceById(Long id)
    {
        return kgEdgeInstanceMapper.deleteKgEdgeInstanceById(id);
    }

    @Override
    public Integer deleteEdgeByNodeNeo4jId(Long nodeId) {
        KgEdgeInstance instance = new KgEdgeInstance();
        // 起点或者终点都需要删除
        instance.setFromNodeNeo4jId(nodeId);
        List<KgEdgeInstance> list1 = selectKgEdgeInstanceList(instance);

        KgEdgeInstance newInstance = new KgEdgeInstance();
        newInstance.setToNodeNeo4jId(nodeId);
        List<KgEdgeInstance> list2 = selectKgEdgeInstanceList(newInstance);

        list1.addAll(list2);

        if(ObjectUtil.isEmpty(list1)){
            return 0;
        }

        for (KgEdgeInstance item : list1) {
            // 逻辑删除
            item.setValid(0l);
            updateKgEdgeInstance(item);

            KgHistory history = new KgHistory();
            history.setType(2);
            history.setTargetType(5);
            history.setTargetId(item.getId());
            history.setTargetName(item.getLabel());
            historyService.insertKgHistory(history);

            System.out.println("删除关系属性");
            int i = edgeInstacePropertiesService.deleteByEdgeId(item.getId());
        }



        return list1.size();
    }
}
