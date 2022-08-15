package com.fardo.modules.system.security.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.modules.system.security.entity.SysIpWhileEntity;
import com.fardo.modules.system.security.mapper.SysIpWhileMapper;
import com.fardo.modules.system.security.service.ISysIpWhileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("sysIpWhileService")
public class SysIpWhileServiceImpl extends ServiceImpl<SysIpWhileMapper, SysIpWhileEntity> implements ISysIpWhileService {

}
