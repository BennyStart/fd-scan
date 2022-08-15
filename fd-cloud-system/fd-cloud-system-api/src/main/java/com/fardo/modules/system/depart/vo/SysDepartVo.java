package com.fardo.modules.system.depart.vo;

import com.fardo.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @(#)SysDepartVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/12 14:30
 * 描　述：
 */
@Data
@ApiModel(value="机构信息", description="机构信息")
public class SysDepartVo {
    @ApiModelProperty(value="机构id(新增放空，修改必填)")
    private String id;
    @ApiModelProperty(value="父级机构id")
    private String parentId;
    @ApiModelProperty(value="父级机构名称")
    private String parentName;
    @NotBlank(message = "机构名称不能为空")
    @Length(max = 50, message = "机构名称最多可输入50个字")
    @ApiModelProperty(value="机构名称")
    private String departName;
    @NotBlank(message = "机构简称不能为空")
    @Length(max = 30, message = "机构简称最多可输入30个字")
    @ApiModelProperty(value="机构简称")
    private String departNameAbbr;
    @NotBlank(message = "机构别名不能为空")
    @Length(max = 50, message = "机构别名最多可输入50个字")
    @ApiModelProperty(value="机构别名")
    private String departNameAlias;
    @NotBlank(message = "机构代码不能为空")
    @Length(max = 16, message = "机构代码最多可输入16个字")
    @ApiModelProperty(value="机构代码")
    private String orgCode;
    /**机构类型*/
    @ApiModelProperty(value="机构类型，字典-ORG_TYPE")
    @Dict(dicCode = "ORG_TYPE")
    private String orgType;
    /**行政区划编码*/
    @ApiModelProperty(value="行政区划，字典-PROVINCE_AREA")
    private String areaCode;
    @ApiModelProperty(value="上级行政区划，字典-PROVINCE_AREA")
    private String parentAreaCode;
    /**机构简介*/
    @Length(max = 500, message = "机构简介最多可输入500个字")
    @ApiModelProperty(value="机构简介")
    private String description;
}
