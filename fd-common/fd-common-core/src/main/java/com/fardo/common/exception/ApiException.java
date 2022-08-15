/**
 * 
 */
package com.fardo.common.exception;

import com.fardo.common.api.vo.IResultCode;
import com.fardo.common.api.vo.ResultVo;

public class ApiException extends IllegalStateException {

	private static final long serialVersionUID = 1797703463906900393L;
	private String status;
	private String message;
	/**
	 *
	 * @param status
	 * @param message
	 */
	public ApiException(String status, String message){
		this.status = status;
		this.message = message;
	}

	public ApiException(String status){
		this(status,"");
	}
	public ApiException(IResultCode resultCode){
		this(resultCode.getResultCode(),resultCode.getResultMsg());
	}
	
	public ResultVo getResultVo(){
		return  ResultVo.getResultVo(this.status,this.message);
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
