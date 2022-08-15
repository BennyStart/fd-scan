package com.fardo.modules.system.gateway.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.gateway.entity.SysGatewayRouteEntity;

/**
 * @Description: gateway路由管理
 * @Author: maozf
 * @Date:   2020-05-26
 * @Version: V1.0
 */
public interface ISysGatewayRouteService extends IService<SysGatewayRouteEntity> {

    /**
     * 添加所有的路由信息到redis
     * @param key
     */
    public void addRoute2Redis(String key);

    /**
     * 保存路由配置
     * @param array
     */
    void updateAll(JSONArray array);

    /**
     * 清空redis中的route信息
     */
    void clearRedis();

}
