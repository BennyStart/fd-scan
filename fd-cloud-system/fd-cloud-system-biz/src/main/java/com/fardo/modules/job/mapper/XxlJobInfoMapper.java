package com.fardo.modules.job.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface XxlJobInfoMapper extends BaseMapper {

    @Update("update xxl_job_info set job_cron = #{cron},trigger_next_time = #{nextTime}, trigger_status = 1 where id = #{id}")
    void updateCron(@Param("cron") String cron, @Param("nextTime") long nextTime, @Param("id") Integer id);

    @Update("update xxl_job_info set trigger_status = 0,trigger_last_time = 0, trigger_next_time = 0 where id = #{id}")
    void stop(@Param("id") Integer id);


}
