package com.fardo.modules.system.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

;

/**
 * @Description 
 * @Author suzc
 * @Date 2021-07-23 
 */

@Data
@TableName("T_SYS_USER_PERMISSION")
public class SysUserPermissionEntity implements Serializable {

	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty(value = "ID")
	private String id;
	/**
	 * 用户id
	 */
	@ApiModelProperty("用户id")
	private String userId;

	/**
	 * 菜单id
	 */
	@ApiModelProperty("菜单id")
	private String permissionId;

	/**
	 * 排序
	 */
	@ApiModelProperty("排序")
	private Double sortNo;

	/**
	 * 功能类型，common-常用，biz-业务
	 */
	@ApiModelProperty("功能类型，common-常用，biz-业务")
	private String funcType;

}
