package com.fardo.modules.system.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.common.enums.OperTypeEnum;
import com.fardo.modules.system.log.entity.SysUserLogEntity;
import com.fardo.modules.system.log.model.LogDetailModel;
import com.fardo.modules.system.log.model.LogGridModel;
import com.fardo.modules.system.log.vo.LogIdVo;
import com.fardo.modules.system.log.vo.LogQueryVo;
import com.fardo.modules.system.sys.vo.ParamVo;

import java.util.List;

/**
 * @(#)ISysUserLogService <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/7/22 9:29
 * 描　述：
 */
public interface ISysUserLogService extends IService<SysUserLogEntity> {

    void saveLog(ParamVo paramVo, OperTypeEnum operTypeEnum, String operDesc, String api);

    /**
     * 获取用户登录次数
     * @return
     */
    Integer getUserDlcs();

    IPage<LogGridModel> pageList(LogQueryVo vo);

    LogDetailModel detail(LogIdVo vo);

    List<SysUserLogEntity> list(LogQueryVo vo);

}
