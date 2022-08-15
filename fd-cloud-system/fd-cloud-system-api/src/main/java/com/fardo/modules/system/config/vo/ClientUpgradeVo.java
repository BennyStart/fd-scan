
package com.fardo.modules.system.config.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value="客户端升级查询条件查询参数", description="客户端升级查询条件查询参数")
public class ClientUpgradeVo {

	@NotBlank(message="软件名称不能为空")
	@ApiModelProperty(value="软件名称")
	private String softName;
	@NotBlank(message="软件类型不能为空")
	@ApiModelProperty(value="软件类型")
	private String softType;
	@NotBlank(message="软件版本号")
	@ApiModelProperty(value="软件版本号")
	private String softVer;
	@ApiModelProperty(value="行政区划")
	private String area;
	@ApiModelProperty(value="mac地址")
	private String mac;
	@ApiModelProperty(value="操作系统版本")
	private String sysVer;
	@ApiModelProperty(value="操作系统位数")
	private String sysBit;


}
