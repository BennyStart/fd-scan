/**
 * @(#)ResultCode.java
 * 版权声明  巨龙软件工程有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：李木泉
 * 时　间：2016-8-19
 * 描　述：创建
 */

package com.fardo.common.api.vo;

public enum ResultCode implements IResultCode {
    SUCCESS("0", "成功!"),
	IN_VALID_SIGN("1", "签名有误!"),
	LOGIN_TIME_OUT("2", "登录超时!"),
	ERROR("3", "服务器内部错误!"),
	ERROR_FUNCTION_NAME("4", "api不存在！"),
	KICK_ERROR("5", "你已被踢"),
	SID_ERROR("6", "无效的sid"),
	MAINTAINING("7", "系统维护中,请稍候!"),
    INVALIDPARAMETER("8", "参数无效或缺失"),
    UN_KNOWN_ERROR("12", "未知错误"),
    ENCRYPT_ERROR("13", "解密错误"),
    TEXT_TOO_LONG_ERROR("14", "字段太长,超出数据库字段的长度"),
	DUPLICATE_KEY_ERROR("15", "数据库已存在该记录"),
	DB_EXECUTE_ERROR("16", "数据库执行错误"),
	DATA_ERROR("17", "没有相关数据"),
	DATA_OTHER("18", "他人照片数据不可编辑"),
	DATA_DELETE("19", "他人照片数据不可删除"),

	DATA_SFZH("20", "相同身份证号照片已存在"),

	IS_SFZH("21", "存在身份证信息"),
	NO_SFZH("22", "不存在身份证信息"),
	ADD_ERROR("23", "操作失败"),
	EXCEL("-1", "导入格式错误"),
	REPEAT("24", "配置项代码已存在"),
	DATA_NOT_EXIST("25", "请求数据不存在或已被删除"),
	/**
	 * 9-授权文件过期（允许登录）
	 */
	INVALID_LICENSE("9", "当前系统属于未授权或者授权过期的状态，数据等各方面未得到安全保障。"
			+ "为保障系统数据等方面安全，请尽快联系系统管理员进行系统授权。"),
	/**
     * 9-实际操作人数超过授权人数（允许登录）
	 */
	INVALID_LICENSE_OF_USER("9", "当前系统的用户数已超出最大值，为保障系统的稳定运行，"
			+ "请联系管理员尽快删除部分用户，以保障系统稳定安全。"),
	/**
	* 10-授权文件过期（不允许登录）
	 */
	ERROR_LICENSE("10", "当前系统属于未授权或者授权过期的状态，数据等各方面未得到安全保障。为保障系统数据等方面安全，"
		    + "请尽快联系系统管理员进行系统授权。未取得授权将无法进行登录，请您谅解。"),
	/**
	* 11-授权文件过期,大于警告时间（允许登录）
	*/
	WARNING_INVALID_LICENSE("11","当前系统属于未授权或者授权过期的状态，数据等各方面未得到安全保障。"
			+ "为保障系统数据等方面安全，请尽快联系系统管理员进行系统授权。");

	private String resultCode;
	private String resultMsg;

	ResultCode(String resultCode, String resultMsg){
		this.resultCode = resultCode;
		this.resultMsg = resultMsg;
	}

	/**
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * @return the resultMsg
	 */
	public String getResultMsg() {
		return resultMsg;
	}

	/**
	 * @param resultMsg the resultMsg to set
	 */
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}


}
