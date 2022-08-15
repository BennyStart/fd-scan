package com.fardo.modules.system.depart.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.CacheConstant;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.system.query.QueryGenerator;
import com.fardo.common.system.vo.LoginUser;
import com.fardo.common.util.ImportExcelUtil;
import com.fardo.common.util.oConvertUtils;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.model.DepartIdModel;
import com.fardo.modules.system.depart.model.SysDepartModel;
import com.fardo.modules.system.depart.model.SysDepartTreeModel;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.depart.vo.SysDepartVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Author: maozf
 * @Since： 20-11-21
 */
@RestController
@Api(tags = "组织机构")
@RequestMapping("/sys/sysDepart")
@Slf4j
public class SysDepartController {

    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    @ApiOperation(value = "根据机构id获取机构树", notes = "根据机构id获取机构树")
    @RequestMapping(value = "/queryTreeById", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> queryTreeById(@ApiParam(name = "id", value = "部门id") @RequestParam("id") String id) {
        Result<List<SysDepartTreeModel>> result = new Result<>();
        List<SysDepartTreeModel> list = sysDepartService.queryTreeList(id);
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    @ApiOperation(value = "根据机构id获取下级机构信息", notes = "根据机构id获取下级机构信息")
    @RequestMapping(value = "/querySubDeptList", method = RequestMethod.GET)
    public Result<List<SysDepartModel>> querySubDeptList(@ApiParam(name = "id", value = "部门id") @RequestParam("id") String id) {
        Result<List<SysDepartModel>> result = new Result<>();
        List<SysDepartModel> list = sysDepartService.querySubDeptList(id);
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    //@RequiresRoles({"admin"})
    @ApiOperation(value = "新增组织机构", notes = "新增组织机构")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result add(@RequestBody SysDepartVo sysDepartVo, HttpServletRequest request) {
        //前端传0，则是空
        if ("0".equals(sysDepartVo.getParentId())) {
            sysDepartVo.setParentId(null);
        }
        SysDepartEntity sysDepart = new SysDepartEntity();
        BeanUtils.copyProperties(sysDepartVo, sysDepart);
        sysDepartService.saveDepartData(sysDepart);
        return Result.ok();
    }

    /**
     * 编辑数据 编辑部门的部分数据,并保存到数据库
     *
     * @param sysDepartModel
     * @return
     */
    //@RequiresRoles({"admin"})
    @ApiOperation(value = "修改组织机构", notes = "修改组织机构")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result edit(@RequestBody SysDepartVo sysDepartModel, HttpServletRequest request) {
        Result result = Result.ok();
        SysDepartEntity sysDepartEntity = sysDepartService.getById(sysDepartModel.getId());
        if (sysDepartEntity == null) {
            result.error500("未找到对应实体");
        } else {
            //前端传0，则是空
            if ("0".equals(sysDepartModel.getParentId())) {
                sysDepartModel.setParentId(null);
            }
            SysDepartEntity sysDepart = new SysDepartEntity();
            BeanUtils.copyProperties(sysDepartModel, sysDepart);
            sysDepartService.updateDepartDataById(sysDepart);
        }
        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    //@RequiresRoles({"admin"})
    @ApiOperation(value = "删除组织机构", notes = "删除组织机构")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result delete(@ApiParam(value = "部门id", name = "id") @RequestParam(name = "id", required = true) String id) {
        Result result = Result.ok();
        SysDepartEntity sysDepart = sysDepartService.getById(id);
        if (sysDepart == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysDepartService.logicalDelete(id);
            if (!ok) {
                result.error500("删除失败！");
            }
        }
        return result;
    }


    /**
     * 查询数据 添加或编辑页面对该方法发起请求,以树结构形式加载所有部门的名称,方便用户的操作
     *
     * @return
     */
    @RequestMapping(value = "/queryIdTree", method = RequestMethod.GET)
    public Result<List<DepartIdModel>> queryIdTree() {
//		Result<List<DepartIdModel>> result = new Result<List<DepartIdModel>>();
//		List<DepartIdModel> idList;
//		try {
//			idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//			if (idList != null && idList.size() > 0) {
//				result.setResult(idList);
//				result.setSuccess(true);
//			} else {
//				sysDepartService.queryTreeList();
//				idList = FindsDepartsChildrenUtil.wrapDepartIdModel();
//				result.setResult(idList);
//				result.setSuccess(true);
//			}
//			return result;
//		} catch (Exception e) {
//			log.error(e.getMessage(),e);
//			result.setSuccess(false);
//			return result;
//		}
        Result<List<DepartIdModel>> result = new Result<>();
        try {
            List<DepartIdModel> list = sysDepartService.queryDepartIdTreeList();
            result.setResult(list);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * <p>
     * 部门搜索功能方法,根据关键字模糊搜索相关部门
     * </p>
     *
     * @param keyWord
     * @return
     */
    @RequestMapping(value = "/searchBy", method = RequestMethod.GET)
    public Result<List<SysDepartTreeModel>> searchBy(@RequestParam(name = "keyWord", required = true) String keyWord, @RequestParam(name = "myDeptSearch", required = false) String myDeptSearch) {
        Result<List<SysDepartTreeModel>> result = new Result<List<SysDepartTreeModel>>();
        //部门查询，myDeptSearch为1时为我的部门查询，登录用户为上级时查只查负责部门下数据
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String departIds = null;
        if (oConvertUtils.isNotEmpty(user.getUserIdentity()) && user.getUserIdentity().equals(CommonConstant.USER_IDENTITY_2)) {
            departIds = user.getDepartIds();
        }
        List<SysDepartTreeModel> treeList = this.sysDepartService.searhBy(keyWord, myDeptSearch, departIds);
        if (treeList == null || treeList.size() == 0) {
            result.setSuccess(false);
            result.setMessage("未查询匹配数据！");
            return result;
        }
        result.setResult(treeList);
        return result;
    }


    /**
     * 导出excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysDepartEntity sysDepart, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysDepartEntity> queryWrapper = QueryGenerator.initQueryWrapper(sysDepart, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysDepartEntity> pageList = sysDepartService.list(queryWrapper);
        //按字典排序
        Collections.sort(pageList, new Comparator<SysDepartEntity>() {
            @Override
            public int compare(SysDepartEntity arg0, SysDepartEntity arg1) {
                return arg0.getOrgCode().compareTo(arg1.getOrgCode());
            }
        });
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "部门列表");
        mv.addObject(NormalExcelConstants.CLASS, SysDepartEntity.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("部门列表数据", "导出人:" + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    //@RequiresRoles({"admin"})
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        List<String> errorMessageList = new ArrayList<>();
        List<SysDepartEntity> listSysDeparts = null;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                // orgCode编码长度
                int codeLength = 3;
                listSysDeparts = ExcelImportUtil.importExcel(file.getInputStream(), SysDepartEntity.class, params);
                //按长度排序
                Collections.sort(listSysDeparts, new Comparator<SysDepartEntity>() {
                    @Override
                    public int compare(SysDepartEntity arg0, SysDepartEntity arg1) {
                        return arg0.getOrgCode().length() - arg1.getOrgCode().length();
                    }
                });

                int num = 0;
                for (SysDepartEntity sysDepart : listSysDeparts) {
                    String orgCode = sysDepart.getOrgCode();
                    if (orgCode.length() > codeLength) {
                        String parentCode = orgCode.substring(0, orgCode.length() - codeLength);
                        QueryWrapper<SysDepartEntity> queryWrapper = new QueryWrapper<SysDepartEntity>();
                        queryWrapper.eq("org_code", parentCode);
                        try {
                            SysDepartEntity parentDept = sysDepartService.getOne(queryWrapper);
                            if (!parentDept.equals(null)) {
                                sysDepart.setParentId(parentDept.getId());
                            } else {
                                sysDepart.setParentId("");
                            }
                        } catch (Exception e) {
                            //没有查找到parentDept
                        }
                    } else {
                        sysDepart.setParentId("");
                    }
                    sysDepart.setDelFlag(CommonConstant.DEL_FLAG_0);
                    ImportExcelUtil.importDateSaveOne(sysDepart, ISysDepartService.class, errorMessageList, num, CommonConstant.SQL_INDEX_UNIQ_DEPART_ORG_CODE);
                    num++;
                }
                //清空部门缓存
                Set keys3 = redisTemplate.keys(CacheConstant.SYS_DEPARTS_CACHE + "*");
                Set keys4 = redisTemplate.keys(CacheConstant.SYS_DEPART_IDS_CACHE + "*");
                redisTemplate.delete(keys3);
                redisTemplate.delete(keys4);
                return ImportExcelUtil.imporReturnRes(errorMessageList.size(), listSysDeparts.size() - errorMessageList.size(), errorMessageList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }


    /**
     * 查询所有部门信息
     *
     * @return
     */
    @GetMapping("listAll")
    public Result<List<SysDepartEntity>> listAll(@RequestParam(name = "id", required = false) String id) {
        Result<List<SysDepartEntity>> result = new Result<>();
        LambdaQueryWrapper<SysDepartEntity> query = new LambdaQueryWrapper<SysDepartEntity>();
        query.orderByAsc(SysDepartEntity::getOrgCode);
        if (oConvertUtils.isNotEmpty(id)) {
            String arr[] = id.split(",");
            query.in(SysDepartEntity::getId, arr);
        }
        List<SysDepartEntity> ls = this.sysDepartService.list(query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    /**
     * 根据部门id获取部门信息
     *
     * @param departId
     * @return
     */
    @RequestMapping(value = "/findDepart/{departId}", method = RequestMethod.GET)
    @CacheEvict(value = {CacheConstant.SYS_DEPARTS_CACHE, CacheConstant.SYS_DEPART_IDS_CACHE}, allEntries = true)
    public SysDepartEntity findDepart(@PathVariable(name = "departId") String departId) {
        SysDepartEntity sysDepart = this.sysDepartService.getById(departId);
        return sysDepart;
    }


    /**
     * 根据部门org获取部门信息
     *
     * @param departId
     * @return
     */
    @RequestMapping(value = "/findDepartByOrgCode/{orgCode}", method = RequestMethod.GET)
    public SysDepartEntity findDepartByOrgCode(@PathVariable(name = "orgCode") String orgCode) {
        SysDepartEntity sysDepart = this.sysDepartService.findDepartByOrgCode(orgCode);
        System.out.println("返回部门:" + sysDepart.toString());
        return sysDepart;
    }

    /**
     * 功能描述：获取当前部门顶级部门<br>
     *
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2022/1/7 17:41
     **/
    @GetMapping("/getTopDepart/{deptId}")
    public SysDepartEntity getTopDepartId(@PathVariable(name = "deptId") String deptId) {
        return sysDepartService.getTopDepart(deptId);
    }

}
