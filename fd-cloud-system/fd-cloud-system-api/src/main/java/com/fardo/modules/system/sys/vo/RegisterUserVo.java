package com.fardo.modules.system.sys.vo;

import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.vo.SysUserPositionVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @(#)RegisterUserVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/15 16:58
 * 描　述：
 */
@Data
public class RegisterUserVo {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(value="用户名")
    private String username;

    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty(value="姓名")
    private String realname;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value="密码")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "机构id不能为空")
    @ApiModelProperty("机构id")
    private String departId;

    @NotBlank(message = "角色不能为空")
    @ApiModelProperty("角色id集合,用英文逗号隔开")
    private String roleIds;

    @ApiModelProperty("是否挂职")
    private Boolean otherPositionFlag;

    @ApiModelProperty("挂职信息")
    private List<SysUserPositionVo> otherPositions;

    @ApiModelProperty("警种")
    private String manType;

    @NotBlank(message = "警号不能为空")
    @ApiModelProperty(value="警号")
    private String policeNo;

    @NotBlank(message = "身份证号码不能为空")
    @ApiModelProperty(value="身份证号码")
    private String idcard;

    @ApiModelProperty("办公电话")
    private String officephone;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("手机号")
    private String mobilephone;

    @ApiModelProperty("职位")
    private String position;

    @ApiModelProperty("邮箱")
    private String email;

    public static SysUserEntity getSysUser(SysUserEntity po, RegisterUserVo vo) {
        if(po == null) {
            po = new SysUserEntity();
        }
        po.setMobilephone(vo.getMobilephone());
        po.setDepartId(vo.getDepartId());
        po.setRealname(vo.getRealname());
        po.setPoliceNo(vo.getPoliceNo());
        po.setIdcard(vo.getIdcard());
        po.setPassword(vo.getPassword());
        po.setUsername(vo.getUsername());
        po.setAddress(vo.getAddress());
        po.setEmail(vo.getEmail());
        po.setManType(vo.getManType());
        po.setOfficephone(vo.getOfficephone());
        po.setOtherPositionFlag(vo.getOtherPositionFlag());
        po.setPosition(vo.getPosition());
        po.setSex(vo.getSex());
        return po;
    }
}
