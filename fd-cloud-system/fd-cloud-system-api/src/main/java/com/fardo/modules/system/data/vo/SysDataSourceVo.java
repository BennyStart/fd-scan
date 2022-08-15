package com.fardo.modules.system.data.vo;

import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 多数据源管理
 * @Author: maozf
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Data
@ApiModel(value = "sys_data_source对象", description = "多数据源管理")
public class SysDataSourceVo extends PageVo {

    @ApiModelProperty(value = "数据源编码")
    private String code;

    @ApiModelProperty(value = "数据源名称")
    private String name;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "数据库类型")
    private String dbType;

    @ApiModelProperty(value = "驱动类")
    private String dbDriver;

    @ApiModelProperty(value = "数据源地址")
    private String dbUrl;

    @ApiModelProperty(value = "数据库名称")
    private String dbName;

    @ApiModelProperty(value = "用户名")
    private String dbUsername;
}
