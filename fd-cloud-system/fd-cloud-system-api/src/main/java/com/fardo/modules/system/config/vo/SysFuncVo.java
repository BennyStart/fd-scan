package com.fardo.modules.system.config.vo;

/**
 * 客户端功能启用情况临时实体
 */
public class SysFuncVo {
	/**功能模块名*/
	private String proKey;
	/**功能是否启用*/
	private String proValue;
	/**是否启用黑、白名单设置功能*/
	private String moduleFilter;
	
	public String getProKey() {
		return proKey;
	}
	public void setProKey(String proKey) {
		this.proKey = proKey;
	}
	public String getProValue() {
		return proValue;
	}
	public void setProValue(String proValue) {
		this.proValue = proValue;
	}
	public String getModuleFilter() {
		return moduleFilter;
	}
	public void setModuleFilter(String moduleFilter) {
		this.moduleFilter = moduleFilter;
	}
	
}
