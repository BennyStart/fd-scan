package com.fardo.modules.system.config.service.impl;

import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.exception.ApiException;
import com.fardo.modules.system.area.service.ISysAreaService;
import com.fardo.modules.system.config.entity.SysSoftwareDownloadEntity;
import com.fardo.modules.system.config.entity.SysSoftwareInfoEntity;
import com.fardo.modules.system.config.entity.SysSoftwareUpgradeStrategyEntity;
import com.fardo.modules.system.config.mapper.SysSoftwareDownloadMapper;
import com.fardo.modules.system.config.mapper.SysSoftwareInfoMapper;
import com.fardo.modules.system.config.mapper.SysSoftwareUpgradeStrategyMapper;
import com.fardo.modules.system.config.model.SysSoftwareUpgradeStrategyModel;
import com.fardo.modules.system.config.service.ISysSoftwareUpgradeStrategyService;
import com.fardo.modules.system.config.vo.ClientUpgradeVo;
import com.fardo.modules.system.config.vo.SysSoftwareUpgradeStrategyVo;
import com.fardo.modules.system.config.vo.UpgradeQueryResult;
import com.fardo.modules.system.sys.vo.ParamVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service("sysSoftwareUpgradeStrategyService")
public class SysSoftwareUpgradeStrategyServiceImpl extends ServiceImpl<SysSoftwareUpgradeStrategyMapper, SysSoftwareUpgradeStrategyEntity> implements ISysSoftwareUpgradeStrategyService {
    private static final String SPLIT_CHAR = ".";
    @Value("${project.version}")
    private String serverVersion;

    @Autowired
    private SysSoftwareUpgradeStrategyMapper softwareUpgradeStrategyMapper;

    @Autowired
    private SysSoftwareInfoMapper softwareInfoMapper;
    @Autowired
    private ISysAreaService sysAreaService;
    @Autowired
    private SysSoftwareDownloadMapper softwareDownloadMapper;

    @Override
    public IPage<SysSoftwareUpgradeStrategyModel> getPageList(SysSoftwareUpgradeStrategyVo softwareUpgradeStrategyVo) {
        LambdaQueryWrapper<SysSoftwareUpgradeStrategyEntity> lambdaQueryWrapper = new LambdaQueryWrapper<SysSoftwareUpgradeStrategyEntity>();
        IPage<SysSoftwareUpgradeStrategyModel> modelIPage = new Page<>();
        IPage<SysSoftwareUpgradeStrategyEntity> page = new Page<>(softwareUpgradeStrategyVo.getPageNo(), softwareUpgradeStrategyVo.getPageSize());
        //软件名称模糊查询
        if(StringUtils.isNoneBlank(softwareUpgradeStrategyVo.getSoftwareName())){
            lambdaQueryWrapper.like(SysSoftwareUpgradeStrategyEntity::getSoftwareName,softwareUpgradeStrategyVo.getSoftwareName());
        }
        //软件类型
        if(StringUtils.isNoneBlank(softwareUpgradeStrategyVo.getSoftwareType())){
            lambdaQueryWrapper.eq(SysSoftwareUpgradeStrategyEntity::getSoftwareType,softwareUpgradeStrategyVo.getSoftwareType());
        }
        IPage<SysSoftwareUpgradeStrategyEntity> pageList = softwareUpgradeStrategyMapper.selectPage(page, lambdaQueryWrapper);
        List<SysSoftwareUpgradeStrategyModel> list = new ArrayList<>(pageList.getRecords().size());
        pageList.getRecords().forEach(m->{
            SysSoftwareUpgradeStrategyModel model = new SysSoftwareUpgradeStrategyModel();
            BeanUtils.copyProperties(m,model);
            list.add(model);
        });
        modelIPage.setRecords(list);
        modelIPage.setSize(pageList.getSize());
        modelIPage.setTotal(pageList.getTotal());
        modelIPage.setPages(pageList.getPages());
        return modelIPage;
    }

    @Override
    @Transactional
    public void updateUpgradeStrategy(SysSoftwareUpgradeStrategyVo softwareUpgradeStrategyVo) {
        SysSoftwareUpgradeStrategyEntity upgradeStrategy = this.getById(softwareUpgradeStrategyVo.getId());
        if(upgradeStrategy==null) {
            throw new RuntimeException("未找到对应实体");
        }
        BeanUtils.copyProperties(softwareUpgradeStrategyVo, upgradeStrategy);
        this.updateById(upgradeStrategy);
    }

