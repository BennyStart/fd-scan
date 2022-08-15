package com.fardo.common.system.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: Entity基类
 * @Author: maozf
 * @Date: 20-11-21
 * @Version: 1.1
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/** ID */
	@TableId(type = IdType.ID_WORKER_STR)
	@ApiModelProperty(value = "ID")
	private String id;
	/** 创建人 */
	@ApiModelProperty(value = "创建人")
	@Excel(name = "创建人", width = 15)
	private String createBy;
	/** 创建时间 */
	@ApiModelProperty(value = "创建时间")
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	private String createTime;
	/** 更新人 */
	@ApiModelProperty(value = "更新人")
	@Excel(name = "更新人", width = 15)
	private String updateBy;
	/** 更新时间 */
	@ApiModelProperty(value = "更新时间")
	@Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	private String updateTime;
}
