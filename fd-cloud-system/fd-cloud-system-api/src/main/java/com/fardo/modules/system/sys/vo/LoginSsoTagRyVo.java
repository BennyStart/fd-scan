package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class LoginSsoTagRyVo {

    @ApiModelProperty("人员身份证号")
    private String sfz;
    @ApiModelProperty("姓名")
    private String xm;
    @ApiModelProperty("职业")
    private String zy;
    @ApiModelProperty("工作单位")
    private String gzdw;
    @ApiModelProperty("联系电话")
    private String lxdh;
    @ApiModelProperty("户籍地详址")
    private String hjdxz;
    @ApiModelProperty("现住地")
    private String xzd;
    @ApiModelProperty("询问对象类型编码，101-当事人，102-证人")
    private String xwdxlx;
    @ApiModelProperty("人员类型，0-个人，1-单位")
    private String rylx;
    @ApiModelProperty("社会统一信用代码，rylx=0个人,rylx=1单位")
    private String shdm;

}
