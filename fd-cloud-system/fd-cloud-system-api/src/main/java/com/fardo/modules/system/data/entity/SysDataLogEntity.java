package com.fardo.modules.system.data.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_data_log")
public class SysDataLogEntity extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String dataTable; //表名
	private String dataId; //数据ID
	private String dataContent; //数据内容
	private String dataVersion; //版本号
}
