package com.fardo.modules.system.log.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.enums.OperTypeEnum;
import com.fardo.common.util.DateUtils;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.log.entity.SysUserLogEntity;
import com.fardo.modules.system.log.mapper.SysUserLogMapper;
import com.fardo.modules.system.log.model.AnShenLogInfo;
import com.fardo.modules.system.log.model.LogDetailModel;
import com.fardo.modules.system.log.model.LogGridModel;
import com.fardo.modules.system.log.service.ISysUserLogService;
import com.fardo.modules.system.log.vo.LogIdVo;
import com.fardo.modules.system.log.vo.LogQueryVo;
import com.fardo.modules.system.log.vo.UserInfoLogVo;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @(#)SysUserLogServiceImpl <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/7/22 9:58
 * 描　述：
 */
@Transactional
@Service
public class SysUserLogServiceImpl extends ServiceImpl<SysUserLogMapper, SysUserLogEntity> implements ISysUserLogService {

    /**
     * 保存日志
     * @param paramVo
     * @param operTypeEnum
     * @param operDesc
     * @param api
     */
    @Override
    public void saveLog(ParamVo paramVo, OperTypeEnum operTypeEnum, String operDesc, String api) {
        SysUserLogEntity logEntity = new SysUserLogEntity();
        logEntity.setAppKey(paramVo.getApp_key());
        LoginUserVo userVo = LoginUtil.getLoginUser();
        if(userVo != null) {
            logEntity.setDepartId(userVo.getDepartVo().getId());
            UserInfoLogVo userInfo = new UserInfoLogVo(userVo);
            logEntity.setUserInfo(JSON.toJSONString(userInfo));
        }
        logEntity.setOperResult("0");
        logEntity.setOperName(operTypeEnum.getOperName());
        logEntity.setOperType(operTypeEnum.getOperType());
        logEntity.setOperModule(operTypeEnum.getOperModule());
        logEntity.setOperDesc(operDesc);
        logEntity.setData1(operDesc);
        AnShenLogInfo anShenLogInfo = new AnShenLogInfo(operTypeEnum.getOperateType(),operTypeEnum.getResourceType(),operTypeEnum.getResourceName());
        logEntity.setData2(JSON.toJSONString(anShenLogInfo));
        logEntity.setRequestIp(paramVo.getRemoteIp());
        logEntity.setRequestUrl(api);
        logEntity.setVersion(paramVo.getClientVersion());
        this.save(logEntity);
    }

    /**
     * 分页获取
     * @param vo
     * @return
     */
    @Override
    public IPage<LogGridModel> pageList(LogQueryVo vo) {
        return this.baseMapper.pageList(new Page(vo.getPageNo(), vo.getPageSize()), vo);
    }


    /**
     * 获取详情
     * @param vo
     * @return
     */
    @Override
    public LogDetailModel detail(LogIdVo vo) {
        SysUserLogEntity entity = this.getById(vo.getId());
        LogDetailModel model = new LogDetailModel();
        if(StringUtil.isNotBlank(entity.getUserInfo())) {
            UserInfoLogVo userInfo = JSONObject.parseObject(entity.getUserInfo(), UserInfoLogVo.class);
            model.setUsername(userInfo.getUsername());
            model.setDepartName(userInfo.getDepartNmae());
        }
        model.setOperType(entity.getOperType());
        model.setOperName(entity.getOperName());
        if("0".equals(entity.getOperResult())) {
            model.setOperResult("成功");
        } else {
            model.setOperResult("失败");
        }
        model.setCreateTime(DateUtils.toDisplayStr(entity.getCreateTime(), DateUtils.datetimeFormat.get()));
        model.setRequestIp(entity.getRequestIp());
        model.setOperDesc(entity.getOperDesc());
        model.setResultData(entity.getData1());
        return model;
    }

    /**
     * 获取用户登录次数
     * @return
     */
    @Override
    public Integer getUserDlcs() {
        LambdaQueryWrapper<SysUserLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserLogEntity::getCreateBy, LoginUtil.getLoginUser().getId());
        queryWrapper.eq(SysUserLogEntity::getOperType, OperTypeEnum.YHDL.getOperType());
        Integer dlsc = this.count(queryWrapper);
        return dlsc;
    }

    @Override
    public List<SysUserLogEntity> list(LogQueryVo vo) {
        return this.baseMapper.list(vo);
    }
}
