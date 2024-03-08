package com.ruoyi.system.service.impl;

import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.KgNodeClass;
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
        });
        return kgEdgeClasses;
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgEdgeClass 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgEdgeClass(KgEdgeClass kgEdgeClass)
    {
        kgEdgeClass.setCreateTime(DateUtils.getNowDate());
        return kgEdgeClassMapper.insertKgEdgeClass(kgEdgeClass);
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
