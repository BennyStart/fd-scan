package com.fardo.modules.system.user.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
@Data
@TableName("t_sys_user_depart_roles")
public class SysUserDepartRolesEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**主键id*/
    @TableId(type = IdType.ID_WORKER_STR)
	private String id;
	/**用户id*/
	private String userId;
	/**部门id*/
	private String depId;
	/**角色id*/
	private String roleId;

	public SysUserDepartRolesEntity(String id, String userId, String depId, String roleId) {
		this.id = id;
		this.userId = userId;
		this.depId = depId;
		this.roleId = roleId;
	}

	public SysUserDepartRolesEntity(String userId, String depId, String roleId) {
		this.userId = userId;
		this.depId = depId;
		this.roleId = roleId;
	}
}
