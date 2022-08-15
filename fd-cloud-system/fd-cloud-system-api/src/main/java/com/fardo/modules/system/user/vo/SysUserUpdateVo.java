package com.fardo.modules.system.user.vo;

import com.fardo.modules.system.user.entity.SysUserEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @(#)SysUserVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/15 16:58
 * 描　述：
 */
@ApiModel("更新用户信息")
@Data
public class SysUserUpdateVo {

    @NotBlank(message = "id不能为空")
    @ApiModelProperty(value="id")
    private String id;

    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty(value="姓名",required = true)
    private String realname;

    @NotBlank(message = "机构id不能为空")
    @ApiModelProperty("机构id")
    private String departId;

    @ApiModelProperty(value="机构名称")
    private String departName;

    @NotBlank(message = "角色不能为空")
    @ApiModelProperty("角色id集合,用英文逗号隔开")
    private String roleIds;

    @ApiModelProperty("角色名称集合,用英文逗号隔开")
    private String roleNames;

    @ApiModelProperty("是否挂职")
    private Boolean otherPositionFlag;

    @ApiModelProperty("挂职信息")
    private List<SysUserPositionVo> otherPositions;

    @ApiModelProperty("警种")
    private String manType;

    @NotBlank(message = "执法证号不能为空")
    @ApiModelProperty(value="执法证号")
    private String policeNo;

    // @NotBlank(message = "身份证号码不能为空")
    @ApiModelProperty(value="身份证号码")
    private String idcard;

    @ApiModelProperty("办公电话")
    private String officephone;

    @ApiModelProperty("性别")
    private String sex;

    @Size(max = 50,message = "地址长度不能超过50")
    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("手机号")
    private String mobilephone;
    @Size(max = 50,message = "职位长度不能超过50")
    @ApiModelProperty("职位")
    private String position;

    @Size(max = 40,message = "邮箱长度不能超过40")
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("登录方式，多个用英文逗号隔开")
    private String loginModes;

    public static SysUserEntity getSysUser(SysUserEntity po, SysUserUpdateVo vo) {
        if(po == null) {
            po = new SysUserEntity();
        }
        po.setMobilephone(vo.getMobilephone());
        po.setDepartId(vo.getDepartId());
        po.setRealname(vo.getRealname());
        po.setPoliceNo(vo.getPoliceNo());
        po.setIdcard(vo.getIdcard());
        po.setAddress(vo.getAddress());
        po.setEmail(vo.getEmail());
        po.setLoginModes(vo.getLoginModes());
        po.setManType(vo.getManType());
        po.setOfficephone(vo.getOfficephone());
        po.setOtherPositionFlag(vo.getOtherPositionFlag());
        po.setPosition(vo.getPosition());
        po.setSex(vo.getSex());
        return po;
    }
}
