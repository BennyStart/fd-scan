package com.fardo.modules.system.config.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fardo.common.aspect.annotation.Dict;
import com.fardo.common.system.base.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sys_dict_item")
public class SysDictItemEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字典id
     */
    @Excel(name ="字典类型")
    private String dictId;

    /**
     * 字典项文本
     */
    @Excel(name = "字典文本", width = 20)
    private String itemText;

    /**
     * 字典项值
     */
    @Excel(name = "字典编码", width = 30)
    private String itemValue;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    @Excel(name = "字典排序", width = 15,type=4)
    private Integer sortOrder;


    /**
     * 状态（1启用 0不启用）
     */
    @Dict(dicCode = "dict_item_status")
    private Integer status;


}
