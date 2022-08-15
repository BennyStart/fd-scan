package com.fardo.modules.system.log.service.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fardo.common.system.api.ISysBaseAPI;
import com.fardo.modules.system.log.entity.SysLogEntity;
import com.fardo.modules.system.log.mapper.SysLogMapper;
import com.fardo.modules.system.log.service.ISysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * <p>
 * 系统日志表 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLogEntity> implements ISysLogService {

	@Resource
	private SysLogMapper sysLogMapper;
	@Autowired
	private ISysBaseAPI sysBaseAPI;
	
	/**
	 * @功能：清空所有日志记录
	 */
	@Override
	public void removeAll() {
		sysLogMapper.removeAll();
	}

	@Override
	public Long findTotalVisitCount() {
		return sysLogMapper.findTotalVisitCount();
	}

	//update-begin--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数
	@Override
	public Long findTodayVisitCount(Date dayStart, Date dayEnd) {
		return sysLogMapper.findTodayVisitCount(dayStart,dayEnd);
	}

	@Override
	public Long findTodayIp(Date dayStart, Date dayEnd) {
		return sysLogMapper.findTodayIp(dayStart,dayEnd);
	}
	//update-end--Author:zhangweijian  Date:20190428 for：传入开始时间，结束时间参数

	@Override
	public List<Map<String,Object>> findVisitCount(Date dayStart, Date dayEnd) {
		try {
			String dbType = sysBaseAPI.getDatabaseType();
			return sysLogMapper.findVisitCount(dayStart, dayEnd,dbType);
		} catch (SQLException e) {
		}
		return null;
	}
}
