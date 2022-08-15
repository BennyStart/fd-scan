/**
* 版权所有：厦门市巨龙软件工程有限公司
* Copyright 2015 Xiamen Dragon Software Eng. Co. Ltd.
* All right reserved. 
*====================================================
* 文件名称: UserVo.java
* 修订记录：
* No    日期				作者(操作:具体内容)
* 1.    2015年5月18日		hongjy(创建:创建文件)
*====================================================
* 类描述：(说明未实现或其它不应生成javadoc的内容)
* 
*/
package com.fardo.modules.system.sys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *<pre><b><font color="blue">UserVo</font></b></pre>
 *
 *<pre><b>&nbsp;--描述说明--</b></pre>
 * <pre></pre>
 * <pre>
 * <b>--样例--</b>
 *   UserVo obj = new UserVo();
 *   obj.method();
 * </pre>
 * JDK版本：JDK1.4
 * @author  <b>hongjy</b> 
 */
@Data
public class UserVo {
	/**
	 * 用户ID
	 */
	@ApiModelProperty(value = "用户ID")
	private String userId;
	/**
	 * 用户名登陆名
	 */
    @ApiModelProperty(value = "用户名")
	private String userName;
	/**
	 * 用户真实名
	 */
    @ApiModelProperty(value = "用户真实姓名")
	private String name;
	/**
	 * 用户身份证号
	 */
	@ApiModelProperty(value="用户身份证号")
	private String idCard;
	/**
	 * 用户警号
	 */
	@ApiModelProperty(value="用户警号")
	private String policeNo;
	
	/**
	 * 用户部门ID
	 */
	@ApiModelProperty(value="用户部门id")
	private String deptId;
	/**
	 * 顶级部门ID
	 */
	@ApiModelProperty(value="用户顶级部门id")
	private String topDeptId;
	@ApiModelProperty(value="手机号")
	private String mobilePhone;
	
	/**
	 * 是否需要修改密码
	 */
	@ApiModelProperty(value="是否需要修改密码")
	private String needChangePwd;
	/**
	 * 需要修改密码的提示信息
	 */
	@ApiModelProperty(value="需要修改密码的提示信息")
	private String needChangePwdInfo;
	@ApiModelProperty(value="部门代码")
	private String deptCode;
	@ApiModelProperty(value="加密的密码")
	private String pwd;
	@ApiModelProperty(value="性别（1-男，2-女）")
	private String sex;
	
}
