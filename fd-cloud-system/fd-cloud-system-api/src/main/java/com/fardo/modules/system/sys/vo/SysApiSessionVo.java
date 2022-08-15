package com.fardo.modules.system.sys.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户端用户会话
 */
@Data
public class SysApiSessionVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String sid;
    /** 创建时间 */
    private String createTime;
    /** 更新时间 */
    private String updateTime;
	/**登录用户实体*/
	private LoginUserVo loginUserVo;
	/**最后访问时间*/
	private String lastAccessTime;
	/**登录用户的IP地址*/
	private String remoteIp;
	/**版本号*/
	private String clientVersion;
}
