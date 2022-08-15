/**
 * @(#)LoginUser.java
 * 版权声明  巨龙软件工程有限公司, 版权所有 违者必究
 * 版本号: 
 * 修订记录:
 * 更改者：李木泉
 * 时　间：2016-8-16
 * 描　述：创建
 */

package com.fardo.modules.system.sys.vo;

import lombok.Data;

import java.util.List;

/**
 * @作者:wangbt
 * @文件名:LoginUserVo
 * @版本号:1.0
 * @创建日期:2020-12-16
 * @描述:已登录用户的vo实体
 */
@Data
public class LoginUserVo {
	private String sid;//
	private String mobilePhone;// 手机
	private String email;// 邮箱
	private String policeNo;
	private String idcard;
	private String sex;
	private String address;
	private String areaCode;
	private String username;// 用户名
	private String realname;// 真实姓名
	private String password;// 用户密码
	private Short status;// 状态1：在线,2：离线,0：禁用
	private String id;
	private String deviceFlag;// 设备码
	private Long loginTime;
	private String version;
	/**当前登录部门id*/
	private String curDepartId;
	/**当前登录角色id*/
	private String curRoleId;
	/**用户主部门*/
    private DepartVo departVo;
	/**
	 * 身份（0 普通成员 1 法度超级管理员）
	 */
	private Integer userIdentity;
	/**用户可查看数据的单位范围*/
	private List<String> orgPaths;

    /**
     * 登录方式
     */
	private String loginType;

}
