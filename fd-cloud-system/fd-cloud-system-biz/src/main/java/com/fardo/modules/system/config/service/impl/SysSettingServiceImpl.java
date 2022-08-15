package com.fardo.modules.system.config.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.exception.ApiException;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fardo.common.util.RedisUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.system.config.entity.SysSettingEntity;
import com.fardo.modules.system.config.mapper.SysSettingMapper;
import com.fardo.modules.system.config.model.SysSettingQueryModel;
import com.fardo.modules.system.config.service.ISysSettingService;
import com.fardo.modules.system.config.vo.SysSettingDeleteVo;
import com.fardo.modules.system.config.vo.SysSettingQueryVo;
import com.fardo.modules.system.config.vo.SysSettingSaveVo;
import com.fardo.modules.system.constant.CacheKeyConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service("sysSettingService")
public class SysSettingServiceImpl extends ServiceImpl<SysSettingMapper, SysSettingEntity> implements ISysSettingService {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String getSysParam(String key) {
        String value;
        Object cache = redisUtil.hget(CacheKeyConstants.CLIENT_PARAMETER, key);
        if(cache == null) {
            SysSettingEntity entity = this.query().eq("proKey", key).one();
            if(entity == null) {
                log.error("参数:"+key+"不存在，请检查表t_s_config");
                return "";
            }
            value = entity.getProValue();
            redisUtil.hset(CacheKeyConstants.CLIENT_PARAMETER, key, value);
        } else {
            value = (String) cache;
        }
        return value;
    }

    @Override
    public int getIntSysParam(String key) {
        String valueStr = getSysParam(key);
        return NumberUtils.toInt(valueStr);
    }

    @Override
    public boolean getBoolSysParam(String key) {
        String valueStr = getSysParam(key);
        return BooleanUtils.toBoolean(valueStr);
    }

    @Override
    public void init() {
        log.info("初始化客户端系统参数begin");
        List<SysSettingEntity> settingList = this.list();
        for(SysSettingEntity sysSetting : settingList){
            redisUtil.hset(CacheKeyConstants.CLIENT_PARAMETER,sysSetting.getProKey(), sysSetting.getProValue());
        }
        log.info("初始化客户端系统参数:{}个 end",settingList.size());
    }

    @Override
    public void refresh() {
        redisUtil.del(CacheKeyConstants.CLIENT_PARAMETER);
        init();
    }

    @Override
    public boolean updateSysParam(String key, String value) {
        SysSettingEntity entity = this.query().eq("proKey", key).one();
        if(entity == null) {
            log.error("参数:"+key+"不存在，请检查表T_SYS_SETTING");
            return false;
        }
        entity.setProValue(value);
        this.updateById(entity);
        redisUtil.hset(CacheKeyConstants.CLIENT_PARAMETER, key, value);
        return true;
    }


    /**
     * 分页查询
     * @param vo
     * @return
     */
    @Override
    public IPage<SysSettingQueryModel> queryPage(SysSettingQueryVo vo) {
        LambdaQueryWrapper<SysSettingEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtil.isNotEmpty(vo.getProName())) {
            queryWrapper.like(SysSettingEntity::getProName, vo.getProName());
        }
        if(StringUtil.isNotEmpty(vo.getProKey())) {
            queryWrapper.like(SysSettingEntity::getProKey, vo.getProKey());
        }
        if(StringUtil.isNotEmpty(vo.getRemark())) {
            queryWrapper.like(SysSettingEntity::getRemark, vo.getRemark());
        }
        Page<SysSettingEntity> entityPage = new Page<>(vo.getPageNo(), vo.getPageSize());
        this.page(entityPage, queryWrapper);
        List<SysSettingEntity> entityList = entityPage.getRecords();
        Page<SysSettingQueryModel> modelPage = new Page<>();
        BeanUtils.copyProperties(entityPage, modelPage);
        List<SysSettingQueryModel> modelList = new ArrayList<>();
        entityList.forEach(entity -> {
            SysSettingQueryModel model = new SysSettingQueryModel();
            BeanUtils.copyProperties(entity, model);
            modelList.add(model);
        });
        modelPage.setRecords(modelList);
        return modelPage;
    }

    /**
     * 保存
     * @param vo
     */
    @Override
    public void save(SysSettingSaveVo vo) {
        if(this.isExist(vo.getId(), vo.getProKey())) {
            throw new ApiException("101", "配置已存在，不能重复");
        }
        SysSettingEntity entity;
        if(StringUtil.isEmpty(vo.getId())) {
            entity = new SysSettingEntity();
            entity.setSoftType("API_CLOUD");
            entity.setEditable("1");
            entity.setIsModuleSwitch("0");
            entity.setModuleUserFilter("0");
        } else {
            entity = this.getById(vo.getId());
            if(entity == null) {
                throw new ApiException("102", "id找不到配置");
            }
        }
        entity.setProName(vo.getProName());
        entity.setProKey(vo.getProKey());
        entity.setProValue(vo.getProValue());
        entity.setRemark(vo.getRemark());
        this.saveOrUpdate(entity);
        redisUtil.hset(CacheKeyConstants.CLIENT_PARAMETER, entity.getProKey(), entity.getProValue());
    }

    /**
     * 删除
     * @param vo
     */
    @Override
    public void delete(SysSettingDeleteVo vo) {
        List<String> ids = Arrays.asList(vo.getIds().split(","));
        LambdaQueryWrapper<SysSettingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysSettingEntity::getId, ids);
        List<SysSettingEntity> entityList = this.list(queryWrapper);
        Object[] proKeys = entityList.stream().map(SysSettingEntity::getProKey).toArray();
        redisUtil.hdel(CacheKeyConstants.CLIENT_PARAMETER, proKeys);
            this.removeByIds(ids);
    }



    private boolean isExist(String id, String proKey) {
        LambdaQueryWrapper<SysSettingEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysSettingEntity::getProKey, proKey);
        if(StringUtil.isNotEmpty(id)) {
            queryWrapper.ne(BaseEntity::getId, id);
        }
        List<SysSettingEntity> entities = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(entities)) {
            return false;
        }
        return true;
    }
}
