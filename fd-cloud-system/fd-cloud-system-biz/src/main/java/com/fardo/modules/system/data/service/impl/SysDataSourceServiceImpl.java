package com.fardo.modules.system.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.modules.system.data.entity.SysDataSourceEntity;
import com.fardo.modules.system.data.service.ISysDataSourceService;
import com.fardo.modules.system.data.mapper.SysDataSourceMapper;
import org.springframework.stereotype.Service;

/**
 * @Description: 多数据源管理
 * @Author: maozf
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Service
public class SysDataSourceServiceImpl extends ServiceImpl<SysDataSourceMapper, SysDataSourceEntity> implements ISysDataSourceService {

}