    @Override
    public ResultVo<UpgradeQueryResult> clientUpgradeQuery(ParamVo<ClientUpgradeVo> paramVo) {
        ClientUpgradeVo clientUpgradeVo = paramVo.getData();
        String softType = clientUpgradeVo.getSoftType();
        String softVer = clientUpgradeVo.getSoftVer();
        String softName = clientUpgradeVo.getSoftName();
        String area = clientUpgradeVo.getArea();
        //根据软件名称（ZNBL）、软件类型、客户端版本号、服务端版本号、行政区划获取升级策略——>获取升级目标版本
        String serverVer= redoServerVersion();
        LambdaQueryWrapper<SysSoftwareUpgradeStrategyEntity> upgradeStrategyWrapper = new LambdaQueryWrapper<SysSoftwareUpgradeStrategyEntity>();
        upgradeStrategyWrapper.eq(SysSoftwareUpgradeStrategyEntity::getSoftwareName,softName)
        .eq(SysSoftwareUpgradeStrategyEntity::getSoftwareType,softType)
        .gt(SysSoftwareUpgradeStrategyEntity::getUpgradeSoftwareVersion,softVer)//大于要升级的版本
        .and(i ->i.ge(SysSoftwareUpgradeStrategyEntity::getServerSoftwareVersionEnd,serverVer).or().isNull(SysSoftwareUpgradeStrategyEntity::getServerSoftwareVersionEnd))
        .and(i ->i.le(SysSoftwareUpgradeStrategyEntity::getServerSoftwareVersionStart,serverVer).or().isNull(SysSoftwareUpgradeStrategyEntity::getServerSoftwareVersionStart))
        .and(i ->i.ge(SysSoftwareUpgradeStrategyEntity::getSoftwareVersionEnd,softVer).or().isNull(SysSoftwareUpgradeStrategyEntity::getSoftwareVersionEnd))
        .and(i ->i.le(SysSoftwareUpgradeStrategyEntity::getSoftwareVersionStart,softVer).or().isNull(SysSoftwareUpgradeStrategyEntity::getSoftwareVersionStart));
        if (StringUtil.isNotBlank(area)) {//行政区划
            upgradeStrategyWrapper.and(i ->i.le(SysSoftwareUpgradeStrategyEntity::getArea, area).or().isNull(SysSoftwareUpgradeStrategyEntity::getArea));
        }
        upgradeStrategyWrapper.orderByDesc(SysSoftwareUpgradeStrategyEntity::getCreateTime);
        List<SysSoftwareUpgradeStrategyEntity> list = softwareUpgradeStrategyMapper.selectList(upgradeStrategyWrapper);

        UpgradeQueryResult upgradeQueryResult = new UpgradeQueryResult();
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() > 1) {
                log.warn("升级策略，客户端版本："+softVer+"，服务端版本："+serverVer+"，出现多条升级策略！");
            }
            SysSoftwareUpgradeStrategyEntity temp = list.get(0);
            for (SysSoftwareUpgradeStrategyEntity t : list) {
                if (t.getUpgradeSoftwareVersion().compareTo(temp.getUpgradeSoftwareVersion()) > 0) {
                    temp = t;
                }
            }
            //根据软件名称（ZNBL）、软件类型、软件版本获取软件信息表
            LambdaQueryWrapper<SysSoftwareInfoEntity> softwareInfoWrapper = new LambdaQueryWrapper<>();
            softwareInfoWrapper.eq(SysSoftwareInfoEntity::getSoftwareName,softName)
                    .eq(SysSoftwareInfoEntity::getSoftwareType,softType)
                    .eq(SysSoftwareInfoEntity::getSoftwareVersion,temp.getUpgradeSoftwareVersion());
            List<SysSoftwareInfoEntity> softInfoList = softwareInfoMapper.selectList(softwareInfoWrapper);
            if (CollectionUtils.isNotEmpty(softInfoList)) {
                SysSoftwareInfoEntity softInfo = softInfoList.get(0);
                upgradeQueryResult.setForceFlag(temp.getForceFlag());
                upgradeQueryResult.setUpgradeFlag(temp.getUpgradeSoftwareVersion());
                upgradeQueryResult.setSoftVerChange(softInfo.getVersionChangeExplain());
                upgradeQueryResult.setUpgradeFlag("1");
                //根据软件信息id获取对应软件下载地址
                LambdaQueryWrapper<SysSoftwareDownloadEntity> softeDownWrapper = new LambdaQueryWrapper<>();
                softeDownWrapper.eq(SysSoftwareDownloadEntity::getSoftwareId,softInfo.getId());
                List<SysSoftwareDownloadEntity> downList = softwareDownloadMapper.selectList(softeDownWrapper);
                if (CollectionUtils.isNotEmpty(downList)) {
                    //分流,随机返回下载地址
                    Random random = new Random();
                    int index = random.nextInt(downList.size());
                    String upgradeUrl = downList.get(index).getDownloadUrl();
                    upgradeQueryResult.setUpgradeUrl(upgradeUrl);
                } else {
                    throw new ApiException("101", "未找到升级下载地址，无需升级");
                }
            } else {
                throw new ApiException("101", "未找到升级下载地址，无需升级");
            }
        }
        ResultVo<UpgradeQueryResult> result = ResultVo.getResultVo(ResultCode.SUCCESS);
        result.setResults(upgradeQueryResult);
        return result;
    }

    private String redoServerVersion() {
        String[] verArr = serverVersion.split("\\"+SPLIT_CHAR);
        StringBuilder versionSb = new StringBuilder();
        int i = 1;
        for (String ver : verArr) {
            if (ver.length() < 2) {
                versionSb.append("0");
            }
            versionSb.append(ver);
            if (i != verArr.length) {
                versionSb.append(SPLIT_CHAR);
            }
            ++i;
        }
        return versionSb.toString();
    }

}
