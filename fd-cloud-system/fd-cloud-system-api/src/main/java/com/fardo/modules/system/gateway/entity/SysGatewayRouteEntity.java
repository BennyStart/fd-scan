package com.fardo.modules.system.gateway.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * @Description: gateway路由管理
 * @Author: maozf
 * @Date:   2020-05-26
 * @Version: V1.0
 */
@Data
@TableName("t_sys_gateway_route")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_gateway_route对象", description="gateway路由管理")
public class SysGatewayRouteEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**服务名*/
	@Excel(name = "服务名", width = 15)
    @ApiModelProperty(value = "服务名")
    private String name;

	/**服务地址*/
	@Excel(name = "服务地址", width = 15)
    @ApiModelProperty(value = "服务地址")
    private String uri;

    /**
     * 断言配置
     */
	private String predicates;

    /**
     * 过滤配置
     */
	private String filters;

    /**是否忽略前缀0-否 1-是*/
    @Excel(name = "忽略前缀", width = 15)
    @ApiModelProperty(value = "忽略前缀")
    @Dict(dicCode = "yn")
    private Integer stripPrefix;

    /**是否重试0-否 1-是*/
    @Excel(name = "是否重试", width = 15)
    @ApiModelProperty(value = "是否重试")
    @Dict(dicCode = "yn")
    private Integer retryable;

    /**是否为保留数据:0-否 1-是*/
    @Excel(name = "是否重试", width = 15)
    @ApiModelProperty(value = "保留数据")
    @Dict(dicCode = "yn")
    private Integer persist;

    /**是否在接口文档中展示:0-否 1-是*/
    @Excel(name = "在接口文档中展示", width = 15)
    @ApiModelProperty(value = "在接口文档中展示")
    @Dict(dicCode = "yn")
    private Integer showApi;

    /**状态 1有效 0无效*/
    @Excel(name = "状态", width = 15)
    @ApiModelProperty(value = "状态")
    @Dict(dicCode = "yn")
    private Integer status;
}
