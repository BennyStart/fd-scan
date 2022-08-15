package com.fardo.modules.system.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.modules.system.log.entity.SysUserLogEntity;
import com.fardo.modules.system.log.model.LogGridModel;
import com.fardo.modules.system.log.vo.LogQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @(#)SysUserLogMapper <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：wangbt
 * 时　间：2021/7/22 9:29
 * 描　述：
 */
public interface SysUserLogMapper extends BaseMapper<SysUserLogEntity> {

    IPage<LogGridModel> pageList(Page page, @Param("vo")LogQueryVo vo);

    List<SysUserLogEntity> list(@Param("vo")LogQueryVo vo);

}
