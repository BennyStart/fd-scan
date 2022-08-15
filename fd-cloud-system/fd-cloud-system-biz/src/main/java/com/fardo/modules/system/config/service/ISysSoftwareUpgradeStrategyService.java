package com.fardo.modules.system.config.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.modules.system.config.entity.SysSoftwareUpgradeStrategyEntity;
import com.fardo.modules.system.config.model.SysSoftwareUpgradeStrategyModel;
import com.fardo.modules.system.config.vo.ClientUpgradeVo;
import com.fardo.modules.system.config.vo.SysSoftwareUpgradeStrategyVo;
import com.fardo.modules.system.config.vo.UpgradeQueryResult;
import com.fardo.modules.system.sys.vo.ParamVo;

public interface ISysSoftwareUpgradeStrategyService extends IService<SysSoftwareUpgradeStrategyEntity> {
    IPage<SysSoftwareUpgradeStrategyModel> getPageList(SysSoftwareUpgradeStrategyVo softwareUpgradeStrategyVo);

    void updateUpgradeStrategy(SysSoftwareUpgradeStrategyVo softwareUpgradeStrategyVo);

    /**
     * 客户端升级查询接口
     * @param paramVo
     * @return
     */
    ResultVo<UpgradeQueryResult> clientUpgradeQuery(ParamVo<ClientUpgradeVo> paramVo);
}
