package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.logging.Handler;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.domain.KgHistory;
import com.ruoyi.system.domain.KgNodeClassProperties;
import com.ruoyi.system.mapper.KgNodeClassPropertiesMapper;
import com.ruoyi.system.myEnum.HistoryEnum;
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
    @Autowired
    private KgNodeClassPropertiesMapper propertiesMapper;
    @Autowired
    private KgHistoryServiceImpl historyService;

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
        List<KgNodeClass> kgNodeClasses = kgNodeClassMapper.selectKgNodeClassList(kgNodeClass);
        for (KgNodeClass nodeClass : kgNodeClasses) {
            KgNodeClassProperties req = new KgNodeClassProperties();
            req.setValid(1);
            req.setNodeId(nodeClass.getId());
            nodeClass.setProps(propertiesMapper.selectKgNodeClassPropertiesList(req));
        }
        return kgNodeClasses;
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

        KgNodeClass test = new KgNodeClass();
        test.setName(kgNodeClass.getName());
        test.setValid(1l);
        List<KgNodeClass> kgNodeClasses = selectKgNodeClassList(test);
        if(ObjectUtil.isNotEmpty(kgNodeClasses)){
            throw new RuntimeException("名称重复");
        }


        log.info(kgNodeClass.toString());
        kgNodeClass.setCreateTime(DateUtils.getNowDate());
        kgNodeClass.setId(cn.hutool.core.util.IdUtil.getSnowflakeNextId());
        kgNodeClass.setValid(1L);
        kgNodeClass.setCreateUser(SecurityUtils.getUserId());

        KgHistory history = new KgHistory();
        // 新增
        history.setType(1);
        history.setTargetType(1);
        history.setTargetId(kgNodeClass.getId());
        history.setTargetName(kgNodeClass.getName());
        historyService.insertKgHistory(history);

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

    @Override
    public List<KgNodeClass> getAll(KgNodeClass kgNodeClass) {
        List<KgNodeClass> kgNodeClasses = kgNodeClassMapper.selectKgNodeClassList(kgNodeClass);
        for (KgNodeClass nodeClass : kgNodeClasses) {
            KgNodeClassProperties req = new KgNodeClassProperties();
            req.setValid(1);
            req.setNodeId(nodeClass.getId());
            nodeClass.setProps(propertiesMapper.selectKgNodeClassPropertiesList(req));
        }
        return kgNodeClasses;
    }
}
