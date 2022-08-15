package com.fardo.modules.system.config.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 软件信息表
 */
@Data
@TableName("T_SYS_SOFTWARE_INFO")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysSoftwareInfoEntity extends BaseEntity {


    /**软件名称*/
    private String  softwareName ;
    /**软件类型*/
    private String  softwareType;
    /**软件版本*/
    private String  softwareVersion;
    /**软件版本变化说明*/
    private String  versionChangeExplain;
    /**发布时间 yyyymmdd*/
    private String  releaseDate;


}
