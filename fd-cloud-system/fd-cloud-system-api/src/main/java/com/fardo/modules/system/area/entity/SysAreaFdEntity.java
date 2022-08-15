package com.fardo.modules.system.area.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Description 汇信提供的浙江省行政区划
 * @Author suzc
 * @Date 2022-05-10 
 */

@Data
@TableName("sys_area_fd")
public class SysAreaFdEntity implements Serializable {


	/**
	 * 区划主键
	 */
	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty("区划主键")
	private Long areaId;

	/**
	 * 同步主键
	 */
	@ApiModelProperty("同步主键")
	private String oid;

	/**
	 * 区划编码
	 */
	@ApiModelProperty("区划编码")
	private String areaCode;

	/**
	 * 内容
	 */
	@ApiModelProperty("内容")
	private String content;

	/**
	 * 行政等级 1省 2地市 3区县 4乡镇/街道 5村/社区
	 */
	@ApiModelProperty("行政等级 1省 2地市 3区县 4乡镇/街道 5村/社区")
	private String level;

	/**
	 * 上级code
	 */
	@ApiModelProperty("上级code")
	private String supCode;

	/**
	 * 排序值 值越大越靠前
	 */
	@ApiModelProperty("排序值 值越大越靠前")
	private Long sort;

	/**
	 * 区划字号
	 */
	@ApiModelProperty("区划字号")
	private String docName;

	/**
	 * 区划简称
	 */
	@ApiModelProperty("区划简称")
	private String areaShortName;

	/**
	 * 国办区划编码
	 */
	@ApiModelProperty("国办区划编码")
	private String gbAreaCode;

	/**
	 * 备注
	 */
	@ApiModelProperty("备注")
	private String remark;

	/**
	 * 是否删除 1 是 0 否
	 */
	@ApiModelProperty("是否删除 1 是 0 否")
	private String deleted;

}
