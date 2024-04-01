package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.IdUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.KgHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgNodeClassPropertiesMapper;
import com.ruoyi.system.domain.KgNodeClassProperties;
import com.ruoyi.system.service.IKgNodeClassPropertiesService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-04
 */
@Service
public class KgNodeClassPropertiesServiceImpl implements IKgNodeClassPropertiesService
{
    @Autowired
    private KgNodeClassPropertiesMapper kgNodeClassPropertiesMapper;
    @Autowired
    private KgHistoryServiceImpl historyService;
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgNodeClassProperties selectKgNodeClassPropertiesById(Long id)
    {
        return kgNodeClassPropertiesMapper.selectKgNodeClassPropertiesById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgNodeClassProperties> selectKgNodeClassPropertiesList(KgNodeClassProperties kgNodeClassProperties)
    {
        return kgNodeClassPropertiesMapper.selectKgNodeClassPropertiesList(kgNodeClassProperties);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgNodeClassProperties(KgNodeClassProperties kgNodeClassProperties)
    {
        kgNodeClassProperties.setCreateTime(DateUtils.getNowDate());
        kgNodeClassProperties.setId(IdUtil.getSnowflakeNextId());
        kgNodeClassProperties.setCreateUser(SecurityUtils.getUserId());

        KgHistory history = new KgHistory();
        // 新增
        history.setType(1);
        history.setTargetType(2);
        history.setTargetId(kgNodeClassProperties.getId());
        history.setTargetName(kgNodeClassProperties.getName());
        historyService.insertKgHistory(history);

        return kgNodeClassPropertiesMapper.insertKgNodeClassProperties(kgNodeClassProperties);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgNodeClassProperties 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgNodeClassProperties(KgNodeClassProperties kgNodeClassProperties)
    {
        return kgNodeClassPropertiesMapper.updateKgNodeClassProperties(kgNodeClassProperties);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeClassPropertiesByIds(Long[] ids)
    {
        int count = 0;
        for (Long id : ids) {
            count+=deleteKgNodeClassPropertiesById(id);
        }
        return count;
    }

    @Override
    public int deleteKgNodeClassPropertiesById(Long id)
    {
//        return kgNodeClassPropertiesMapper.deleteKgNodeClassPropertiesById(id);
        KgNodeClassProperties kgNodeClassProperties = selectKgNodeClassPropertiesById(id);
        // 置为失效,设置修改时间和修改人
        kgNodeClassProperties.setValid(0);
        kgNodeClassProperties.setModifyTime(DateUtils.getNowDate());
        kgNodeClassProperties.setModifyUser(SecurityUtils.getUserId());
        kgNodeClassProperties.setModifyType(0L);

        // 历史记录
        KgHistory history = new KgHistory();
        // 删除
        history.setType(2);
        history.setTargetType(2);
        history.setTargetId(kgNodeClassProperties.getId());
        history.setTargetName(kgNodeClassProperties.getName());
        historyService.insertKgHistory(history);

        // 更新数据
        return updateKgNodeClassProperties(kgNodeClassProperties);

    }
}
