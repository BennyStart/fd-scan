package com.fardo.modules.system.personal.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("T_BL_QUALITYSET_USER")
public class BlQualitysetUserEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;

    /**
     * 用户id（不可重复）
     */
    private String userId;

    /**
     * '检验方式'
     */
    private String jyfs;

    /**
     * 默认类型：是否开启：0不开启,1开启
     */
    private String type;

    /**
     * '最后更新时间'
     */
    private String editTime;


}
