package com.fardo.modules.system.data.service;

import com.fardo.modules.system.data.entity.SysDataLogEntity;

import com.baomidou.mybatisplus.extension.service.IService;

public interface ISysDataLogService extends IService<SysDataLogEntity> {
	
	/**
	 * 添加数据日志
	 * @param tableName
	 * @param dataId
	 * @param dataContent
	 */
	public void addDataLog(String tableName, String dataId, String dataContent);

}
