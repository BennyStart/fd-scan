package com.fardo.modules.system.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.common.constant.CacheConstant;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.constant.DataBaseConstant;
import com.fardo.common.exception.FdException;
import com.fardo.common.system.api.ISysBaseAPI;
import com.fardo.common.system.vo.*;
import com.fardo.common.util.IPUtils;
import com.fardo.common.util.MinioUtil;
import com.fardo.common.util.SpringContextUtils;
import com.fardo.common.util.oConvertUtils;
import com.fardo.common.util.oss.OssBootUtil;
import com.fardo.modules.system.config.entity.SysDictEntity;
import com.fardo.modules.system.config.service.ISysDictService;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.mapper.SysDepartMapper;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.data.entity.SysDataSourceEntity;
import com.fardo.modules.system.log.entity.SysLogEntity;
import com.fardo.modules.system.log.mapper.SysLogMapper;
import com.fardo.modules.system.role.entity.SysRoleEntity;
import com.fardo.modules.system.role.mapper.SysRoleMapper;
import com.fardo.modules.system.data.service.ISysDataSourceService;
import com.fardo.modules.system.user.entity.SysUserEntity;
import com.fardo.modules.system.user.mapper.SysUserMapper;
import com.fardo.modules.system.user.mapper.SysUserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 底层共通业务API，提供其他独立模块调用
 * @Author: scott
 * @Date:2019-4-20
 * @Version:V1.0
 */
@Slf4j
@Service
public class SysBaseApiImpl implements ISysBaseAPI {
	/** 当前系统数据库类型 */
	private static String DB_TYPE = "";
	@Resource
	private SysLogMapper sysLogMapper;
	@Resource
	private SysUserMapper userMapper;
	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	@Autowired
	private ISysDepartService sysDepartService;
	@Autowired
	private ISysDictService sysDictService;
	@Resource
	private SysRoleMapper roleMapper;
	@Resource
	private SysDepartMapper departMapper;

	@Autowired
	private ISysDataSourceService dataSourceService;

	@Override
	public void addLog(String LogContent, Integer logType, Integer operatetype) {
		SysLogEntity sysLog = new SysLogEntity();
		//注解上的描述,操作日志内容
		sysLog.setLogContent(LogContent);
		sysLog.setLogType(logType);
		sysLog.setOperateType(operatetype);

		//请求的方法名
		//请求的参数

		try {
			//获取request
			HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
			//设置IP地址
			sysLog.setIp(IPUtils.getIpAddr(request));
		} catch (Exception e) {
			sysLog.setIp("127.0.0.1");
		}

		//获取登录用户信息
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		if(sysUser!=null){
			sysLog.setUserid(sysUser.getUsername());
			sysLog.setUsername(sysUser.getRealname());

		}
		//保存系统日志
		sysLogMapper.insert(sysLog);
	}

	@Override
	@Cacheable(cacheNames=CacheConstant.SYS_USERS_CACHE, key="#username")
	public LoginUser getUserByName(String username) {
		if(oConvertUtils.isEmpty(username)) {
			return null;
		}
		LoginUser loginUser = new LoginUser();
		SysUserEntity sysUser = userMapper.getUserByName(username);
		if(sysUser==null) {
			return null;
		}
		BeanUtils.copyProperties(sysUser, loginUser);
		return loginUser;
	}

	@Override
	public LoginUser getUserById(String id) {
		if(oConvertUtils.isEmpty(id)) {
			return null;
		}
		LoginUser loginUser = new LoginUser();
		SysUserEntity sysUser = userMapper.selectById(id);
		if(sysUser==null) {
			return null;
		}
		BeanUtils.copyProperties(sysUser, loginUser);
		return loginUser;
	}

	@Override
	public List<String> getRolesByUsername(String username) {
		return sysUserRoleMapper.getRoleByUserName(username);
	}

	@Override
	public List<String> getDepartIdsByUsername(String username) {
		List<SysDepartEntity> list = sysDepartService.queryDepartsByUsername(username);
		List<String> result = new ArrayList<>(list.size());
		for (SysDepartEntity depart : list) {
			result.add(depart.getId());
		}
		return result;
	}

