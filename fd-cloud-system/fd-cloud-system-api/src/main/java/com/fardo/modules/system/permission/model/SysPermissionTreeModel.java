package com.fardo.modules.system.permission.model;

import com.fardo.common.aspect.annotation.Dict;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel("菜单树信息")
public class SysPermissionTreeModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("ID")
	private String id;

	@ApiModelProperty("上级菜单")
	private String parentId;

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
	 * 菜单排序
	 */
	@ApiModelProperty("菜单排序")
	private Double sortNo;

	/**
	 * 是否叶子节点: 1:是 0:不是
	 */
	@ApiModelProperty("是否叶子节点")
	private boolean isLeaf;

	/**
	 * 删除状态 0正常 1已删除
	 */
	@ApiModelProperty("删除状态 0正常 1已删除")
	private Integer delFlag;

	/**
	 * 前端是否显示
	 */
	@ApiModelProperty("是否显示 1显示 0不显示")
	private Integer showFlag;

	public SysPermissionTreeModel() {
	}

	public SysPermissionTreeModel(SysPermissionEntity permission) {
		this.id = permission.getId();
		this.perms = permission.getPerms();
		this.delFlag = permission.getDelFlag();
		this.isLeaf = permission.isLeaf();
		this.resName = permission.getName();
		this.parentId = permission.getParentId();
		this.sortNo = permission.getSortNo();
		this.resUrl = permission.getUrl();
		this.menuType = permission.getMenuType();
		this.showFlag = permission.getShowFlag();
		if (!permission.isLeaf()) {
			this.children = new ArrayList<SysPermissionTreeModel>();
		}
	}

	@ApiModelProperty("子菜单集合")
	private List<SysPermissionTreeModel> children;

	public List<SysPermissionTreeModel> getChildren() {
		return children;
	}

	public void setChildren(List<SysPermissionTreeModel> children) {
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getResName() {
		return resName;
	}

	public void setResName(String resName) {
		this.resName = resName;
	}

	public String getResUrl() {
		return resUrl;
	}

	public void setResUrl(String resUrl) {
		this.resUrl = resUrl;
	}

	public Double getSortNo() {
		return sortNo;
	}

	public void setSortNo(Double sortNo) {
		this.sortNo = sortNo;
	}

	public Integer getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(Integer delFlag) {
		this.delFlag = delFlag;
	}


	public String getPerms() {
		return perms;
	}

	public void setPerms(String perms) {
		this.perms = perms;
	}

	public boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public Integer getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(Integer showFlag) {
		this.showFlag = showFlag;
	}
}
