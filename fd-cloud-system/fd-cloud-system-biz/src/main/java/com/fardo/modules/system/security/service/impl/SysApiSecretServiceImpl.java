package com.fardo.modules.system.security.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.system.vo.PageVo;
import com.fardo.common.util.RedisUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.common.util.security.AESTool;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.security.entity.SysApiSecretEntity;
import com.fardo.modules.system.security.mapper.SysApiSecretMapper;
import com.fardo.modules.system.security.model.ApiSecretPageModel;
import com.fardo.modules.system.security.service.ISysApiSecretService;
import com.fardo.modules.system.security.vo.ApiSecretIdsVo;
import com.fardo.modules.system.security.vo.ApiSecretSaveVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class SysApiSecretServiceImpl extends ServiceImpl<SysApiSecretMapper, SysApiSecretEntity> implements ISysApiSecretService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 分页查询接入应用
     * @param pageVo
     * @return
     */
    @Override
    public IPage<ApiSecretPageModel> queryPage(PageVo pageVo) {
        IPage<SysApiSecretEntity> page = this.page(new Page<>(pageVo.getPageNo(), pageVo.getPageSize()));
        List<SysApiSecretEntity> entityList = page.getRecords();
        IPage<ApiSecretPageModel> modelPage = new Page<>();
        BeanUtils.copyProperties(page, modelPage, "records");
        if(CollectionUtils.isEmpty(entityList)) {
            return modelPage;
        }
        List<ApiSecretPageModel> modelList = new ArrayList<>();
        entityList.forEach(entity -> {
            ApiSecretPageModel model = new ApiSecretPageModel();
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
    public void save(ApiSecretSaveVo vo) {
        SysApiSecretEntity entity;
        if(StringUtil.isBlank(vo.getId())) {
            entity = new SysApiSecretEntity();
            BeanUtils.copyProperties(vo, entity);
            String key = UUID.randomUUID().toString().replace("-", "");
            entity.setApiKey(key);
            AESTool aesTool = new AESTool();
            try {
                entity.setApiSecret(aesTool.encrypt("", key));
                redisUtil.hset(CacheKeyConstants.API_SECRET_PREFIX, entity.getApiKey(), entity.getApiSecret());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            entity = this.getById(vo.getId());
            BeanUtils.copyProperties(vo, entity);
        }
        this.saveOrUpdate(entity);
    }

    /**
     * 删除
     * @param vo
     */
    @Override
    public void delete(ApiSecretIdsVo vo) {
        List<String> ids = Arrays.asList(vo.getIds().split(","));
        this.removeByIds(ids);
        redisUtil.del(CacheKeyConstants.API_SECRET_PREFIX);
        this.initApiSecretMap();
    }

    /**
     * 初始化
     */
    @Override
    public void initApiSecretMap() {
        List<SysApiSecretEntity> entityList = this.list();
        entityList.forEach(entity -> redisUtil.hset(CacheKeyConstants.API_SECRET_PREFIX, entity.getApiKey(), entity.getApiSecret()));
    }

}
