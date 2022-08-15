package com.fardo.modules.system.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fardo.common.enums.OperTypeEnum;
import com.fardo.common.util.DateUtils;
import com.fardo.common.util.RedisUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.api.SysUserLogRomoteApi;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.log.entity.SysUserLogEntity;
import com.fardo.modules.system.log.model.AnShenLogInfo;
import com.fardo.modules.system.log.vo.UserInfoLogVo;
import com.fardo.modules.system.sys.service.ApiSessionService;
import com.fardo.modules.system.sys.vo.KickOutNoticeVo;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.sys.vo.SysApiSessionVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ApiSessionServiceImpl implements ApiSessionService {

    private static final String DEBUG = "debug";
    /**
     * 系统级sid 用于系统在无人员登录的情况下发起的请求,需要添加该sid 例如:定时器
     **/
    private static final String SF_SYS_SID = "SYS";
    private static final int SESSION_VALID_TIME = 30 * 60; //会话有效时间，单位秒

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysUserLogRomoteApi userLogRomoteApi;

    /**
     * 创建会话
     *
     * @param randomKey
     * @return
     */
    @Override
    public String createSession(String randomKey) {
        String sid = UUID.randomUUID().toString();
        String key = getRedisKeyOfSession(sid);
        redisUtil.hset(key, FieldOfRandomKey, randomKey, getSessionValidTimeInSeconds());
        return sid;
    }

    /**
     * 激活对话
     *
     * @param sid
     * @param remoteIp
     * @return
     */
    @Override
    public long activeSession(String sid, String remoteIp) {
        if (StringUtils.isEmpty(sid)) {
            return 0;
        }
        String key = getRedisKeyOfSession(sid);
        int sessionValidTimeInSeconds = getSessionValidTimeInSeconds();
        //未登录的会话sid保存7天
        String loginUserVo = getLoginUser(sid);
        if (StringUtil.isEmpty(loginUserVo)) {
            sessionValidTimeInSeconds = sessionValidTimeInSeconds * 2 * 24 * 7;
        }
        redisUtil.hset(key, FieldOfLastAccessTime, DateUtils.date2Str(DateUtils.yyyymmddhhmmss.get()));
        redisUtil.hset(key, FieldOfRemoteIp, remoteIp);
        redisUtil.expire(key, sessionValidTimeInSeconds);
        return sessionValidTimeInSeconds;
    }

    /**
     * 删除对话
     *
     * @param sid
     */
    @Override
    public void removeSession(String sid) {
        String key = getRedisKeyOfSession(sid);
        redisUtil.del(key);
        this.deleteMapApiSession(sid);
    }

    @Override
    public String getSessionValue(String sid, String field) {
        String key = getRedisKeyOfSession(sid);
        Object value = redisUtil.hget(key, field);
        if (value == null) {
            return null;
        }
        return (String) value;
    }

    @Override
    public String getLoginUser(String sid) {
        if (DEBUG.equals(sid)) {
            LoginUserVo loginUserVo = new LoginUserVo();
            loginUserVo.setId(DEBUG);
            loginUserVo.setUsername(DEBUG);
            loginUserVo.setRealname(DEBUG);
            loginUserVo.setPoliceNo(DEBUG);
            return JSON.toJSONString(loginUserVo);
        }
        if (SF_SYS_SID.equals(sid)) {
            LoginUserVo loginUserVo = new LoginUserVo();
            loginUserVo.setId(SF_SYS_SID);
            loginUserVo.setUsername(SF_SYS_SID);
            loginUserVo.setRealname(SF_SYS_SID);
            loginUserVo.setPoliceNo(SF_SYS_SID);
            return JSON.toJSONString(loginUserVo);
        }
        return getSessionValue(sid, FieldOfLoginUser);
    }

    @Override
    public void setLoginUser(String sid, LoginUserVo loginUser, String remoteIp, String clientVersion) {
        String key = getRedisKeyOfSession(sid);
        redisUtil.hset(key, FieldOfLoginUser, JSONObject.toJSONString(loginUser));
        SysApiSessionVo apiSessionEntity = new SysApiSessionVo();
        apiSessionEntity.setSid(sid);
        apiSessionEntity.setRemoteIp(remoteIp);
        apiSessionEntity.setLoginUserVo(loginUser);
        apiSessionEntity.setCreateTime(DateUtils.date2Str(DateUtils.yyyymmddhhmmss.get()));
        apiSessionEntity.setUpdateTime(DateUtils.date2Str(DateUtils.yyyymmddhhmmss.get()));
        apiSessionEntity.setLastAccessTime(DateUtils.date2Str(DateUtils.yyyymmddhhmmss.get()));
        apiSessionEntity.setClientVersion(clientVersion);
        this.hsetMapApiSession(apiSessionEntity);
        this.activeSession(sid, remoteIp);
    }

    @Override
    public boolean isKickedOutSid(String sid) {
        String redisKey = getRedisKeyOfBeKickedOutSidPrefix(sid);
        boolean existed = redisUtil.hasKey(redisKey);
        if (existed) {
            redisUtil.del(redisKey);
        }
        return existed;
    }

    @Override
    public void addKickedOutSid(String sid, KickOutNoticeVo kickOutNoticeVo) {
        String redisKey = getRedisKeyOfBeKickedOutSidPrefix(sid);
        redisUtil.set(redisKey, JSONObject.toJSONString(kickOutNoticeVo), 24 * 60 * 60);
    }

    @Override
    public void checkSessionTimeOut() {
        log.info("开始执行会话检测");
        //STEP2:获取缓存里所有会话
        Map<Object, Object> redisMap = this.getMapApiSession();
        if (redisMap == null) {
            return;
        }
        //STEP3:迭代表中的数据，检测数据是否还在cache中存在，
        for (Map.Entry<Object, Object> entry : redisMap.entrySet()) {
            String sid = (String) entry.getKey();
            String cacheSessionKey = this.getRedisKeyOfSession(sid);
            SysApiSessionVo apiSessionVo = (SysApiSessionVo) entry.getValue();
            if (redisUtil.hasKey(cacheSessionKey)) {
                //存在，则更新last_access_time,update_time
                apiSessionVo.setUpdateTime(DateUtils.date2Str(DateUtils.yyyymmddhhmmss.get()));
                apiSessionVo.setLastAccessTime((String) redisUtil.hget(cacheSessionKey, FieldOfLastAccessTime));//暂准确不区分最后访问时间
                this.hsetMapApiSession(apiSessionVo);
            } else {
                //不存在，则移除记录，并补记注销日志
                this.removeSession(sid);
                this.saveLogoutLog(apiSessionVo);
            }
        }
    }

    @Override
    public void updateLoginUser(String sid, LoginUserVo userVo) {
        String key = getRedisKeyOfSession(sid);
        redisUtil.hset(key, FieldOfLoginUser, JSONObject.toJSONString(userVo));
    }

    private String getRedisKeyOfSession(String sid) {
        return CacheKeyConstants.API_SESSION_PREFIX + sid;
    }

    private int getSessionValidTimeInSeconds() {
        return SESSION_VALID_TIME;
    }

    /**
     * 获取被踢除的sid的redis的key值
     *
     * @param sid
     * @return
     */
    private String getRedisKeyOfBeKickedOutSidPrefix(String sid) {
        return CacheKeyConstants.BE_KICKED_OUT_SID_PREFIX + sid;
    }


    /**
     * 删除集合里的会话
     *
     * @param sid
     */
    private void deleteMapApiSession(String sid) {
        redisUtil.hdel(CacheKeyConstants.API_SESSION_ON_LINE_MAP, sid);
    }

    /**
     * 获取所有在线会话
     *
     * @return
     */
    private Map<Object, Object> getMapApiSession() {
        Map<Object, Object> map = redisUtil.hmget(CacheKeyConstants.API_SESSION_ON_LINE_MAP);
        return map;
    }

    private void hsetMapApiSession(SysApiSessionVo apiSessionVo) {
        redisUtil.hset(CacheKeyConstants.API_SESSION_ON_LINE_MAP, apiSessionVo.getSid(), apiSessionVo);
    }


    @Override
    public boolean hasSession(String sid) {
        return redisUtil.hHasKey(CacheKeyConstants.API_SESSION_ON_LINE_MAP, sid);
    }

    /**
     * 保存超时退出日志
     *
     * @param apiSessionVo
     */
    private void saveLogoutLog(SysApiSessionVo apiSessionVo) {
        SysUserLogEntity logEntity = new SysUserLogEntity();
        LoginUserVo userVo = apiSessionVo.getLoginUserVo();
        String operDesc = "";
        if (userVo != null) {
            logEntity.setDepartId(userVo.getDepartVo().getId());
            UserInfoLogVo userInfo = new UserInfoLogVo(userVo);
            logEntity.setUserInfo(JSON.toJSONString(userInfo));
            operDesc = userVo.getLoginType();
        }
        operDesc = operDesc + "，登录版本号" + apiSessionVo.getClientVersion() + "，" + DateUtils.now() + SysConstants.LOGOUT_TYPE_TIMEOUT;
        logEntity.setOperResult("0");
        OperTypeEnum operTypeEnum = OperTypeEnum.YHDC;
        logEntity.setOperName(operTypeEnum.getOperName());
        logEntity.setOperType(operTypeEnum.getOperType());
        logEntity.setOperModule(operTypeEnum.getOperModule());
        logEntity.setOperDesc(operDesc);
        logEntity.setData1(operDesc);
        AnShenLogInfo anShenLogInfo = new AnShenLogInfo(operTypeEnum.getOperateType(), operTypeEnum.getResourceType(), operTypeEnum.getResourceName());
        logEntity.setData2(JSON.toJSONString(anShenLogInfo));
        this.userLogRomoteApi.save(logEntity);
    }

}
