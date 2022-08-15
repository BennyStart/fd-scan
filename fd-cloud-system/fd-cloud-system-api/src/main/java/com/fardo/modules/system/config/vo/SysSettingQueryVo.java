package com.fardo.modules.system.config.vo;

import com.fardo.common.system.vo.PageVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("客户端配置分页查询条件")
public class SysSettingQueryVo extends PageVo {


    @ApiModelProperty("配置项名称")
    private String proName;
    @ApiModelProperty("配置")
    private String proKey;
    @ApiModelProperty("配置描述")
    private String remark;

}
