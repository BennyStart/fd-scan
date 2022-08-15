package com.fardo.common.system.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.fardo.common.system.vo.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 底层共通业务API，提供其他独立模块调用
 * @Author: maozf
 * @Date:20-11-25
 * @Version:V1.0
 */
public interface ISysBaseAPI {

	/**
	 * 日志添加
	 * @param LogContent 内容
	 * @param logType 日志类型(0:操作日志;1:登录日志;2:定时任务)
	 * @param operatetype 操作类型(1:添加;2:修改;3:删除;)
	 */
	void addLog(String LogContent, Integer logType, Integer operatetype);
	
	/**
	  * 根据用户账号查询用户信息
	 * @param username
	 * @return
	 */
	public LoginUser getUserByName(String username);
	
	/**
	  * 根据用户id查询用户信息
	 * @param id
	 * @return
	 */
	public LoginUser getUserById(String id);
	
	/**
	 * 通过用户账号查询角色集合
	 * @param username
	 * @return
	 */
	public List<String> getRolesByUsername(String username);

	/**
	 * 通过用户账号查询部门集合
	 * @param username
	 * @return 部门 id
	 */
	List<String> getDepartIdsByUsername(String username);

	/**
	 * 通过用户账号查询部门 name
	 * @param username
	 * @return 部门 name
	 */
	List<String> getDepartNamesByUsername(String username);

	/**
	 * 获取当前数据库类型
	 * @return
	 * @throws Exception 
	 */
	public String getDatabaseType() throws SQLException;
	
	/**
	  * 获取数据字典
	 * @param code
	 * @return
	 */
	public List<DictModel> queryDictItemsByCode(String code);

	/** 查询所有的父级字典，按照create_time排序 */
	public List<DictModel> queryAllDict();

	/**
	  * 获取表数据字典
	 * @param table
	 * @param text
	 * @param code
	 * @return
	 */
    List<DictModel> queryTableDictItemsByCode(String table, String text, String code);
    
    /**
   	 * 查询所有部门 作为字典信息 id -->value,departName -->text
   	 * @return
   	 */
   	public List<DictModel> queryAllDepartBackDictModel();

    /**
   	 * 查询所有部门，拼接查询条件
   	 * @return
   	 */
   	List<JSONObject> queryAllDepart(Wrapper wrapper);


	/**
	 * 查询表字典 支持过滤数据
	 * @param table
	 * @param text
	 * @param code
	 * @param filterSql
	 * @return
	 */
	public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql);

	/**
	 * 查询指定table的 text code 获取字典，包含text和value
	 * @param table
	 * @param text
	 * @param code
	 * @param keyArray
	 * @return
	 */
	@Deprecated
	public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray);

	/**
	 * 获取所有有效用户
	 * @return
	 */
	public List<ComboModel> queryAllUser();

    /**
     * 获取所有有效用户 带参
     * userIds 默认选中用户
     * @return
     */
    public JSONObject queryAllUser(String[] userIds, int pageNo, int pageSize);

    /**
     * 获取所有有效用户 拼接查询条件
     *
     * @return
     */
    List<JSONObject> queryAllUser(Wrapper wrapper);

	/**
	 * 获取所有角色
	 * @return
	 */
	public List<ComboModel> queryAllRole();

	/**
	 * 获取所有角色 带参
     * roleIds 默认选中角色
	 * @return
	 */
	public List<ComboModel> queryAllRole(String[] roleIds );

	/**
	 * 通过用户账号查询角色Id集合
	 * @param username
	 * @return
	 */
	public List<String> getRoleIdsByUsername(String username);

	/**
	 * 通过部门编号查询部门id
	 * @param orgCode
	 * @return
	 */
	public String getDepartIdsByOrgCode(String orgCode);

	/**
	 * 查询上一级部门
	 * @param departId
	 * @return
	 */
	public DictModel getParentDepartId(String departId);

	/**
	 * 查询所有部门
	 * @return
	 */
	public List<SysDepartModel> getAllSysDepart();

	/**
	 * 根据 id 查询数据库中存储的 DynamicDataSourceModel
	 *
	 * @param dbSourceId
	 * @return
	 */
	DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId);

	/**
	 * 根据 code 查询数据库中存储的 DynamicDataSourceModel
	 *
	 * @param dbSourceCode
	 * @return
	 */
	DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode);

	/**
	 * 根据部门Id获取部门负责人
	 * @param deptId
	 * @return
	 */
	public List<String> getDeptHeadByDepId(String deptId);

	/**
	 * 文件上传
	 * @param file 文件
	 * @param bizPath 自定义路径
	 * @param uploadType 上传方式
	 * @return
	 */
	public String upload(MultipartFile file,String bizPath,String uploadType);

	/**
	 * 文件上传 自定义桶
	 * @param file
	 * @param bizPath
	 * @param uploadType
	 * @param customBucket
	 * @return
	 */
	public String upload(MultipartFile file,String bizPath,String uploadType,String customBucket);

	/**
	 * 文档管理文件下载预览
	 * @param filePath
	 * @param uploadpath
	 * @param response
	 */
	public void viewAndDownload(String filePath, String uploadpath, String uploadType,HttpServletResponse response);


	/**
	 * 根据id获取所有参与用户
	 * userIds
	 * @return
	 */
	public List<LoginUser> queryAllUserByIds(String[] userIds);
	/**
	 * 根据name获取所有参与用户
	 * userNames
	 * @return
	 */
	List<LoginUser> queryUserByNames(String[] userNames);
}
