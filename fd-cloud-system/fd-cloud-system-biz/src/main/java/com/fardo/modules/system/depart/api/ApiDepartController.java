package com.fardo.modules.system.depart.api;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.enums.OperTypeEnum;
import com.fardo.common.util.FileUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.depart.model.SysDepartModel;
import com.fardo.modules.system.depart.service.ISysDepartService;
import com.fardo.modules.system.depart.vo.*;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.znbl.picmanage.vo.PicmanageTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @(#)ApiDepartController <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/11 15:16
 * 描　述：
 */
@Slf4j
@Api(tags = "api-机构管理接口")
@RestController
@RequestMapping("/api/system/depart")
public class ApiDepartController {

    @Autowired
    private ISysDepartService sysDepartService;

    @RequestAop(value = "分页获取机构列表", clazz = SysDepartParamVo.class)
    @ApiOperation(value = "分页获取机构列表")
    @PostMapping(value = "/page")
    public ResultVo<IPage<SysDepartModel>> page(ParamVo<SysDepartParamVo> paramVo) {
        IPage<SysDepartModel> page = sysDepartService.getPage(paramVo.getData());
        ResultVo<IPage<SysDepartModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        resultVo.setResults(page);
        return resultVo;
    }

    @RequestAop(value = "根据关键字查询机构列表", clazz = SysDepartKeywParamVo.class)
    @ApiOperation(value = "根据关键字查询机构列表")
    @PostMapping(value = "/listForKeyword")
    public ResultVo<List<SysDepartModel>> listForKeyword(ParamVo<SysDepartKeywParamVo> paramVo) {
        List<SysDepartModel> list = sysDepartService.queryDeptListByKeyword(paramVo.getData().getKeyword(), 100);
        ResultVo<List<SysDepartModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        resultVo.setResults(list);
        return resultVo;
    }

    @RequestAop(value = "根据机构id获取下级机构信息", clazz = SysDepartListParamVo.class)
    @ApiOperation(value = "根据机构id获取下级机构信息")
    @PostMapping(value = "/listSub")
    public ResultVo<List<SysDepartModel>> listSub(ParamVo<SysDepartListParamVo> paramVo) {
        SysDepartListParamVo vo = paramVo.getData();
        List<SysDepartModel> list = sysDepartService.querySubDeptList(vo.getId(),vo.getOriginalAreaCode(),vo.getLevel());
        ResultVo<List<SysDepartModel>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        resultVo.setResults(list);
        return resultVo;
    }

    @RequestAop(value = "新增组织机构", clazz = SysDepartVo.class, operType = OperTypeEnum.XZZZJG)
    @ApiOperation(value = "新增组织机构")
    @PostMapping(value = "/add")
    public ResultVo<SysDepartModel> add(ParamVo<SysDepartVo> paramVo, HttpServletRequest request) {
        ResultVo<SysDepartModel> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysDepartEntity sysDepart = new SysDepartEntity();
        BeanUtils.copyProperties(paramVo.getData(), sysDepart);
        String id = sysDepartService.saveDepartData(sysDepart);
        SysDepartModel model = new SysDepartModel(sysDepart);
        resultVo.setResults(model);
        String operDesc = "（机构名称：%s）";
        request.setAttribute(SysConstants.LOG_OPER_DESC, String.format(operDesc, sysDepart.getDepartName()));
        request.setAttribute(SysConstants.LOG_RESULT_DATA, id);
        return resultVo;
    }

    @RequestAop(value = "修改组织机构", clazz = SysDepartVo.class, operType = OperTypeEnum.XGZZJG)
    @ApiOperation(value = "修改组织机构")
    @PostMapping(value = "/edit")
    public ResultVo<SysDepartModel> edit(ParamVo<SysDepartVo> paramVo, HttpServletRequest request) {
        ResultVo<SysDepartModel> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysDepartEntity sysDepart = new SysDepartEntity();
        BeanUtils.copyProperties(paramVo.getData(), sysDepart);
        sysDepartService.updateDepartDataById(sysDepart);
        SysDepartModel model = new SysDepartModel(sysDepart);
        resultVo.setResults(model);
        String operDesc = "（机构名称：%s）";
        request.setAttribute(SysConstants.LOG_OPER_DESC, String.format(operDesc, sysDepart.getDepartName()));
        request.setAttribute(SysConstants.LOG_RESULT_DATA, sysDepart.getId());
        return resultVo;
    }

    @RequestAop(value = "获取组织机构详情", clazz = SysDepartIdParamVo.class)
    @ApiOperation(value = "获取组织机构详情")
    @PostMapping(value = "/get")
    public ResultVo<SysDepartVo> get(ParamVo<SysDepartIdParamVo> paramVo) {
        ResultVo<SysDepartVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysDepartVo vo = sysDepartService.getDepartDetail(paramVo.getData().getId());
        resultVo.setResults(vo);
        return resultVo;
    }

    @RequestAop(value = "删除组织机构", clazz = SysDepartIdParamVo.class, operType = OperTypeEnum.SCZZJG)
    @ApiOperation(value = "删除组织机构")
    @PostMapping(value = "/delete")
    public ResultVo delete(ParamVo<SysDepartIdParamVo> paramVo, HttpServletRequest request) {
        ResultVo resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        if(StringUtil.isEmpty(paramVo.getData().getId())) {
            return ResultVo.getResultVo(ResultCode.INVALIDPARAMETER);
        }
        List<String> ids = Arrays.asList(StrUtil.split(paramVo.getData().getId(), ","));
        List<String> operDesc = new ArrayList<>();
        for(String id : ids) {
            SysDepartEntity sysDepart = sysDepartService.getById(id);
            if (sysDepart == null) {
                return ResultVo.getResultVo(ResultCode.INVALIDPARAMETER);
            }
            sysDepartService.logicalDelete(id);
            operDesc.add(String.format("（机构名称：%s）", sysDepart.getDepartName()));
        }
        request.setAttribute(SysConstants.LOG_OPER_DESC, StringUtils.join(operDesc, ResultVo.SEPARATOR));
        request.setAttribute(SysConstants.LOG_RESULT_DATA, paramVo.getData().getId());
        return resultVo;
    }

    @RequestAop(value = "导入模板下载")
    @ApiOperation(value = "导入模板下载", notes = "导入模板下载")
    @PostMapping(value = "/downloadTpl")
    public void downloadTpl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("multipart/form-data");
        String userAgent = request.getHeader("User-Agent");
        String formFileName = "导入组织机构模板.xls";
        // 针对IE或者以IE为内核的浏览器：
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            formFileName = java.net.URLEncoder.encode(formFileName, StandardCharsets.UTF_8.name());
        } else {
            // 非IE浏览器的处理：
            formFileName = new String(formFileName.getBytes(StandardCharsets.UTF_8.name()), StandardCharsets.ISO_8859_1.name());
        }
        response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", formFileName));
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setCharacterEncoding("UTF-8");
        InputStream is = this.getClass().getResourceAsStream("/templates/导入组织机构模板.xls");
        IOUtils.write(IOUtils.toByteArray(is), response.getOutputStream());
    }
    @RequestAop(value = "导入组织机构excel",clazz = PicmanageTypeVo.class)
    @ApiOperation(value = "导入组织机构excel", notes = "导入组织机构excel")
    @PostMapping(value = "/importExcel")
    public ResultVo importExcel(HttpServletRequest request, HttpServletResponse response) {
        List<MultipartFile> files = FileUtil.getUploadFiles(request);
        if(CollectionUtils.isEmpty(files)) {
            return ResultVo.getResultVo("101","文件不能为空");
        }
        MultipartFile file = files.get(0);
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        try {
            List<SysDepartExcelVo> list = ExcelImportUtil.importExcel(file.getInputStream(), SysDepartExcelVo.class, params);
            sysDepartService.importExcel(list);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResultVo.getResultVo("102","导入失败："+ e.getMessage());
        } finally {
            try {
                file.getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ResultVo.getResultVo(ResultCode.SUCCESS);
    }

}
