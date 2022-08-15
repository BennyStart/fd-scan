package com.fardo.modules.system.log.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统日志表
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_log")
public class SysLogEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 耗时
	 */
	private Long costTime;

	/**
	 * IP
	 */
	private String ip;

	/**
	 * 请求参数
	 */
	private String requestParam;

	/**
	 * 请求类型
	 */
	private String requestType;

	/**
	 * 请求路径
	 */
	private String requestUrl;
	/**
	 * 请求方法
	 */
	private String method;

	/**
	 * 操作人用户名称
	 */
	private String username;
	/**
	 * 操作人用户账户
	 */
	private String userid;
	/**
	 * 操作详细日志
	 */
	private String logContent;

	/**
	 * 日志类型（1登录日志，2操作日志）
	 */
	@Dict(dicCode = "log_type")
	private Integer logType;

	/**
	 * 操作类型（1查询，2添加，3修改，4删除,5导入，6导出）
	 */
	@Dict(dicCode = "operate_type")
	private Integer operateType;

}
