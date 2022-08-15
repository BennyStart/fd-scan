package com.fardo.modules.system.security.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 白名单管理
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_ip_while")
public class SysIpWhileEntity extends BaseEntity {

    /**
     * 可用类型
     */
    private String accessType;
    /**
     * 是否生效
     */
    private String isActive;
    /**
     * 起始IP
     */
    private String startIp;
    /**
     * 结束IP
     */
    private String endIp;

    private Long startLongIp;

    private Long endLongIp;

}
