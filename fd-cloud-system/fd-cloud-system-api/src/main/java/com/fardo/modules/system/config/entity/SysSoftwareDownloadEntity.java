package com.fardo.modules.system.config.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * 软件下载信息表
 */
@Data
@TableName("T_SYS_SOFTWARE_DOWNLOAD")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysSoftwareDownloadEntity extends BaseEntity {

    /**软件信息标识*/
    @ApiModelProperty(value="软件信息标识")
    private String softwareId;
    /**下载名称'*/
    @ApiModelProperty(value="下载名称")
    private String  downloadName;
    /**下载地址*/
    @ApiModelProperty(value="下载地址")
    private String  downloadUrl;
    /**是否升级包*/
    @ApiModelProperty(value="是否升级包")
    private String  ifUpgrade;

}
