package com.fardo.modules.system.permission.model;

import com.fardo.common.aspect.annotation.Dict;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel("菜单信息")
@Data
public class SysPermissionModel implements Serializable {

	@ApiModelProperty("ID")
	private String id;
	/**
	 * 菜单名称
	 */
	@ApiModelProperty("菜单名称")
	private String resName;
	/**
	 * 菜单权限编码
	 */
	@ApiModelProperty("菜单权限编码")
	private String perms;
	/**
	 * 跳转网页链接
	 */
	@ApiModelProperty("跳转网页链接")
	private String resUrl;
	/**
	 * 类型（0：一级菜单；1：子菜单 ；2：按钮权限）
	 */
	@Dict(dicCode = "menu_type")
	private Integer menuType;
	/**
	 * 常用功能图标
	 */
	@ApiModelProperty("菜单排序")
	private String iconCommon;
	/**
	 * 业务功能图标
	 */
	@ApiModelProperty("业务功能图标")
	private String iconBiz;

	public SysPermissionModel() {
	}

	public SysPermissionModel(SysPermissionEntity permission) {
		this.id = permission.getId();
		this.perms = permission.getPerms();
		this.resName = permission.getName();
		this.resUrl = permission.getFullUrl();
		this.menuType = permission.getMenuType();
		this.iconBiz = permission.getIconBiz();
		this.iconCommon = permission.getIconCommon();
	}

}
