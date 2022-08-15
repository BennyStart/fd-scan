package com.fardo.modules.system.config.api;

import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.constant.CacheConstant;
import com.fardo.common.system.vo.DictModel;
import com.fardo.modules.system.config.entity.SysClientConfigEntity;
import com.fardo.modules.system.config.service.ISysClientConfigService;
import com.fardo.modules.system.config.service.ISysDictService;
import com.fardo.modules.system.config.service.ISysSoftwareUpgradeStrategyService;
import com.fardo.modules.system.config.vo.*;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Api(tags = "api-系统接口")
@RestController
@RequestMapping("/api/system")
public class ConfigController {

    @Autowired
    private ISysClientConfigService sysClientConfigService;
    @Autowired
    private ISysSoftwareUpgradeStrategyService softwareUpgradeStrategyService;
    @Autowired
    private ISysDictService sysDictService;
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;


    @RequestAop(value = "配置数据加载", clazz = SynConfigListVo.class)
    @ApiOperation(value = "配置数据加载", notes = "获取配置信息，请求时上传本地所需的配置项id及对应的数据签名到云端，由云端判断配置数据是否需要更新。有则返回，无则不返回，即表示客户端当前的配置数据是最新的")
    @PostMapping(value = "/synConf")
    public ResultVo<List<SynConfResult>> synConf(ParamVo<SynConfigListVo> paramVo) {
        ResultVo<List<SynConfResult>> result = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SynConfigVo> voList = paramVo.getData().getConfigs();
        List<SynConfResult> responses = new ArrayList<>();
        for(SynConfigVo vo : voList) {
            String configId = vo.getConfigId();
            String configMd5 = vo.getConfigMd5();
            SysClientConfigEntity cc = sysClientConfigService.getConfigId(configId);
            if(cc == null) {
                continue;
            }
            boolean isNewest = cc.getConfigMd5().equals(configMd5);
            if(!isNewest) {
                //不是最新配置，返回最新配置
                String content = sysClientConfigService.getConfigContentByConfigId(configId);
                SynConfResult response = new SynConfResult();
                response.setConfigId(configId);
                response.setConfigMd5(cc.getConfigMd5());
                response.setContent(content);
                response.setConfigVersion(cc.getConfigVersion());
                responses.add(response);
            }
        }
        result.setResults(responses);
        return result;
    }



    @RequestAop(value = "客户端升级查询接口", clazz = ClientUpgradeVo.class)
    @ApiOperation(value = "客户端升级查询接口", notes = "客户端升级查询接口")
    @PostMapping(value = "/upgradeQuery")
    public ResultVo<UpgradeQueryResult> upgradeQuery(ParamVo<ClientUpgradeVo> paramVo) {
        try {
            return  softwareUpgradeStrategyService.clientUpgradeQuery(paramVo);
        } catch (Exception e) {
            return ResultVo.getResultVo(ResultCode.ERROR.getResultCode(), e.getMessage());
        }
    }


    @RequestAop(value = "获取数据字典", clazz = SynDictVo.class)
    @ApiOperation(value = "获取数据字典", notes = "获取数据字典")
    @PostMapping(value = "/getDictByCode")
    public ResultVo<List<DictModel>> getDictByCode(ParamVo<SynDictVo> paramVo) {
        ResultVo<List<DictModel>> result = ResultVo.getResultVo(ResultCode.SUCCESS);
        try {
            String dictCode = paramVo.getData().getDictCode();
            List<DictModel> ls = sysDictService.queryDictItemsByCode(dictCode);
            result.setResults(ls);
        } catch (Exception e) {
            return ResultVo.getResultVo(ResultCode.ERROR.getResultCode(), e.getMessage());
        }
        return result;
    }


    @RequestAop(value = "刷新数据字典")
    @ApiOperation(value = "刷新数据字典", notes = "刷新数据字典")
    @PostMapping(value = "/refleshCache")
    public ResultVo refleshCache(ParamVo paramVo) {
        ResultVo result = new ResultVo<>();
        sysDictService.refleshCache();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }


}
