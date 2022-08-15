package com.fardo.modules.system.personal.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.personal.dto.BlQualitysetUserDTO;
import com.fardo.modules.system.personal.entity.BlQualitysetUserEntity;
import com.fardo.modules.system.personal.service.BlQualitysetUserService;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/10/19-15:34
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Slf4j
@Api(tags = "api-个人中心文书校验")
@RestController
@RequestMapping("/api/system/blQualitysetUser")
public class ApiBlQualitysetUserController {

    @Autowired
    private BlQualitysetUserService blQualitysetUserService;

    @RequestAop(value = "新增/修改", clazz = BlQualitysetUserDTO.class)
    @ApiOperation("新增/修改")
    @PostMapping(value = "/save")
    public ResultVo save(ParamVo<BlQualitysetUserDTO> paramVo) {
        ResultVo resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        //获取登陆人信息
        String id = paramVo.currentLoginUser().getId();
        //获取传入对象
        BlQualitysetUserDTO data = paramVo.getData();
        boolean flag = blQualitysetUserService.saveOrUpdate(data, id);
        if (flag) {
            resultVo.setResultCode(ResultCode.SUCCESS);
            resultVo.setResultMsg("操作成功!");
            return resultVo;
        }
        resultVo.setResultCode(ResultCode.ADD_ERROR);
        resultVo.setResultMsg("操作失败!");
        return resultVo;
    }

    @RequestAop(value = "获取详情")
    @ApiOperation("获取详情")
    @PostMapping(value = "/info")
    public ResultVo<BlQualitysetUserEntity> info(ParamVo paramVo) {
        ResultVo<BlQualitysetUserEntity> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        //获取登陆人信息
        String id = paramVo.currentLoginUser().getId();
        QueryWrapper<BlQualitysetUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USER_ID", id);
        BlQualitysetUserEntity one = blQualitysetUserService.getOne(queryWrapper);
        resultVo.setResults(one);
        resultVo.setResultCode(ResultCode.SUCCESS);
        resultVo.setResultMsg("操作成功!");
        return resultVo;
    }
}
