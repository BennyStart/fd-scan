package com.fardo.modules.system.config.api;

import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.config.entity.SysLogoEntity;
import com.fardo.modules.system.config.service.SysLogoService;
import com.fardo.modules.system.sys.vo.ParamVo;
import com.fardo.modules.utils.FileServiceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "api-系统标识配置相关接口")
@RestController
@RequestMapping("/api/system/logo")
public class ApiSysLogoController {

    @Autowired
    private SysLogoService sysLogoService;


    @RequestAop(value = "查询配置")
    @ApiOperation(value = "查询配置", notes = "查询配置")
    @PostMapping(value = "/query")
    public ResultVo<SysLogoEntity> query(ParamVo paramVo) {
        ResultVo<SysLogoEntity> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        List<SysLogoEntity> list = sysLogoService.list();
        SysLogoEntity sysLogoEntity = new SysLogoEntity();
        if (!CollectionUtils.isEmpty(list)) {
            sysLogoEntity = list.get(0);
            if (StringUtils.isNotBlank(sysLogoEntity.getTheBitmap())) {
                sysLogoEntity.setTheBitmapUrl(FileServiceUtils.getFileUrl(sysLogoEntity.getTheBitmap()));
            }
            if (StringUtils.isNotBlank(sysLogoEntity.getBackground())) {
                sysLogoEntity.setBackgroundUrl(FileServiceUtils.getFileUrl(sysLogoEntity.getBackground()));
            }
            if (StringUtils.isNotBlank(sysLogoEntity.getSystemNameLogo())) {
                sysLogoEntity.setSystemNameLogoUrl(FileServiceUtils.getFileUrl(sysLogoEntity.getSystemNameLogo()));
            }
            if (StringUtils.isNotBlank(sysLogoEntity.getLogo())) {
                sysLogoEntity.setLogoUrl(FileServiceUtils.getFileUrl(sysLogoEntity.getLogo()));
            }
        }
        result.setResults(sysLogoEntity);
        return result;
    }

    @RequestAop(value = "保存、修改配置", clazz = SysLogoEntity.class)
    @ApiOperation(value = "保存、修改配置", notes = "保存、修改配置")
    @PostMapping(value = "/save")
    public ResultVo save(ParamVo<SysLogoEntity> paramVo) {
        ResultVo result = new ResultVo<>();
        String id = paramVo.getData().getId();
        SysLogoEntity data = paramVo.getData();
        boolean flag = false;
        if (StringUtils.isBlank(id)) {
            flag = sysLogoService.save(data);
        } else {
            SysLogoEntity byId = sysLogoService.getById(id);
            BeanUtils.copyProperties(data, byId);
            flag = sysLogoService.saveOrUpdate(byId);
        }
        if (flag) {
            result.setResultCode(ResultCode.SUCCESS);
            return result;
        }
        result.setResultCode(ResultCode.ADD_ERROR);
        return result;
    }


}
