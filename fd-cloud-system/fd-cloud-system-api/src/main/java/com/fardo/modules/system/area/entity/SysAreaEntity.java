package com.fardo.modules.system.area.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_area")
public class SysAreaEntity  implements Serializable {

    private String id;

    private String name;

    private String parentid;

    private String enable;

}
