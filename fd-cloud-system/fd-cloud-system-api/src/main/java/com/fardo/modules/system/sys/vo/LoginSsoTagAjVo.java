package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @(#)LoginSSOTagVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/8/3 16:02
 * 描　述：
 */
@Data
public class LoginSsoTagAjVo {

    @ApiModelProperty("功能呈现（不传入该字段时，则表示默认选择进入普通笔录制作界面）")
    private String gncx;
    @ApiModelProperty("案件编号")
    private String ajbh;
    @ApiModelProperty("案发时间")
    private String afsj;
    @ApiModelProperty("案件环节")
    private String ajhj;
    @ApiModelProperty("案件名称")
    private String ajmc;
    @ApiModelProperty("笔录类型")
    private String bllx;
    @ApiModelProperty("笔录外部ID")
    private String outId;
    @ApiModelProperty("案由编号")
    private String aybh;
    @ApiModelProperty("执法人员2集合")
    private List<LoginSsoTagZfryVo> zfry2List;
    @ApiModelProperty("人员信息集合")
    private List<LoginSsoTagRyVo> ryList;

}
