package com.fardo.modules.system.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 多数据源管理
 * @Author: maozf
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Data
@TableName("t_sys_data_source")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "sys_data_source对象", description = "多数据源管理")
public class SysDataSourceEntity extends BaseEntity {

    /**
     * 数据源编码
     */
    @Excel(name = "数据源编码", width = 15)
    @ApiModelProperty(value = "数据源编码")
    private String code;
    /**
     * 数据源名称
     */
    @Excel(name = "数据源名称", width = 15)
    @ApiModelProperty(value = "数据源名称")
    private String name;
    /**
     * 描述
     */
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 数据库类型
     */
    @Dict(dicCode = "database_type")
    @Excel(name = "数据库类型", width = 15, dicCode = "database_type")
    @ApiModelProperty(value = "数据库类型")
    private String dbType;
    /**
     * 驱动类
     */
    @Excel(name = "驱动类", width = 15)
    @ApiModelProperty(value = "驱动类")
    private String dbDriver;
    /**
     * 数据源地址
     */
    @Excel(name = "数据源地址", width = 15)
    @ApiModelProperty(value = "数据源地址")
    private String dbUrl;
    /**
     * 数据库名称
     */
    @Excel(name = "数据库名称", width = 15)
    @ApiModelProperty(value = "数据库名称")
    private String dbName;
    /**
     * 用户名
     */
    @Excel(name = "用户名", width = 15)
    @ApiModelProperty(value = "用户名")
    private String dbUsername;
    /**
     * 密码
     */
    @Excel(name = "密码", width = 15)
    @ApiModelProperty(value = "密码")
    private String dbPassword;

    /**
     * 所属部门
     */
    @Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
}
