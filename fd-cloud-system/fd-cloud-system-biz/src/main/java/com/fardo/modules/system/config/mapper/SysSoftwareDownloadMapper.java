package com.fardo.modules.system.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.system.config.entity.SysSoftwareDownloadEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface SysSoftwareDownloadMapper  extends BaseMapper<SysSoftwareDownloadEntity> {

    /**删除下载地址表记录*/
    @Delete("delete from T_SYS_SOFTWARE_DOWNLOAD where software_id = #{softId}")
    void deleteBySoftId(@Param("softId") String softId);
}
