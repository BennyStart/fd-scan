package com.fardo.modules.system.config.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.config.entity.SysSoftwareInfoEntity;
import com.fardo.modules.system.config.model.SysSoftwareInfoModel;
import com.fardo.modules.system.config.vo.SysSoftwareInfoVo;

public interface ISysSoftwareInfoService extends IService<SysSoftwareInfoEntity> {
    /**
     * 软件下载配置列表查询
     * @param sysSoftwareInfoVo
     * @return
     */
    IPage<SysSoftwareInfoModel> getPageList(SysSoftwareInfoVo sysSoftwareInfoVo);

    /**
     * 新增软件下载配置
     * @param sysSoftwareInfoVo
     */
    void addSysSoftwareInfo(SysSoftwareInfoVo sysSoftwareInfoVo);

    /**
     * 更新软件下载配置
     * @param sysSoftwareInfoVo
     */
    void updateSysSoftwareInfo(SysSoftwareInfoVo sysSoftwareInfoVo);

    /**
     * 删除软件下载配置
     * @param sysSoftwareInfoVo
     */
    void deleteSoftwareInfo(String id);



}
