package com.fardo.modules.system.config.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_setting")
public class SysSettingEntity extends BaseEntity {

    /**
     * 参数类型
     */
    private String softType;
    /**
     * 参数名称
     */
    private String proName;
    /**
     * 参数代码
     */
    private String proKey;
    /**
     * 参数值
     */
    private String proValue;
    /**
     * 参数描述
     */
    private String remark;
    /**
     * 是否可编辑(0-否，1-是)
     */
    private String editable;
    /**
     * 是否模块控制参数，默认值0 ，是为1
     */
    private String isModuleSwitch;
    /**
     * 模块参数是否可设置启用过滤（设置白名单或黑名单），1：可以设置  默认为0：不可设置
     */
    private String moduleUserFilter;

}
