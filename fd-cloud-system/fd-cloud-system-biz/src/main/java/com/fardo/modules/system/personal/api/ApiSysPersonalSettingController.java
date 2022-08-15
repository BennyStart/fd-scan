package com.fardo.modules.system.personal.api;

import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.personal.dto.SysPersonalDTO;
import com.fardo.modules.system.personal.dto.SysPersonalSettingDTO;
import com.fardo.modules.system.personal.service.ISysPersonalSettingService;
import com.fardo.modules.system.personal.vo.SysPersonalSettingVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人中心常用地址设置
 *
 * @author guohh
 * 2021-08-31
 */
@Slf4j
@Api(tags = "api-个人中心常用地址设置")
@RestController
@RequestMapping("/api/system/personal")
public class ApiSysPersonalSettingController {

    @Autowired
    private ISysPersonalSettingService sysPersonalSettingService;


    @RequestAop(value = "新增/修改个人中心常用地址", clazz = SysPersonalSettingDTO.class)
    @ApiOperation("新增/修改个人中心常用地址")
    @PostMapping(value = "/save")
    public ResultVo save(ParamVo<SysPersonalSettingDTO> paramVo) {
        ResultVo resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        //获取登陆人信息
        String id = paramVo.currentLoginUser().getId();
        //获取传入对象
        SysPersonalSettingDTO data = paramVo.getData();
        boolean flag = sysPersonalSettingService.saveOrUpdate(data,id);
        if (flag){
            resultVo.setResultCode(ResultCode.SUCCESS);
            resultVo.setResultMsg("操作成功!");
            return resultVo;
        }
        resultVo.setResultCode(ResultCode.ADD_ERROR);
        resultVo.setResultMsg("操作失败!");
        return resultVo;
    }


    @RequestAop(value = "获取办案地点设置", clazz = SysPersonalDTO.class)
    @ApiOperation("获取办案地点设置")
    @PostMapping(value = "/list")
    public ResultVo<SysPersonalSettingVo> listAll(ParamVo<SysPersonalDTO> paramVo) {
        ResultVo<SysPersonalSettingVo> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        SysPersonalDTO data = paramVo.getData();
        SysPersonalSettingVo list = sysPersonalSettingService.getList(data);
        resultVo.setResults(list);
        resultVo.setResultMsg("操作成功!");
        return resultVo;
    }

}
