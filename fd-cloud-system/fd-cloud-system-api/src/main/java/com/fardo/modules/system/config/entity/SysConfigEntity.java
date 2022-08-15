package com.fardo.modules.system.config.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_config")
@ApiModel(value = "配置列表")
public class SysConfigEntity extends BaseEntity {

    /**
     * 参数名称
     */
    @ApiModelProperty(value = "参数名称")
    private String name;
    /**
     * 参数代码
     */
    @ApiModelProperty(value = "参数代码")
    private String code;
    /**
     * 参数类型
     */
    @ApiModelProperty(value = "参数类型")
    private String type;
    /**
     * 参数值
     */
    @ApiModelProperty(value = "参数值")
    private String value;
    /**
     * 参数描述
     */
    @ApiModelProperty(value = "参数描述")
    private String remark;

}
