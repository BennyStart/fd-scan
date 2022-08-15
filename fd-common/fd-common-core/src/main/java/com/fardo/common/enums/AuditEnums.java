/**
 * 版权所有：厦门市巨龙软件工程有限公司
 * Copyright 2016 Xiamen Dragon Software Eng. Co. Ltd.
 * All right reserved.
 *====================================================
 * 文件名称: AuditEnums.java
 * 修订记录：
 * No    日期				作者(操作:具体内容)
 * 1.    2016年1月26日		laihm_fz(创建:创建文件)
 *====================================================
 * 类描述：(说明未实现或其它不应生成javadoc的内容)
 *
 */
package com.fardo.common.enums;


public enum AuditEnums implements CodeEnum {

    UNAUDIT("0","未审核"),
	AGREE("1","审核通过"),
	DISAGREE("2","审核不通过"),

	YES("1","是"),
	NO("0","否");
	
	AuditEnums(String value, String name){
		this.value = value;
		this.name = name;
	}

    public static String getNameByValue(String value) {
        for(AuditEnums auditEnum : AuditEnums.values()) {
            if(value.equals(auditEnum.getValue())) {
                return auditEnum.getName();
            }
        }
        return "";
    }


	private String value;
	
	private String name;
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getName() {
		return name;
	}

}
