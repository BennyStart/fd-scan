package com.fardo.modules.api;

import com.fardo.common.constant.ServiceNameConstants;
import com.fardo.modules.api.factory.SysAreaRemoteApiFallbackFactory;
import com.fardo.modules.system.area.entity.SysAreaEntity;
import com.fardo.modules.system.area.vo.QuerySpecifiedLevelAreaDataVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2020年05月21日 14:32
 */
@Component
@FeignClient(contextId = "sysAreaRemoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = SysAreaRemoteApiFallbackFactory.class)
public interface SysAreaRemoteApi {


    @GetMapping("/sys/area/getArea/{id}")
    String getArea(@PathVariable("id") String id);

    /**
     * 功能描述：通过区域编号查询指定等级区域<br>
     *
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2021/12/27 15:39
     **/
    @PostMapping("/sys/area/getSpecifiedLevelListByAreaCode")
    List<SysAreaEntity> getSpecifiedLevelListByAreaCode(@RequestBody QuerySpecifiedLevelAreaDataVo vo);

}
