package com.fardo.modules.api.fallback;

import com.alibaba.fastjson.JSON;
import com.fardo.modules.api.SysAreaRemoteApi;
import com.fardo.modules.system.area.entity.SysAreaEntity;
import com.fardo.modules.system.area.vo.QuerySpecifiedLevelAreaDataVo;
import com.google.common.collect.Lists;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class SysAreaRemoteApiFallback implements SysAreaRemoteApi {

    @Setter
    private Throwable cause;

    @Override
    public String getArea(String id) {
        log.info("--获取区域信息异常--username:" + id, cause);
        return null;
    }

    /**
     * 功能描述：通过区域编号查询指定等级区域<br>
     *
     * @param vo
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2021/12/27 15:39
     */
    @Override
    public List<SysAreaEntity> getSpecifiedLevelListByAreaCode(QuerySpecifiedLevelAreaDataVo vo) {
        log.info("--通过区域编号查询指定等级区域--vo:" + JSON.toJSONString(vo), cause);
        return Lists.newArrayList();
    }
}
