package com.fardo.modules.system.security.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_api_secret")
public class SysApiSecretEntity extends BaseEntity {

    /**接口key*/
    private java.lang.String apiKey;
    /**签名秘钥*/
    private java.lang.String apiSecret;
    /**公司名称*/
    private java.lang.String gsmc;
    /**地址*/
    private java.lang.String dz;
    /**接入方应用类型*/
    private java.lang.String yylx;
    /**接入数上限*/
    private java.lang.Integer jrsx;
    /**并发数上限*/
    private java.lang.Integer bfsx;
    /**联系人*/
    private java.lang.String lxr;
    /**电话*/
    private java.lang.String dh;
    /**手机号*/
    private java.lang.String sj;
    /**email*/
    private java.lang.String email;


}
