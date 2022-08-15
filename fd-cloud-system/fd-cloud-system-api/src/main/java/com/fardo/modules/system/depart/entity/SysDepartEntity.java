package com.fardo.modules.system.depart.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * <p>
 * 部门表
 * <p>
 *
 * @Author Steve
 * @Since  2019-01-22
 */
@Data
@TableName("t_sys_depart")
public class SysDepartEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
	/**父机构ID*/
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private String parentId;
	/**机构/部门名称*/
	@Excel(name="机构/部门名称",width=15)
	private String departName;
	/**部门拼音缩写名*/
	private String departNamePinyinAbbr;
	/**部门简称*/
	private String departNameAbbr;
	/**机构别名*/
	private String departNameAlias;
	/**部门路径*/
	private String path;
	/**机构类型*/
	private String orgType;
	/**机构编码*/
	@Excel(name="机构编码",width=15)
	private String orgCode;
	/**删除状态（0，正常，1已删除）*/
	@Dict(dicCode = "del_flag")
	private Integer delFlag;
	/**系统编码*/
	private String sysCode;
	/**行政区划编码*/
	private String areaCode;
	/**机构简介*/
	private String description;
	@ApiModelProperty("台州部门原始区域代码")
	private String originalAreaCode;
	@ApiModelProperty("台州原始dept_id")
	private String deptId;
}
