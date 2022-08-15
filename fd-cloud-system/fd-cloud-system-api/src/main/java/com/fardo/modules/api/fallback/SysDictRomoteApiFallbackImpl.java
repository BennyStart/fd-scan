package com.fardo.modules.api.fallback;

import com.fardo.common.api.vo.Result;
import com.fardo.common.system.vo.DictModel;
import com.fardo.modules.api.SysDictRomoteApi;
import com.fardo.modules.system.config.entity.SysDictItemEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SysDictRomoteApiFallbackImpl implements SysDictRomoteApi {

    @Setter
    private Throwable cause;

    @Override
    public String getDictTextByCodeAndKey(String dictCode, String key) {
        log.error("获取字典名称错误 {},{}",dictCode, key, cause);
        return null;
    }

    @Override
    public Result<List<DictModel>> getDictItems(String dictCode) {
        log.error("获取数字字段错误 {},{}",dictCode, cause);
        return null;
    }

    @Override
    public List<SysDictItemEntity> findKeyByInfo(String key) {
        log.error("获取数字字段错误 {},{}",key, cause);
        return null;
    }
}
