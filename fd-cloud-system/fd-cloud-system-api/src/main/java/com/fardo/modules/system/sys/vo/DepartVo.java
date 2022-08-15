/**
 * @作者:wangbt
 * @文件名:LoginUserVo
 * @版本号:1.0
 * @创建日期:2020-12-16
 * @描述:已登录部门的vo实体
 */
package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DepartVo {
	@ApiModelProperty(value="部门id")
	private String id;
	@ApiModelProperty(value="部门名称")
	private String name;
	@ApiModelProperty(value="部门机构代码")
	private String departCode;
	@ApiModelProperty(value="部门类型")
	private String orgType;
	@ApiModelProperty(value="部门系统代码")
	private String sysCode;
	@ApiModelProperty(value="部门路径")
	private String path;
	@ApiModelProperty(value="行政区划代码")
	private String areaCode;
	@ApiModelProperty(value="行政区划名称")
	private String areaName;
	@ApiModelProperty("当前部门是否顶级单位")
	private boolean currentDeptIsTopDept;
	@ApiModelProperty("顶级单位名称")
	private String topDeptName;
	

	@Override
	public String toString() {
		String string = "单位代码："+departCode+"，单位名称："+name;
		return string;
	}

	
}
