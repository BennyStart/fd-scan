package com.fardo.modules.system.area.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.system.area.entity.SysAreaEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAreaMapper extends BaseMapper<SysAreaEntity> {

    List<SysAreaEntity> findByParentAreaCode(@Param("parentAreaId") String parentAreaId);
}
