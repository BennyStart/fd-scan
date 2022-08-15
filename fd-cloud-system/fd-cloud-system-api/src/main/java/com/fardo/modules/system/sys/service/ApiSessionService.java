package com.fardo.modules.system.sys.service;

import com.fardo.modules.system.sys.vo.KickOutNoticeVo;
import com.fardo.modules.system.sys.vo.LoginUserVo;


/**
 * @描述:接口会话管理
 * 客户端与服务端协商完密钥后，由服务端分配一个会话id给客户端，之后客户端的所有请求必须携带此会话sid
 */
public interface ApiSessionService {

    String FieldOfSid = "sid";
    String FieldOfRandomKey = "randomKey";
    String FieldOfLoginUser = "loginUser";
    String FieldOfLastAccessTime = "lastAccessTime";
    String FieldOfRemoteIp = "remoteIp";
    /**
     * 创建一个会话
     * 并把密钥存入关联当前会话
     * @param randomKey
     * @return 返回会话id
     */
    public String createSession(String randomKey);
    /**
     * 重置超时时间点:即session失效时间=当前时间+ 超时时长
     * @param sid
     * @return
     */
    long activeSession(String sid, String remoteIp);
    /**
     * 删除session
     * @param sid
     */
    void removeSession(String sid);

    String getSessionValue(String sid, String field);

    /**
     * 获取当前登录用户，未登录时返回null
     * @param sid
     * @return
     */
    String getLoginUser(String sid);
    /**
     * 当登录成功时设置
     * @param sid
     * @param loginUser
     */
    void setLoginUser(String sid, LoginUserVo loginUser, String remoteIp, String clientVersion);
    /**
     * 判断当前的sid是否是被踢除的sid
     *
     * @param sid
     * @return
     */
    boolean isKickedOutSid(String sid);
    /**
     * 添加被踢除的sid
     * @param sid
     */
    void addKickedOutSid(String sid, KickOutNoticeVo kickOutNoticeVo);

    /**
     * 会话管理：定期检测api会话
     */
    void checkSessionTimeOut();

    /**
     * 更新登录用户信息
     */
    void updateLoginUser(String sid, LoginUserVo userVo);

    /**
     * 是否存在会话
     * @param sid
     * @return
     */
    boolean hasSession(String sid);

}
