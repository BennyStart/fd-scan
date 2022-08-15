package com.fardo.modules.api;

import com.fardo.common.api.vo.Result;
import com.fardo.common.constant.ServiceNameConstants;
import com.fardo.common.system.vo.DictModel;
import com.fardo.modules.api.factory.SysDictRomoteApiFallbackFactory;
import com.fardo.modules.api.fallback.SysDictRomoteApiFallbackImpl;
import com.fardo.modules.system.config.entity.SysDictItemEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


@Component
@FeignClient(contextId = "SysDictRomoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallback = SysDictRomoteApiFallbackImpl.class, fallbackFactory = SysDictRomoteApiFallbackFactory.class)
public interface SysDictRomoteApi {


    /**
     * 字典编码&类型编码获取字典数据
     *
     * @param dictCode
     * @param key
     * @return
     */
    @GetMapping("/sys/dict/getDictText/{dictCode}/{key}")
    String getDictTextByCodeAndKey(@PathVariable("dictCode") String dictCode, @PathVariable("key") String key);


    @GetMapping("/sys/dict/getDictItems/{dictCode}")
    Result<List<DictModel>> getDictItems(@PathVariable("dictCode") String dictCode);

    @GetMapping("/sys/dict/findKeyByInfo/{key}")
    List<SysDictItemEntity> findKeyByInfo(@PathVariable("key") String key);
}
