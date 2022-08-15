package com.fardo.modules.system.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_user")
public class SysUserEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录账号
     */
    @Excel(name = "登录账号", width = 15)
    private String username;

    /**
     * 真实姓名
     */
    @Excel(name = "真实姓名", width = 15)
    private String realname;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * md5密码盐
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String salt;

    /**
     * 头像
     */
    @Excel(name = "头像", width = 15,type = 2)
    private String avatar;

    /**
     * 生日
     */
    @Excel(name = "生日", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 性别（1：男 2：女）
     */
    @Excel(name = "性别", width = 15,dicCode="sex")
    @Dict(dicCode = "sex")
    private String sex;

    /**
     * 电子邮件
     */
    @Excel(name = "电子邮件", width = 15)
    private String email;

    @Excel(name = "手机号", width = 15)
    private String mobilephone;

    @Excel(name = "办公电话", width = 15)
    private String officephone;
    /**
     * 所在部门id(用户主部门)
     */
    private String departId;
    /**
     * 登录的部门id
     */
    private String loginDepartId;
    /**
     * 登录的角色id
     */
    private String loginRoleId;
    /**
     * 状态(1：正常  2：冻结 ）
     */
    @Excel(name = "状态", width = 15,dicCode="user_status")
    @Dict(dicCode = "user_status")
    private String status;

    /**
     * 删除状态（0，正常，1已删除）
     */
    @Excel(name = "删除状态", width = 15,dicCode="del_flag")
    private Integer delFlag;

    /**
     * 警号
     */
    @Excel(name = "警号", width = 15)
    private String policeNo;

    /**
     * 身份证号
     */
    @Excel(name = "身份证号", width = 15)
    private String idcard;

    /**
     * 身份（0 普通成员 1 法度超级管理员）
     */
    @Excel(name="（1普通成员 2法度超级管理员）",width = 15)
    private Integer userIdentity;

    /**
     * 1正常管理员2.单位管理员 默认1
     */
    private String type;

    /**
     * pc端的登录sesseionID
     */
    private String pcSid;
    /**
     * mobile的登录sessionID
     */
    private String mobileSid;
    /**
     * 登录ip
     */
    private String loginIp;
    /**
     * 是否需要修改密码
     */
    private String needChangePwd;
    /**
     * 地址
     */
    private String address;
    /**
     * 职位
     */
    private String position;
    /**
     * 警种
     */
    private String manType;
    /**
     * 登录模式
     */
    private String loginModes;

    /**
     * 是否挂职
     */
    private Boolean otherPositionFlag;
    /**
     * 真实姓名拼音
     */
    private String realnamePy;

    @TableField(exist = false)
    private String userUid;
}
