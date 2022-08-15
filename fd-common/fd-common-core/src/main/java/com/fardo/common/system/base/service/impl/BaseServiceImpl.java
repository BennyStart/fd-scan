package com.fardo.common.system.base.service.impl;

import com.fardo.common.system.base.service.BaseService;
import com.fardo.common.system.base.entity.BaseEntity;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: ServiceImpl基类
 * @Author: maozf
 * @Date: 20-11-25
 * @Version: 1.0
 */
@Slf4j
public class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {

}