	@Override
	public List<String> getDepartNamesByUsername(String username) {
		List<SysDepartEntity> list = sysDepartService.queryDepartsByUsername(username);
		List<String> result = new ArrayList<>(list.size());
		for (SysDepartEntity depart : list) {
			result.add(depart.getDepartName());
		}
		return result;
	}

	@Override
	public String getDatabaseType() throws SQLException {
		if(oConvertUtils.isNotEmpty(DB_TYPE)){
			return DB_TYPE;
		}
		DataSource dataSource = SpringContextUtils.getApplicationContext().getBean(DataSource.class);
		return getDatabaseTypeByDataSource(dataSource);
	}

	@Override
	@Cacheable(value = CacheConstant.SYS_DICT_CACHE,key = "#code")
	public List<DictModel> queryDictItemsByCode(String code) {
		return sysDictService.queryDictItemsByCode(code);
	}

	@Override
	public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
		return sysDictService.queryTableDictItemsByCode(table, text, code);
	}

	@Override
	public List<DictModel> queryAllDepartBackDictModel() {
		return sysDictService.queryAllDepartBackDictModel();
	}

    @Override
    public List<JSONObject> queryAllDepart(Wrapper wrapper) {
        //noinspection unchecked
        return JSON.parseArray(JSON.toJSONString(sysDepartService.list(wrapper))).toJavaList(JSONObject.class);
    }


	/**
	 * 获取数据库类型
	 * @param dataSource
	 * @return
	 * @throws SQLException
	 */
	private String getDatabaseTypeByDataSource(DataSource dataSource) throws SQLException{
		if("".equals(DB_TYPE)) {
			Connection connection = dataSource.getConnection();
			try {
				DatabaseMetaData md = connection.getMetaData();
				String dbType = md.getDatabaseProductName().toLowerCase();
				if(dbType.indexOf("mysql")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_MYSQL;
				}else if(dbType.indexOf("oracle")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_ORACLE;
				}else if(dbType.indexOf("sqlserver")>=0||dbType.indexOf("sql server")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_SQLSERVER;
				}else if(dbType.indexOf("postgresql")>=0) {
					DB_TYPE = DataBaseConstant.DB_TYPE_POSTGRESQL;
				}else {
					throw new FdException("数据库类型:["+dbType+"]不识别!");
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}finally {
				connection.close();
			}
		}
		return DB_TYPE;

	}

	@Override
	public List<DictModel> queryAllDict() {
		// 查询并排序
		QueryWrapper<SysDictEntity> queryWrapper = new QueryWrapper<SysDictEntity>();
		queryWrapper.orderByAsc("create_time");
		List<SysDictEntity> dicts = sysDictService.list(queryWrapper);
		// 封装成 model
		List<DictModel> list = new ArrayList<DictModel>();
		for (SysDictEntity dict : dicts) {
			list.add(new DictModel(dict.getDictCode(), dict.getDictName()));
		}

		return list;
	}


	@Override
	public List<DictModel> queryFilterTableDictInfo(String table, String text, String code, String filterSql) {
		return sysDictService.queryTableDictItemsByCodeAndFilter(table,text,code,filterSql);
	}

	@Override
	public List<String> queryTableDictByKeys(String table, String text, String code, String[] keyArray) {
		return sysDictService.queryTableDictByKeys(table,text,code,keyArray);
	}

	@Override
	public List<ComboModel> queryAllUser() {
		List<ComboModel> list = new ArrayList<ComboModel>();
		List<SysUserEntity> userList = userMapper.selectList(new QueryWrapper<SysUserEntity>().eq("status",1).eq("del_flag",0));
		for(SysUserEntity user : userList){
			ComboModel model = new ComboModel();
			model.setTitle(user.getRealname());
			model.setId(user.getId());
			model.setUsername(user.getUsername());
			list.add(model);
		}
		return list;
	}

    @Override
    public JSONObject queryAllUser(String[] userIds,int pageNo,int pageSize) {
		JSONObject json = new JSONObject();
		QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<SysUserEntity>().eq("status",1).eq("del_flag",0);
        List<ComboModel> list = new ArrayList<ComboModel>();
		Page<SysUserEntity> page = new Page<SysUserEntity>(pageNo, pageSize);
		IPage<SysUserEntity> pageList = userMapper.selectPage(page, queryWrapper);
        for(SysUserEntity user : pageList.getRecords()){
            ComboModel model = new ComboModel();
            model.setUsername(user.getUsername());
            model.setTitle(user.getRealname());
            model.setId(user.getId());
            model.setEmail(user.getEmail());
            if(oConvertUtils.isNotEmpty(userIds)){
                for(int i = 0; i<userIds.length;i++){
                    if(userIds[i].equals(user.getId())){
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
		json.put("list",list);
        json.put("total",pageList.getTotal());
        return json;
    }

    @Override
    public List<JSONObject> queryAllUser(Wrapper wrapper) {
        //noinspection unchecked
        return JSON.parseArray(JSON.toJSONString(userMapper.selectList(wrapper))).toJavaList(JSONObject.class);
    }

    @Override
	public List<ComboModel> queryAllRole() {
		List<ComboModel> list = new ArrayList<ComboModel>();
		List<SysRoleEntity> roleList = roleMapper.selectList(new QueryWrapper<SysRoleEntity>());
		for(SysRoleEntity role : roleList){
			ComboModel model = new ComboModel();
			model.setTitle(role.getRoleName());
			model.setId(role.getId());
			list.add(model);
		}
		return list;
	}

    @Override
    public List<ComboModel> queryAllRole(String[] roleIds) {
        List<ComboModel> list = new ArrayList<ComboModel>();
        List<SysRoleEntity> roleList = roleMapper.selectList(new QueryWrapper<SysRoleEntity>());
        for(SysRoleEntity role : roleList){
            ComboModel model = new ComboModel();
            model.setTitle(role.getRoleName());
            model.setId(role.getId());
            model.setRoleCode(role.getRoleCode());
            if(oConvertUtils.isNotEmpty(roleIds)) {
                for (int i = 0; i < roleIds.length; i++) {
                    if (roleIds[i].equals(role.getId())) {
                        model.setChecked(true);
                    }
                }
            }
            list.add(model);
        }
        return list;
    }

	@Override
	public List<String> getRoleIdsByUsername(String username) {
		return sysUserRoleMapper.getRoleIdByUserName(username);
	}

	@Override
	public String getDepartIdsByOrgCode(String orgCode) {
		return departMapper.queryDepartIdByOrgCode(orgCode);
	}

	@Override
	public DictModel getParentDepartId(String departId) {
		SysDepartEntity depart = departMapper.getParentDepartId(departId);
		DictModel model = new DictModel(depart.getId(),depart.getParentId());
		return model;
	}

	@Override
	public List<SysDepartModel> getAllSysDepart() {
		List<SysDepartModel> departModelList = new ArrayList<SysDepartModel>();
		List<SysDepartEntity> departList = departMapper.selectList(new QueryWrapper<SysDepartEntity>().eq("del_flag","0"));
		for(SysDepartEntity depart : departList){
			SysDepartModel model = new SysDepartModel();
			BeanUtils.copyProperties(depart,model);
			departModelList.add(model);
		}
		return departModelList;
	}

	@Override
	public DynamicDataSourceModel getDynamicDbSourceById(String dbSourceId) {
		SysDataSourceEntity dbSource = dataSourceService.getById(dbSourceId);
		return new DynamicDataSourceModel(dbSource);
	}

	@Override
	public DynamicDataSourceModel getDynamicDbSourceByCode(String dbSourceCode) {
		SysDataSourceEntity dbSource = dataSourceService.getOne(new LambdaQueryWrapper<SysDataSourceEntity>().eq(SysDataSourceEntity::getCode, dbSourceCode));
		return new DynamicDataSourceModel(dbSource);
	}

	@Override
	public List<String> getDeptHeadByDepId(String deptId) {
		List<SysUserEntity> userList = userMapper.selectList(new QueryWrapper<SysUserEntity>().like("depart_ids",deptId).eq("status",1).eq("del_flag",0));
		List<String> list = new ArrayList<>();
		for(SysUserEntity user : userList){
			list.add(user.getUsername());
		}
		return list;
	}

	@Override
	public String upload(MultipartFile file,String bizPath,String uploadType) {
		String url = "";
		if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
			url = MinioUtil.upload(file,bizPath);
		}else{
			url = OssBootUtil.upload(file,bizPath);
		}
		return url;
	}

	@Override
	public String upload(MultipartFile file, String bizPath, String uploadType, String customBucket) {
		String url = "";
		if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
			url = MinioUtil.upload(file,bizPath,customBucket);
		}else{
			url = OssBootUtil.upload(file,bizPath,customBucket);
		}
		return url;
	}

	@Override
	public void viewAndDownload(String filePath, String uploadpath, String uploadType, HttpServletResponse response) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			if(filePath.startsWith("http")){
				String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
				if(CommonConstant.UPLOAD_TYPE_MINIO.equals(uploadType)){
					String bucketName = filePath.replace(MinioUtil.getMinioUrl(),"").split("/")[0];
					String objectName = filePath.replace(MinioUtil.getMinioUrl()+bucketName,"");
					inputStream = MinioUtil.getMinioFile(bucketName,objectName);
					if(inputStream == null){
						bucketName = CommonConstant.UPLOAD_CUSTOM_BUCKET;
						objectName = filePath.replace(OssBootUtil.getStaticDomain()+"/","");
						inputStream = OssBootUtil.getOssFile(objectName,bucketName);
					}
				}else{
					String bucketName = CommonConstant.UPLOAD_CUSTOM_BUCKET;
					String objectName = filePath.replace(OssBootUtil.getStaticDomain()+"/","");
					inputStream = OssBootUtil.getOssFile(objectName,bucketName);
					if(inputStream == null){
						bucketName = filePath.replace(MinioUtil.getMinioUrl(),"").split("/")[0];
						objectName = filePath.replace(MinioUtil.getMinioUrl()+bucketName,"");
						inputStream = MinioUtil.getMinioFile(bucketName,objectName);
					}
				}
				response.addHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes("UTF-8"),"iso-8859-1"));
			}else{
				// 本地文件处理
				filePath = filePath.replace("..", "");
				if (filePath.endsWith(",")) {
					filePath = filePath.substring(0, filePath.length() - 1);
				}
				String fullPath = uploadpath + File.separator + filePath;
				String downloadFilePath = uploadpath + File.separator + fullPath;
				File file = new File(downloadFilePath);
				inputStream = new BufferedInputStream(new FileInputStream(fullPath));
				response.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes("UTF-8"),"iso-8859-1"));
			}
			response.setContentType("application/force-download");// 设置强制下载不打开
			outputStream = response.getOutputStream();
			if(inputStream != null){
				byte[] buf = new byte[1024];
				int len;
				while ((len = inputStream.read(buf)) > 0) {
					outputStream.write(buf, 0, len);
				}
				response.flushBuffer();
			}
		} catch (IOException e) {
			response.setStatus(404);
			log.error("预览文件失败" + e.getMessage());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public List<LoginUser> queryAllUserByIds(String[] userIds) {
		QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<SysUserEntity>().eq("status",1).eq("del_flag",0);
		queryWrapper.in("id",userIds);
		List<LoginUser> loginUsers = new ArrayList<>();
		List<SysUserEntity> sysUsers = userMapper.selectList(queryWrapper);
		for (SysUserEntity user:sysUsers) {
			LoginUser loginUser=new LoginUser();
			BeanUtils.copyProperties(user, loginUser);
			loginUsers.add(loginUser);
		}
		return loginUsers;
	}


	@Override
	public List<LoginUser> queryUserByNames(String[] userNames) {
		QueryWrapper<SysUserEntity> queryWrapper = new QueryWrapper<SysUserEntity>().eq("status",1).eq("del_flag",0);
		queryWrapper.in("username",userNames);
		List<LoginUser> loginUsers = new ArrayList<>();
		List<SysUserEntity> sysUsers = userMapper.selectList(queryWrapper);
		for (SysUserEntity user:sysUsers) {
			LoginUser loginUser=new LoginUser();
			BeanUtils.copyProperties(user, loginUser);
			loginUsers.add(loginUser);
		}
		return loginUsers;
	}
}
