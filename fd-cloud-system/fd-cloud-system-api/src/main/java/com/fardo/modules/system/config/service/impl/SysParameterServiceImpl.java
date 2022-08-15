package com.fardo.modules.system.config.service.impl;

import com.fardo.common.util.RedisUtil;
import com.fardo.modules.api.SysParameterRemoteApi;
import com.fardo.modules.system.config.service.SysParameterService;
import com.fardo.modules.system.constant.CacheKeyConstants;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysParameterServiceImpl implements SysParameterService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SysParameterRemoteApi sysParameterRemoteApi;

    /**
     * 先查找缓存
     * 缓存不存在，再查找数据库，并把数据库的值放入缓存
     * @param key
     * @return
     */
    @Override
    public String getSysParam(String key) {
        Object cache = redisUtil.hget(CacheKeyConstants.SYS_PARAMETER, key);
        String value;
        if(cache == null) {
            value = sysParameterRemoteApi.getSysParam(key);
        } else {
            value = (String) cache;
        }
        return value;
    }

    @Override
    public int getIntSysParam(String key) {
        String valueStr = getSysParam(key);
        return NumberUtils.toInt(valueStr);
    }

    @Override
    public boolean getBoolSysParam(String key) {
        String valueStr = getSysParam(key);
        return BooleanUtils.toBoolean(valueStr);
    }
}
