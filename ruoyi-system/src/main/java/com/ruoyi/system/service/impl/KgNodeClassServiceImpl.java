package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KgNodeClassMapper;
import com.ruoyi.system.domain.KgNodeClass;
import com.ruoyi.system.service.IKgNodeClassService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2024-03-04
 */
@Slf4j
@Service
public class KgNodeClassServiceImpl implements IKgNodeClassService
{
    @Autowired
    private KgNodeClassMapper kgNodeClassMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】主键
     * @return 【请填写功能名称】
     */
    @Override
    public KgNodeClass selectKgNodeClassById(Long id)
    {
        return kgNodeClassMapper.selectKgNodeClassById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param kgNodeClass 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<KgNodeClass> selectKgNodeClassList(KgNodeClass kgNodeClass)
    {
        return kgNodeClassMapper.selectKgNodeClassList(kgNodeClass);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param kgNodeClass 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertKgNodeClass(KgNodeClass kgNodeClass)
    {
        log.info(kgNodeClass.toString());
        kgNodeClass.setCreateTime(DateUtils.getNowDate());
        kgNodeClass.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextId());
        kgNodeClass.setValid(1L);
        kgNodeClass.setCreateUser(SecurityUtils.getUserId());
        return kgNodeClassMapper.insertKgNodeClass(kgNodeClass);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param kgNodeClass 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateKgNodeClass(KgNodeClass kgNodeClass)
    {
        return kgNodeClassMapper.updateKgNodeClass(kgNodeClass);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeClassByIds(Long[] ids)
    {
        return kgNodeClassMapper.deleteKgNodeClassByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】主键
     * @return 结果
     */
    @Override
    public int deleteKgNodeClassById(Long id)
    {
        return kgNodeClassMapper.deleteKgNodeClassById(id);
    }
}
