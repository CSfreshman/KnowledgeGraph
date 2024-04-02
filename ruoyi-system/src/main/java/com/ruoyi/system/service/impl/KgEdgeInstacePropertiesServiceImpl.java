package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.service.IKgHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgEdgeInstacePropertiesMapper;
import com.ruoyi.system.domain.KgEdgeInstaceProperties;
import com.ruoyi.system.service.IKgEdgeInstacePropertiesService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-18
 */
@Service
public class KgEdgeInstacePropertiesServiceImpl implements IKgEdgeInstacePropertiesService
{
    @Autowired
    private KgEdgeInstacePropertiesMapper kgEdgeInstacePropertiesMapper;
    @Autowired
    private IKgHistoryService historyService;
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgEdgeInstaceProperties selectKgEdgeInstacePropertiesById(Long id)
    {
        return kgEdgeInstacePropertiesMapper.selectKgEdgeInstacePropertiesById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgEdgeInstaceProperties 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgEdgeInstaceProperties> selectKgEdgeInstacePropertiesList(KgEdgeInstaceProperties kgEdgeInstaceProperties)
    {
        return kgEdgeInstacePropertiesMapper.selectKgEdgeInstacePropertiesList(kgEdgeInstaceProperties);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgEdgeInstaceProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgEdgeInstaceProperties(KgEdgeInstaceProperties kgEdgeInstaceProperties)
    {
        kgEdgeInstaceProperties.setCreateTime(DateUtils.getNowDate());
        return kgEdgeInstacePropertiesMapper.insertKgEdgeInstaceProperties(kgEdgeInstaceProperties);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgEdgeInstaceProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgEdgeInstaceProperties(KgEdgeInstaceProperties kgEdgeInstaceProperties)
    {
        return kgEdgeInstacePropertiesMapper.updateKgEdgeInstaceProperties(kgEdgeInstaceProperties);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgEdgeInstacePropertiesByIds(Long[] ids)
    {
        return kgEdgeInstacePropertiesMapper.deleteKgEdgeInstacePropertiesByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgEdgeInstacePropertiesById(Long id)
    {
        return kgEdgeInstacePropertiesMapper.deleteKgEdgeInstacePropertiesById(id);
    }


    @Override
    public int deleteByEdgeId(Long edgeId){
        KgEdgeInstaceProperties properties = new KgEdgeInstaceProperties();
        properties.setId(edgeId);

        List<KgEdgeInstaceProperties> kgEdgeInstaceProperties = selectKgEdgeInstacePropertiesList(properties);
        if(ObjectUtil.isEmpty(kgEdgeInstaceProperties)){
            return 0;
        }
        for (KgEdgeInstaceProperties item : kgEdgeInstaceProperties) {
            // 逻辑删除
            item.setValid(0l);
            updateKgEdgeInstaceProperties(item);

            KgHistory history = new KgHistory();
            history.setType(2);
            history.setTargetType(8);
            history.setTargetId(item.getId());
            history.setTargetName(item.getName());
            historyService.insertKgHistory(history);
        }

        return kgEdgeInstaceProperties.size();

    }
}
