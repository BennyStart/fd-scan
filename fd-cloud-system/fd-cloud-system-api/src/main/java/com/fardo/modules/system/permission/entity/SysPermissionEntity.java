package com.fardo.modules.system.permission.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_permission")
public class SysPermissionEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 父id
	 */
	@Excel(name = "上级菜单")
	private String parentId;

	/**
	 * 名称
	 */
	@Excel(name = "菜单名称")
	private String name;

	/**
	 * 菜单权限编码
	 */
	@Excel(name = "菜单编码")
	private String perms;

	/**
	 * 路径
	 */
	@Excel(name = "前端地址")
	private String url;

	/**
	 * 菜单排序
	 */
	@Excel(name = "菜单顺序")
	private Double sortNo;

	/**
	 * 类型（0：一级菜单；1：子菜单 ；2：按钮权限）
	 */
	@Dict(dicCode = "menu_type")
	@Excel(name = "类型")
	private Integer menuType;

	/**
	 * 是否叶子节点: 1:是  0:不是
	 */
	@TableField(value="is_leaf")
	private boolean leaf;

	/**
	 * 删除状态 0正常 1已删除
	 */
	private Integer delFlag;

	/**
	 * 是否显示在首页全部功能
	 */
	private Integer indexFlag;
	/**
	 * 常用功能图标
	 */
	private String iconCommon;
	/**
	 * 业务功能图标
	 */
	private String iconBiz;

	/**
	 * 完整url
	 */
	private String fullUrl;
	/**
	 * 前端是否显示
	 */
	private Integer showFlag;
	/**
	 * 功能启用管理是否显示
	 */
	private Integer showManage;

    public SysPermissionEntity() {

    }
}
