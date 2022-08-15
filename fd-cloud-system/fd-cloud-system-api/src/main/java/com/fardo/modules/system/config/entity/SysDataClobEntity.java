package com.fardo.modules.system.config.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_data_clob")
public class SysDataClobEntity {

    /** ID */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "ID")
    private String id;
    /**
     * 引用数据的业务表名
     */
    private String refTableName;
    /**
     * 引用数据的业务表字段名
     */
    private String refTableFieldName;
    /**
     * 数据值
     */
    private String clobData;

}
