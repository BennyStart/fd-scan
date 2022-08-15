package com.fardo.modules.system.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("导入任务进度信息")
public class TaskResultBaseVo {
	

	public static final int SUCCESS = 1;

	public static final int DOING = 2;

	public static final int FAIL = -1;

	@ApiModelProperty("任务执行结果，（1-成功，2-进行中，-1-失败）")
	private int result;
	@ApiModelProperty("返回消息")
	private String msg;
	@ApiModelProperty("任务总数")
	private int totalCount = 0;
	@ApiModelProperty("任务完成数")
	private int doneCount = 0;
	@ApiModelProperty("任务失败数")
	private int failCount = 0;
	@ApiModelProperty("任务总耗时")
	private long elapsedTime;
	@ApiModelProperty("任务进度")
	private Double progress;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getDoneCount() {
		return doneCount;
	}

	public void setDoneCount(int doneCount) {
		this.doneCount = doneCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public Double getProgress() {
		if(totalCount == 0) {
			this.progress = 0.0;
		}else{
			this.progress = (doneCount + failCount)* 100.0 / totalCount ;
		}
		return progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}

	public void refresh(int result, String msg, long elapsedTime) {
		this.result = result;
		this.msg = msg;
		this.elapsedTime = elapsedTime;
	}
	
	public void init() {
		this.totalCount = 0;
		this.failCount = 0;
		this.doneCount = 0;
	}
}
