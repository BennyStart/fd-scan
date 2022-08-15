package com.fardo.modules.system.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.modules.system.config.entity.SysSoftwareInfoEntity;
import com.fardo.modules.system.config.model.SysSoftwareInfoModel;
import com.fardo.modules.system.config.vo.SysSoftwareInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysSoftwareInfoMapper extends BaseMapper<SysSoftwareInfoEntity> {

    /**
     * 多表分页查询软件下载列表
     * @param page 分页实体
     * @param sysSoftwareInfoVo 查询条件
     * @return
     */
    List<SysSoftwareInfoModel> getPageList(IPage page,@Param("softVo") SysSoftwareInfoVo sysSoftwareInfoVo);

    Integer getPageListTotal(@Param("softVo") SysSoftwareInfoVo sysSoftwareInfoVo);


}
