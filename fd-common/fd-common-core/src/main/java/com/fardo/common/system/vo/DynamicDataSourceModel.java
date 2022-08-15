package com.fardo.common.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
@ApiModel(value = "数据源", description = "数据源")
public class DynamicDataSourceModel {

    public DynamicDataSourceModel() {

    }

    public DynamicDataSourceModel(Object dbSource) {
        if (dbSource != null) {
            BeanUtils.copyProperties(dbSource, this);
        }
    }

    /**
     * id
     */
    @ApiModelProperty(value = "id")
    private java.lang.String id;
    /**
     * 数据源编码
     */
    @ApiModelProperty(value = "数据源编码")
    private java.lang.String code;
    /**
     * 数据库类型
     */
    @ApiModelProperty(value = "数据库类型")
    private java.lang.String dbType;
    /**
     * 驱动类
     */
    @ApiModelProperty(value = "驱动类")
    private java.lang.String dbDriver;
    /**
     * 数据源地址
     */
    @ApiModelProperty(value = "dbUrl")
    private java.lang.String dbUrl;
    /**
     * 数据库名称
     */
    @ApiModelProperty(value = "数据库名称")
    private java.lang.String dbName;
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private java.lang.String dbUsername;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private java.lang.String dbPassword;

}
