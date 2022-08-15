package com.fardo.modules.system.config.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_client_config")
public class SysClientConfigEntity {

    /** ID */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "ID")
    private String id;
    /**
     * 配置信息md5值
     */
    private String configMd5;
    /**
     * 配置信息版本号
     */
    private String configVersion;
    /**
     * 配置信息内容,json格式,内容引用T_DATA_CLOB表存储
     */
    private String configContent;
    /**
     * 更新时间，yyyyMMddHHmmSS
     */
    private String updateTime;

    public static final String TABLE_ALIAS = "T_SYS_CLIENT_CONFIG";
    public static final String REF_TABLE_NAME = TABLE_ALIAS;
    public static final String REF_TABLE_FIELD_NAME = "CONFIG_CONTENT";

}
