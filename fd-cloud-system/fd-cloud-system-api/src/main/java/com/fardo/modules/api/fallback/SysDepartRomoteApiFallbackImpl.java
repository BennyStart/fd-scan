package com.fardo.modules.api.fallback;

import com.fardo.modules.api.SysDepartRomoteApi;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SysDepartRomoteApiFallbackImpl implements SysDepartRomoteApi {

    @Setter
    private Throwable cause;

    @Override
    public SysDepartEntity findDepart(String departId) {
        log.error("获取部门信息错误 {}",departId, cause);
        return null;
    }

    @Override
    public SysDepartEntity findDepartByOrgCode(String orgCode) {
        log.error("根据code获取部门信息错误 {}",orgCode, cause);
        return null;
    }

    /**
     * 功能描述：通过部门ID获取当前顶级部门ID<br>
     *
     * @param deptId
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2022/1/7 17:37
     */
    @Override
    public SysDepartEntity getTopDepart(String deptId) {
        log.error("通过部门ID获取当前顶级部门{}",deptId, cause);
        return null;
    }


}
