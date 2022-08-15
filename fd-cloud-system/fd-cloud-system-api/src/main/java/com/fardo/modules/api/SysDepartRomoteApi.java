package com.fardo.modules.api;

import com.fardo.common.constant.ServiceNameConstants;
import com.fardo.modules.api.fallback.SysDepartRomoteApiFallbackImpl;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Component
@FeignClient(contextId = "sysDepartRomoteApi", value = ServiceNameConstants.SYSTEM_SERVICE, fallback = SysDepartRomoteApiFallbackImpl.class,fallbackFactory = SysDepartRomoteApiFallbackImpl.class)
public interface SysDepartRomoteApi {

    /**
     * 通过部门id获取部门信息
     *
     * @param departId 部门id
     * @return 部门信息
     */
    @GetMapping("/sys/sysDepart/findDepart/{departId}")
    SysDepartEntity findDepart(@PathVariable("departId") String departId);

    /**
     * 通过部门code获取部门信息
     * @param orgCode
     * @return
     */
    @GetMapping("/sys/sysDepart/findDepartByOrgCode/{orgCode}")
    SysDepartEntity findDepartByOrgCode(@PathVariable("orgCode") String orgCode);

    /**
     * 功能描述：通过部门ID获取当前顶级部门ID<br>
     *
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2022/1/7 17:37
     **/
    @GetMapping("/sys/sysDepart/getTopDepart/{deptId}")
    SysDepartEntity getTopDepart(@PathVariable("deptId") String deptId);
}
