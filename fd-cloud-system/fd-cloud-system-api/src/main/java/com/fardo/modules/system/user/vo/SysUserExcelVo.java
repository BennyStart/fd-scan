package com.fardo.modules.system.user.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fardo.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @(#)SysUserExcelVo <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/25 19:47
 * 描　述：
 */
@Data
public class SysUserExcelVo {
    private String id;

    @Excel(name = "用户名", width = 15)
    private String username;
    /**
     * 真实姓名
     */
    @Excel(name = "*真实姓名", width = 15)
    private String realname;
    /**
     * 电话
     */
    @NotBlank(message = "手机号不能为空")
    @Excel(name = "*手机号", width = 15)
    private String phone;

    /**
     * 部门id(当前选择登录部门)
     */
    @Excel(name = "*单位编码", width = 15)
    private String departCode;
    /**
     * 警号
     */
    @Excel(name = "*执法证号", width = 15)
    private String policeNo;
    /**
     * 身份证号
     */
    @Excel(name = "身份证号", width = 15)
    private String idcard;

    @Excel(name = "*角色编码")
    private String roleCode;

}
