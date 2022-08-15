package com.fardo.modules.job.controller;

import com.fardo.modules.job.cron.CronExpression;
import com.fardo.modules.job.mapper.XxlJobInfoMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/sys/job")
@Api(tags="定时器操作")
public class JobController {


    @Autowired
    private XxlJobInfoMapper xxlJobInfoMapper;


    @ApiOperation("修改cron")
    @RequestMapping(value = "/updateCron/{id}/{day}/{hour}", method = RequestMethod.GET)
    public void save(@PathVariable("id") Integer id, @PathVariable("day") String day, @PathVariable("hour") String hour) {
        String cronFormat = "0 0 %s 1/%s * ? *";
        String cron = String.format(cronFormat, hour, day);
        Date nextTime = new Date();
        try {
            CronExpression cronExpression = new CronExpression(cron);
            nextTime = cronExpression.getNextValidTimeAfter(nextTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        xxlJobInfoMapper.updateCron(cron, nextTime.getTime(), id);
    }

    @ApiOperation("停止job")
    @RequestMapping(value = "/stop/{id}", method = RequestMethod.GET)
    public void stop(@PathVariable("id") Integer id) {
        xxlJobInfoMapper.stop(id);
    }

}
