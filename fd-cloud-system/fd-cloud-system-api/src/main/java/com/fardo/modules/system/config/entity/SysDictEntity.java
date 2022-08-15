package com.fardo.modules.system.config.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_dict")
public class SysDictEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * [预留字段，暂时无用]
     * 字典类型,0 string,1 number类型,2 boolean
     * 前端js对stirng类型和number类型 boolean 类型敏感，需要区分。在select 标签匹配的时候会用到
     * 默认为string类型
     */
    private Integer type;
    
    /**
     * 字典名称
     */
    @Excel(name ="字典文本")
    private String dictName;

    /**
     * 字典编码
     */
    @Excel(name = "字典编码")
    private String dictCode;

    /**
     * 描述
     */
    private String description;

    /**
     * 删除状态
     */
    @TableLogic
    private Integer delFlag;


}
