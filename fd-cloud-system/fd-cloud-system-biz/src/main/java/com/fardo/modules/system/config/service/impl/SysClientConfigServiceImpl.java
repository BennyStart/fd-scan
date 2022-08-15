package com.fardo.modules.system.config.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.aspect.annotation.RedisLock;
import com.fardo.common.util.DateUtils;
import com.fardo.common.util.MD5Util;
import com.fardo.common.util.RedisUtil;
import com.fardo.common.util.UUIDGenerator;
import com.fardo.modules.system.config.entity.SysClientConfigEntity;
import com.fardo.modules.system.config.entity.SysClientConfigGlobalEntity;
import com.fardo.modules.system.config.entity.SysDataClobEntity;
import com.fardo.modules.system.config.entity.SysSettingEntity;
import com.fardo.modules.system.config.mapper.SysClientConfigGlobalMapper;
import com.fardo.modules.system.config.mapper.SysClientConfigMapper;
import com.fardo.modules.system.config.service.ISysClientConfigService;
import com.fardo.modules.system.config.service.ISysDataClobService;
import com.fardo.modules.system.config.service.ISysSettingService;
import com.fardo.modules.system.config.vo.SysFuncVo;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.constant.ClientConfigIdConstants;
import com.fardo.modules.system.constant.SysConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SysClientConfigServiceImpl extends ServiceImpl<SysClientConfigMapper, SysClientConfigEntity> implements ISysClientConfigService {

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private SysClientConfigGlobalMapper sysClientConfigGlobalMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ISysDataClobService iSysDataClobService;
    @Autowired
    private ISysSettingService sysSettingService;

    @Override
    public SysClientConfigEntity getConfigId(String configId) {
        Object configObject = redisUtil.hget(CacheKeyConstants.CLIENT_CONFIG, configId);
        if(configObject == null) {
            return null;
            //TODO 先去掉
//            reload();
//            configObject = redisUtil.hget(CacheKeyConstants.CLIENT_CONFIG, configId);
        }
        SysClientConfigEntity entity = JSONObject.parseObject(String.valueOf(configObject), SysClientConfigEntity.class);
        return entity;
    }

    @Override
    public String getConfigContentByConfigId(String configId) {
        String content = "";
        if(redisUtil.hasKey(getContentCacheKey(configId))){
            content = (String) redisUtil.get(getContentCacheKey(configId));
        }else{
            SysClientConfigEntity cc = this.getById(configId);
            if(cc != null){
                LambdaQueryWrapper<SysDataClobEntity> dcLq = new LambdaQueryWrapper<>();
                dcLq.eq(SysDataClobEntity::getId, cc.getConfigContent());
                SysDataClobEntity dc = iSysDataClobService.getOne(dcLq);
                if (dc != null) {
                    putClientConfigContent(cc, dc);
                }
                content = dc.getClobData();
            }
        }
        return content;
    }

    @Override
    public void load() {
        boolean isLoaded = redisUtil.hasKey(CacheKeyConstants.CLIENT_CONFIG);
        if (isLoaded) {
            // 已加载 过配置项，无须重复加载
            log.warn("已加载过客户配置配置参数，无需重复加载！如需重刷缓存，请调用 refresh的方法");
        }
        // 初始化配置项
        List<SysClientConfigEntity> ccList = this.list();
        if(CollectionUtils.isEmpty(ccList)) {
            log.info("加载的客户端配置项条数为0,请检查表：T_SYS_CLIENT_CONFIG_GLOBAL");
            return;
        }
        for (SysClientConfigEntity cc : ccList) {
            LambdaQueryWrapper<SysDataClobEntity> dcLq = new LambdaQueryWrapper<>();
            dcLq.eq(SysDataClobEntity::getId, cc.getConfigContent());
            SysDataClobEntity dc = iSysDataClobService.getOne(dcLq);
            if (dc != null) {
                redisUtil.hset(CacheKeyConstants.CLIENT_CONFIG, cc.getId(), JSONObject.toJSONString(cc));
                putClientConfigContent(cc, dc);
            } else {
                log.error("内容丢失，请检查T_DATA_CLOB表的数据:" + cc.getConfigContent());
            }
        }
        log.info("共加载客户端配置项条数:{}", ccList.size());
    }

    @Override
    public void reload(String configId) {
        log.info("重新加载客户端配置：{}",configId);
        SysClientConfigEntity cc = this.getById(configId);
        LambdaQueryWrapper<SysDataClobEntity> dcLq = new LambdaQueryWrapper<>();
        dcLq.eq(SysDataClobEntity::getId, cc.getConfigContent());
        SysDataClobEntity dc = iSysDataClobService.getOne(dcLq);
        if (dc != null) {
            redisUtil.hset(CacheKeyConstants.CLIENT_CONFIG, cc.getId(), JSONObject.toJSONString(cc));
            putClientConfigContent(cc, dc);
        } else {
            log.error("内容丢失，请检查T_DATA_CLOB表的数据:" + cc.getConfigContent());
        }
        log.info("重新加载客户端配置：{}完成",configId);
    }

    private void putClientConfigContent(SysClientConfigEntity cc, SysDataClobEntity dc) {
        if(StringUtil.isNotEmpty(dc.getClobData())){
            if(dc.getClobData().getBytes().length < 1024 * 1024) {
                redisUtil.set(getContentCacheKey(cc.getId()), dc.getClobData());
            }else{
                log.warn("客户端配置项{}数据超过1M",cc.getId());
            }
        }
    }


    /**
     * 刷新缓存
     */
    @Override
    public void reload() {
        log.info("重刷客户端配置缓存begin...");
        //取出列表
        Map<Object,Object> map = redisUtil.hmget(CacheKeyConstants.CLIENT_CONFIG);
        //迭代删除内容
        if(map!=null){
            Set<Object> keyItr = map.keySet();
            for(Object configId : keyItr){
                redisUtil.del(getContentCacheKey(String.valueOf(configId)));
            }
        }
        //删除列表
        redisUtil.del(CacheKeyConstants.CLIENT_CONFIG);
        load();
        log.info("重刷客户端配置缓存end");
    }

    private String getContentCacheKey(String configId){
        return CacheKeyConstants.CLIENT_CONFIG_CONTENT+CacheKeyConstants.KeySep+configId;
    }

    @RedisLock
    @Async
    @Transactional
    @Override
    public void refreshClientConfig() {
        // * 刷新客户端配置信息
        // * step1:获取t_client_config_global的config_type=load的记录
        LambdaQueryWrapper<SysClientConfigGlobalEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigType, "load");
        List<SysClientConfigGlobalEntity> gcgList = sysClientConfigGlobalMapper.selectList(queryWrapper);
        // * step2:逐条生成对应记录存储到t_client_config表中
        if (gcgList != null) {
            for (SysClientConfigGlobalEntity ccg : gcgList) {
                refreshClientConfig(ccg);
            }
        }
        // * step3:刷新缓存
        reload();
    }

    @Async
    @Transactional
    @Override
    public void refreshClientConfig(String configId) {
        // s1:获取配置项
        LambdaQueryWrapper<SysClientConfigGlobalEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigKey, configId);
        SysClientConfigGlobalEntity ccg = sysClientConfigGlobalMapper.selectOne(queryWrapper);
        // s2:对配置项生成内容
        refreshClientConfig(ccg);
        // s3:刷新缓存
        reload(configId);
    }

    private void refreshClientConfig(SysClientConfigGlobalEntity clientConfigGlobal) {
        String sql = clientConfigGlobal.getConfigValue();
        List<Map<String, Object>> ls = jdbcTemplate.queryForList(sql);
        String content = JSONObject.toJSONString(ls);
        String newConfigMd5 = MD5Util.MD5(content).toUpperCase();

        String configId = clientConfigGlobal.getConfigKey();
        SysClientConfigEntity cc = this.getById(configId);
        SysDataClobEntity dc = null;
        if (cc != null) {
            dc = this.iSysDataClobService.getById(cc.getConfigContent());
        }
        if (dc == null) {
            dc = new SysDataClobEntity();
        }
        dc.setRefTableName(SysClientConfigEntity.REF_TABLE_NAME);
        dc.setRefTableFieldName(SysClientConfigEntity.REF_TABLE_FIELD_NAME);
        dc.setClobData(content);
        boolean isChange = false;
        if (cc == null) {
            cc = new SysClientConfigEntity();
            cc.setId(configId);
            cc.setConfigMd5(newConfigMd5);
            cc.setConfigVersion("1");
            // 存储内容
            String contentId = UUIDGenerator.generate();
            dc.setId(contentId);
            cc.setConfigContent(contentId);
            isChange = true;
        } else if (StringUtils.isNotBlank(cc.getConfigContent())
                && !cc.getConfigMd5().equals(newConfigMd5)) {
            // 更新t_data_clob的记录
            dc.setId(cc.getConfigContent());
            cc.setConfigMd5(newConfigMd5);
            int configVersion = NumberUtils.toInt(cc.getConfigVersion(), 0) + 1;
            cc.setConfigVersion(String.valueOf(configVersion));
            isChange = true;
        }
        if (isChange) {
            cc.setUpdateTime(DateUtils.getCurrentTime());
            this.iSysDataClobService.saveOrUpdate(dc);
            this.saveOrUpdate(cc);
        }
    }

    @Transactional
    @Override
    public void synRefreshClientConfig(String configId) {
        // s1:获取配置项
        LambdaQueryWrapper<SysClientConfigGlobalEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigKey, configId);
        SysClientConfigGlobalEntity ccg = sysClientConfigGlobalMapper.selectOne(queryWrapper);
        // s2:对配置项生成内容
        refreshClientConfig(ccg);
        // s3:刷新缓存
        reload(configId);
    }

    @Override
    public void refreshSite() {
        log.info("刷新常用站点缓存");
//        this.refreshClientConfig(Site);
    }

    @Override
    public void refreshWenShu() {
        log.info("刷新常用文书配置缓存");
//        this.refreshClientConfig(DmWenShuConfig);
    }

    @Override
    public void refreshRygx() {
        log.info("刷新常用人员关系缓存");
//        this.refreshClientConfig(Rygx);
    }

    @Override
    public void refreshLangType() {
        log.info("刷新常用语种缓存");
//        this.refreshClientConfig(LangType);
    }

    @Override
    public void refreshDeviceSx() {
        log.info("刷新审讯设备缓存");
//        this.refreshClientConfig(DeviceSx);
    }

    @Async
    @Override
    public void refreshBllx() {
        /*log.info("刷新笔录类型缓存");
        LambdaQueryWrapper<SysClientConfigGlobalEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigKey, "DM_BLLX");
        SysClientConfigGlobalEntity ccg = sysClientConfigGlobalMapper.selectOne(queryWrapper);
        refreshClientConfig(ccg);
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigKey, "DM_BLDLX");
        ccg = sysClientConfigGlobalMapper.selectOne(queryWrapper);
        refreshClientConfig(ccg);
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigKey, "DM_XWDXLX");
        ccg = sysClientConfigGlobalMapper.selectOne(queryWrapper);
        refreshClientConfig(ccg);
        reload();*/
    }

    @Transactional
    @Async
    @Override
    public void refreshBwxbq() {
        log.info("刷新必问项不全缓存");
        LambdaQueryWrapper<SysClientConfigGlobalEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigKey,  ClientConfigIdConstants.BlQualitySet);
        SysClientConfigGlobalEntity ccg = sysClientConfigGlobalMapper.selectOne(queryWrapper);
        refreshClientConfig(ccg);
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigKey, ClientConfigIdConstants.BlQualitySetAskRule);
        ccg = sysClientConfigGlobalMapper.selectOne(queryWrapper);
        refreshClientConfig(ccg);
        reload();
    }

    @Async
    @Override
    public void refreshFuncFilter() {
        log.info("刷新客户端功能启用白名单缓存");
        LambdaQueryWrapper<SysClientConfigGlobalEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysClientConfigGlobalEntity::getConfigKey,  ClientConfigIdConstants.ClientFuncFilter);
        SysClientConfigGlobalEntity ccg = sysClientConfigGlobalMapper.selectOne(queryWrapper);
        refreshClientConfig(ccg);
        reload();
    }

    @Transactional
    @Override
    public void saveClientFunctionOpen(String checkFunction, String uncheckFunction) {
        /*前台选中的功能*/
        String[] checkArr = checkFunction.split(",");
        for(String proKey : checkArr){
            if(StringUtil.isNotEmpty(proKey)){
                SysSettingEntity entity = sysSettingService.query().eq("proKey",proKey).one();
                if(entity != null){
                    String proValue = entity.getProValue();
                    if(TRUE.equals(proValue) || FALSE.equals(proValue)){
                        entity.setProValue(TRUE);
                    }else{
                        entity.setProValue(SysConstants.YES);
                    }
                    sysSettingService.updateById(entity);
                }
            }
        }
        /*前台未选中的功能*/
        String[] uncheckArr = uncheckFunction.split(",");
        for(String proKey : uncheckArr){
            SysSettingEntity entity = sysSettingService.query().eq("proKey",proKey).one();
            if(entity != null){
                String proValue = entity.getProValue();
                if(TRUE.equals(proValue) || FALSE.equals(proValue)){
                    entity.setProValue(FALSE);
                }else{
                    entity.setProValue(SysConstants.NO);
                }
                sysSettingService.updateById(entity);
            }
        }
    }

    @Override
    public List<SysFuncVo> queryFunctionSetting(String proKeys) {
        String[] keys = proKeys.split(",");
        List<SysSettingEntity> settingList = sysSettingService.query().in("proKey", (Object) keys).list();
        List<SysFuncVo> list = new ArrayList<>();
        if(CollectionUtils.isEmpty(settingList)) {
            return list;
        }
        for(SysSettingEntity entity : settingList) {
            SysFuncVo funcVo = new SysFuncVo();
            funcVo.setProKey(entity.getProKey());
            funcVo.setProValue(entity.getProValue());
            funcVo.setModuleFilter(entity.getModuleUserFilter());
            list.add(funcVo);
        }
        return list;
    }
}
