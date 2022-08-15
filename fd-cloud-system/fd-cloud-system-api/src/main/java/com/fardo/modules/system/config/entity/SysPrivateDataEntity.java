package com.fardo.modules.system.config.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.util.DateUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_private_data")
public class SysPrivateDataEntity {

    /** ID */
    @TableId(type = IdType.ID_WORKER_STR)
    @ApiModelProperty(value = "ID")
    private String id;
    /**
     * 内容
     */
    private String data;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 修改时间
     */
    private String updateTime;
    /**
     * 请求地址
     */
    private String requestIp;
    public SysPrivateDataEntity() {
    }

    public SysPrivateDataEntity(String id, String data) {
        super();
        this.id = id;
        this.data = data;
        this.createTime = DateUtils.getCurrentTime();
        this.updateTime = DateUtils.getCurrentTime();
    }

    public static SysPrivateDataEntity createMachine(String machineID) {
        return new SysPrivateDataEntity(machineID, "MACHINE_INFO");
    }

}
