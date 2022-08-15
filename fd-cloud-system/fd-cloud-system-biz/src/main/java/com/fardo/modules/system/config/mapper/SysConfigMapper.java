package com.fardo.modules.system.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.modules.system.config.entity.SysConfigEntity;
import com.fardo.modules.znbl.picmanage.vo.PicmanageListVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 服务端配置 Mapper 接口
 * <p>
 *
 */
public interface SysConfigMapper extends BaseMapper<SysConfigEntity> {

    IPage<SysConfigEntity> getSysConfigEntityPage(Page<SysConfigEntity> page, @Param("code") String code, @Param("name") String name, @Param("remark")  String remark);


}
