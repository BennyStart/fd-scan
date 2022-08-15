package com.fardo.modules.system.config.controller;

import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.config.service.BlDmBllxConfigService;
import com.fardo.modules.system.config.service.ISysClientConfigService;
import com.fardo.modules.system.constant.ClientConfigIdConstants;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.znbl.bl.vo.BlBllxConfigObjectVo;
import com.fardo.modules.znbl.bl.vo.BlBllxConfigVo;
import com.fardo.modules.znbl.ywxt.model.BlBldlxModel;
import com.fardo.modules.znbl.ywxt.model.BlBllxModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/7/14-15:22
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Slf4j
@RestController
@RequestMapping("/api/system/bllxConfig")
@Api(tags = "api-笔录类型管理配置")
public class BlDmBllxConfigController {

    @Autowired
    private BlDmBllxConfigService blDmBllxConfigService;
    @Autowired
    private ISysClientConfigService sysClientConfigService;

    @RequestAop(value = "保存笔录类型管理配置", clazz = BlBllxConfigVo.class)
    @ApiOperation(value = "保存笔录类型管理配置", notes = "保存笔录类型管理配置")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResultVo<String> queryPageList(ParamVo<BlBllxConfigVo> paramVo) {
        ResultVo<String> result = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<BlBllxConfigObjectVo> list = paramVo.getData().getList();
        if (CollectionUtils.isEmpty(list)) {
            result.setResultMsg("数据为空！");
            return result;
        }
        //处理数据
        Integer save = blDmBllxConfigService.saveOrUpdate(list);
        //判断是否成功
        if (save > 0) {
            result.setResultMsg("操作成功");
            //刷新笔录类型下发配置
            sysClientConfigService.refreshClientConfig(ClientConfigIdConstants.DM_BLDLX);
            sysClientConfigService.refreshClientConfig(ClientConfigIdConstants.DM_BLLX);
            sysClientConfigService.refreshClientConfig(ClientConfigIdConstants.DM_LX_XWDXLX);
            return result;
        }
        result.setResultMsg("操作失败！");
        return result;
    }


    // @RequestAop(value = "获取打印树列表")
    // @ApiOperation(value = "获取打印树列表", notes = "获取打印树列表")
    // @PostMapping(value = "/list")
    // public ResultVo<List<BlBldlxModel>> getList(ParamVo paramVo) {
    //     ResultVo<List<BlBldlxModel>> result = new ResultVo<>();
    //     result.setResultCode(ResultCode.SUCCESS);
    //     //获取大类型
    //     List<BlBldlxModel> bldlxTree = blDmBllxConfigService.getBldlxTree();
    //     //判断类型是否为空
    //     if (!CollectionUtils.isEmpty(bldlxTree)) {
    //         for (BlBldlxModel blBldlxModel : bldlxTree) {
    //             List<BlBllxModel> blBllxEntity = blDmBllxConfigService.getBlBllxEntity(blBldlxModel.getId());
    //             blBldlxModel.setChildren(blBllxEntity);
    //         }
    //     }
    //     result.setResults(bldlxTree);
    //     return result;
    // }

    @RequestAop(value = "获取打印树列表")
    @ApiOperation(value = "获取打印树列表", notes = "获取打印树列表")
    @PostMapping(value = "/list")
    public ResultVo<List<BlBldlxModel>> getList(ParamVo paramVo) {
        ResultVo<List<BlBldlxModel>> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        //获取大类型
        List<BlBldlxModel> bldlxTree = blDmBllxConfigService.getBldlxTree();
        //判断类型是否为空
        if (!CollectionUtils.isEmpty(bldlxTree)) {
            for (BlBldlxModel blBldlxModel : bldlxTree) {
                List<BlBllxModel> blBllxEntity = blDmBllxConfigService.getBlBllxBackUpEntity(blBldlxModel.getId());
                blBldlxModel.setChildren(blBllxEntity);
            }
        }
        result.setResults(bldlxTree);
        return result;
    }


}
