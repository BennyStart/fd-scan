package com.fardo.modules.system.data.mapper;

import org.apache.ibatis.annotations.Param;
import com.fardo.modules.system.data.entity.SysDataLogEntity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface SysDataLogMapper extends BaseMapper<SysDataLogEntity>{
	/**
	 * 通过表名及数据Id获取最大版本
	 * @param tableName
	 * @param dataId
	 * @return
	 */
	public String queryMaxDataVer(@Param("tableName") String tableName,@Param("dataId") String dataId);
	
}
