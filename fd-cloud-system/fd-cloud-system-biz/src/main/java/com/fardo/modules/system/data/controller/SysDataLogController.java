package com.fardo.modules.system.data.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.fardo.common.api.vo.Result;
import com.fardo.modules.system.data.entity.SysDataLogEntity;
import com.fardo.modules.system.data.service.ISysDataLogService;
import com.fardo.common.system.query.QueryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sys/dataLog")
@Slf4j
public class SysDataLogController {
	@Autowired
	private ISysDataLogService service;
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysDataLogEntity>> queryPageList(SysDataLogEntity dataLog, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
		Result<IPage<SysDataLogEntity>> result = new Result<IPage<SysDataLogEntity>>();
		QueryWrapper<SysDataLogEntity> queryWrapper = QueryGenerator.initQueryWrapper(dataLog, req.getParameterMap());
		Page<SysDataLogEntity> page = new Page<SysDataLogEntity>(pageNo, pageSize);
		IPage<SysDataLogEntity> pageList = service.page(page, queryWrapper);
		log.info("查询当前页："+pageList.getCurrent());
		log.info("查询当前页数量："+pageList.getSize());
		log.info("查询结果数量："+pageList.getRecords().size());
		log.info("数据总数："+pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}
	
	/**
	 * 查询对比数据
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/queryCompareList", method = RequestMethod.GET)
	public Result<List<SysDataLogEntity>> queryCompareList(HttpServletRequest req) {
		Result<List<SysDataLogEntity>> result = new Result<>();
		String dataId1 = req.getParameter("dataId1");
		String dataId2 = req.getParameter("dataId2");
		List<String> idList = new ArrayList<String>();
		idList.add(dataId1);
		idList.add(dataId2);
		try {
			List<SysDataLogEntity> list =  (List<SysDataLogEntity>) service.listByIds(idList);
			result.setResult(list);
			result.setSuccess(true);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return result;
	}
	
	/**
	 * 查询版本信息
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/queryDataVerList", method = RequestMethod.GET)
	public Result<List<SysDataLogEntity>> queryDataVerList(HttpServletRequest req) {
		Result<List<SysDataLogEntity>> result = new Result<>();
		String dataTable = req.getParameter("dataTable");
		String dataId = req.getParameter("dataId");
		QueryWrapper<SysDataLogEntity> queryWrapper = new QueryWrapper<SysDataLogEntity>();
		queryWrapper.eq("data_table", dataTable);
		queryWrapper.eq("data_id", dataId);
		List<SysDataLogEntity> list = service.list(queryWrapper);
		if(list==null||list.size()<=0) {
			result.error500("未找到版本信息");
		}else {
			result.setResult(list);
			result.setSuccess(true);
		}
		return result;
	}
	
}
