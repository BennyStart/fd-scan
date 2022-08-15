package com.fardo.modules.system.personal.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;

@Data
@TableName("t_sys_personal_setting")
public class SysPersonalSettingEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 角色id
     */
    private String userRole;

    /**
     * 常用地点设置
     */
    private String address;

    /**
     * 默认类型：0为默认，1为非默认
     */
    private String type;

    /**
     * 默认类型：0为默认，1为非默认
     */
    @TableField(exist = false)
    private String typeSort;

    /**
     * 排序
     */
    private String sort;

    /**
     * 部门id
     */
    private String deptId;

}
