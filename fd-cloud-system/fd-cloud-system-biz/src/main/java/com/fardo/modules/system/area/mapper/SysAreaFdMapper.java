package com.fardo.modules.system.area.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.system.area.entity.SysAreaFdEntity;
import org.apache.ibatis.annotations.Param;

public interface SysAreaFdMapper extends BaseMapper<SysAreaFdEntity> {

    SysAreaFdEntity getByAreaCode(@Param("areaCode") String areaCode);
}
