package com.fardo.modules.system.config.mapper;

import com.fardo.modules.system.config.entity.SysDictItemEntity;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface SysDictItemMapper extends BaseMapper<SysDictItemEntity> {
    @Select("SELECT * FROM t_sys_dict_item WHERE DICT_ID = #{mainId} order by sort_order asc, item_value asc")
    public List<SysDictItemEntity> selectItemsByMainId(String mainId);
}
