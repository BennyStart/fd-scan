package com.fardo.modules.system.config.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.util.RedisUtil;
import com.fardo.modules.system.config.entity.SysConfigEntity;
import com.fardo.modules.system.config.mapper.SysConfigMapper;
import com.fardo.modules.system.config.service.ISysClientConfigService;
import com.fardo.modules.system.config.service.ISysConfigService;
import com.fardo.modules.system.config.vo.QueryConfigByCodeVo;
import com.fardo.modules.system.config.vo.SysConfigListVo;
import com.fardo.modules.system.config.vo.SysConfigVo;
import com.fardo.modules.system.config.vo.UpdateConfigByCodeVo;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.constant.ClientConfigIdConstants;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.znbl.picmanage.vo.PicmanageListVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service("sysConfigService")
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfigEntity> implements ISysConfigService {

    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ISysClientConfigService sysClientConfigService;

    @Override
    public List<Map<String, Object>> doFindAlList() {
        List<SysConfigEntity> entitys = this.list();
        List<Map<String, Object>> list = new ArrayList<>();
        for (SysConfigEntity config:entitys) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("section_name", config.getName());
            map.put("config_name", config.getCode());
            map.put("config_value", config.getValue());
            list.add(map);
        }
        return list;
    }

    @Override
    public boolean findConfigSwitch(String code) {
        return false;
    }

    @Override
    public void uploadConfig(String key, String value) {
        SysConfigEntity entity = this.query().eq("code", key).one();
        if(entity == null) {
            log.error("参数:"+key+"不存在，请检查表t_s_config");
            return;
        }
        entity.setValue(value);
        this.updateById(entity);
        redisUtil.hset(CacheKeyConstants.SYS_PARAMETER, key, value);
    }

    @Override
    public IPage<SysConfigEntity> getPageList(SysConfigListVo sysConfigVo) {
        String code = sysConfigVo.getCode();
        String name = sysConfigVo.getName();
        String remark = sysConfigVo.getRemark();
        Page<SysConfigEntity> page = new Page<>(sysConfigVo.getPageNo(),sysConfigVo.getPageSize());
        return baseMapper.getSysConfigEntityPage(page,code,name,remark);
        //        // QueryWrapper<SysConfigEntity> queryWrapper = new QueryWrapper<>();
        // if (StringUtils.isNotBlank(code)){
        //     queryWrapper.like("code",code);
        // }
        // if (StringUtils.isNotBlank(name)){
        //     queryWrapper.like("name",name);
        // }
        // if (StringUtils.isNotBlank(remark)){
        //     queryWrapper.like("remark",remark);
        // }
        // queryWrapper.orderByDesc("create_time");
        // return baseMapper.selectPage(new Page<>(sysConfigVo.getPageNo(),sysConfigVo.getPageSize()),queryWrapper);
        // LambdaQueryWrapper<SysConfigEntity> lambdaQueryWrapper = new LambdaQueryWrapper<SysConfigEntity>();
        // IPage<SysConfigModel> modelIPage = new Page<>();//结果
        // IPage<SysConfigEntity> page = new Page<>(sysConfigVo.getPageNo(), sysConfigVo.getPageSize());
        // lambdaQueryWrapper.orderByDesc(SysConfigEntity::getUpdateTime);
        // if(StringUtils.isNotBlank(sysConfigVo.getRemark())){//描述
        //     lambdaQueryWrapper.like(SysConfigEntity::getRemark,sysConfigVo.getRemark());
        // }
        // if(StringUtils.isNotBlank(sysConfigVo.getType())){//类型
        //     lambdaQueryWrapper.like(SysConfigEntity::getType,sysConfigVo.getType());
        // }
        // IPage<SysConfigEntity> pageList = configMapper.selectPage(page, lambdaQueryWrapper);
        // List<SysConfigModel> list = new ArrayList<>(pageList.getRecords().size());
        // pageList.getRecords().forEach(m->{SysConfigModel model = new SysConfigModel();BeanUtils.copyProperties(m,model);list.add(model);});
        // modelIPage.setRecords(list);
        // modelIPage.setTotal(pageList.getTotal());
        // modelIPage.setSize(pageList.getSize());
        // modelIPage.setPages(pageList.getPages());
        // return modelIPage;
    }

    public boolean checkParameterConfig(String id,String key) {
        if(StringUtils.isBlank(key)){
            throw new RuntimeException("参数不能为空");
        }
        boolean falg=true;
        LambdaQueryWrapper<SysConfigEntity> lambdaQueryWrapper = new LambdaQueryWrapper<SysConfigEntity>();
        if(StringUtils.isNotBlank(id)){
            lambdaQueryWrapper.ne(SysConfigEntity::getId,id);
        }
        lambdaQueryWrapper.eq(SysConfigEntity::getCode,key);
        SysConfigEntity entity = configMapper.selectOne(lambdaQueryWrapper);
        if(entity!=null){
            falg=false;
        }
        return falg;
    }

    @Override
    @Transactional
    public void addParameterConfig(SysConfigVo sysConfigVo,String userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = dateFormat.format(new Date());
        SysConfigEntity sysConfig = new SysConfigEntity();
        BeanUtils.copyProperties(sysConfigVo, sysConfig);
        sysConfig.setCreateTime(format);
        sysConfig.setCreateBy(userId);
       // sysConfig.setId(UUIDGenerator.generate());
        configMapper.insert(sysConfig);
        redisUtil.hset(CacheKeyConstants.SYS_PARAMETER, sysConfig.getCode(), sysConfig.getValue());
    }

    @Override
    @Transactional
    public void updatParameterConfig(SysConfigVo sysConfigVo,String userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = dateFormat.format(new Date());
        SysConfigEntity sysConfig = this.getById(sysConfigVo.getId());
        if(sysConfig==null) {
            throw new RuntimeException("参数无效,未找到对应实体");
        }
        String type = sysConfig.getType();
        BeanUtils.copyProperties(sysConfigVo, sysConfig);
        sysConfig.setUpdateBy(userId);
        sysConfig.setUpdateTime(format);
        this.updateById(sysConfig);
        redisUtil.hset(CacheKeyConstants.SYS_PARAMETER, sysConfig.getCode(), sysConfig.getValue());
        //修改客户端参数，刷新客户端配置项缓存
        if("client".equals(type)) {
            sysClientConfigService.refreshClientConfig(ClientConfigIdConstants.T_SYS_SETTING);
        }
    }

    @Override
    @Transactional
    public void deleteSysConfig(String id) {
        SysConfigEntity byId = this.getById(id);
        if(byId ==null){
            throw new RuntimeException("参数无效");
        }
        this.removeById(id);
        redisUtil.hdel(CacheKeyConstants.SYS_PARAMETER,byId.getCode());
    }

    @Override
    public Integer selectCount(String code) {
        QueryWrapper<SysConfigEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code",code);
        return baseMapper.selectCount(queryWrapper);
    }


    /**
     * 先查找缓存
     * 缓存不存在，再查找数据库，并把数据库的值放入缓存
     * @param key
     * @return
     */
    @Override
    public String getSysParam(String key) {
        Object cache = redisUtil.hget(CacheKeyConstants.SYS_PARAMETER, key);
        String value;
        if(cache == null) {
            SysConfigEntity entity = this.query().eq("code", key).one();
            if(entity == null) {
                log.error("参数:"+key+"不存在，请检查表t_s_config");
                return "";
            }
            value = entity.getValue();
            redisUtil.hset(CacheKeyConstants.SYS_PARAMETER, key, value);
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

    /**
     * 初始化参数
     */
    @Override
    public void init() {
        log.info("初始化系统参数begin");
        List<SysConfigEntity> configList = this.list();
        for(SysConfigEntity config : configList){
            redisUtil.hset(CacheKeyConstants.SYS_PARAMETER,config.getCode(),config.getValue());
        }
        log.info("初始化系统参数:{}个 end",configList.size());
    }

    /**
     * 刷新缓存
     */
    @Override
    public void refresh() {
        redisUtil.del(CacheKeyConstants.SYS_PARAMETER);
        init();
    }

    /**
     * 根据代码修改配置
     * @param vo
     */
    @Override
    public void updateConfigByCode(UpdateConfigByCodeVo vo) {
        LambdaUpdateWrapper<SysConfigEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SysConfigEntity::getValue, vo.getValue());
        updateWrapper.eq(SysConfigEntity::getCode, vo.getCode());
        this.update(updateWrapper);
    }

    @Override
    public SysConfigVo queryConfigByCode(QueryConfigByCodeVo vo) {
        LambdaQueryWrapper<SysConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysConfigEntity::getCode, vo.getCode());
        SysConfigEntity entity = this.getOne(queryWrapper, false);
        SysConfigVo sysConfigVo = new SysConfigVo();
        BeanUtils.copyProperties(entity, sysConfigVo);
        return sysConfigVo;
    }
}
