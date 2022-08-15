package com.fardo.modules.system.config.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.modules.system.config.entity.SysSoftwareDownloadEntity;
import com.fardo.modules.system.config.mapper.SysSoftwareDownloadMapper;
import com.fardo.modules.system.config.service.ISysSoftwareDownloadService;
import org.springframework.stereotype.Service;

@Service("sysSoftwareDownloadService")
public class SysSoftwareDownloadServiceImpl extends ServiceImpl<SysSoftwareDownloadMapper, SysSoftwareDownloadEntity> implements ISysSoftwareDownloadService {
}
