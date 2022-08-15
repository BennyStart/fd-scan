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
@TableName("t_sys_client_config_global")
public class SysClientConfigGlobalEntity {

    /** ID */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "ID")
    private String id;
    /**
     * 配置键值
     */
    private String configKey;
    /**
     * 配置类型
     */
    private String configType;
    /**
     * 配置值
     */
    private String configValue;
    /**
     * 配置描述
     */
    private String configDesc;

}
