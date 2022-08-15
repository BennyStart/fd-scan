package com.fardo.modules.system.config.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("T_SYS_LOGO")
@ApiModel(value = "'系统标识配置设置表'", description = "'系统标识配置设置表'")
public class SysLogoEntity {

    @ApiModelProperty(value = "'id字段'")
    private String id;
    @ApiModelProperty(value = "'位图'")
    private String theBitmap;
    @ApiModelProperty(value = "'背景图'")
    private String background;
    @ApiModelProperty(value = "'系统名称及logo'")
    private String systemNameLogo;
    @ApiModelProperty(value = "'系统内页名称'")
    private String systemInsideName;
    @ApiModelProperty(value = "'logo'")
    private String logo;
    @ApiModelProperty(value = "'版权信息")
    private String copyright;

    @TableField(exist = false)
    @ApiModelProperty(value = "'位图URL'")
    private String theBitmapUrl;
    @TableField(exist = false)
    @ApiModelProperty(value = "'背景图URL'")
    private String backgroundUrl;
    @TableField(exist = false)
    @ApiModelProperty(value = "'系统名称及logoURL'")
    private String systemNameLogoUrl;
    @TableField(exist = false)
    @ApiModelProperty(value = "'logoURL'")
    private String logoUrl;

    @ApiModelProperty(value = "'登录版权信息")
    private String loginCopyright;

}
