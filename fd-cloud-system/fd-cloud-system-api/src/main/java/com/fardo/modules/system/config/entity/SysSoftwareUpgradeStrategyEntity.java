package com.fardo.modules.system.config.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 升级策略表
 */
@Data
@TableName("T_SYS_SOFT_UPGRADE_CONFIG")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysSoftwareUpgradeStrategyEntity extends BaseEntity {
    /**软件名称*/
   private String softwareName;
    /** 软件类型*/
    private String softwareType;
    /** 软件起始版本*/
    private String softwareVersionStart;
    /** 软件结束版本*/
    private String softwareVersionEnd;
    /**服务器软件起始版本*/
    private String serverSoftwareVersionStart;
    /** 服务器软件结束版本*/
    private String serverSoftwareVersionEnd;
   /** 行政区划*/
    private String area;
    /** 操作系统版本*/
    private String systemVersion;
    /**操作系统位数*/
    private String systemBit;
    /**强制升级标志*/
    private String forceFlag;
    /**升级软件版本*/
    private String upgradeSoftwareVersion;
    /**升级软件包url*/
    private String upgradeSoftwareUrl;
    /**备注*/
    private String remark;
}
