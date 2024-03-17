package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
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
}
