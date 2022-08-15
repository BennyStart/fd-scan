package com.fardo.modules.system.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fardo.modules.system.config.entity.SysDictEntity;
import com.fardo.modules.system.config.entity.SysDictItemEntity;
import com.fardo.modules.system.config.mapper.SysDictItemMapper;
import com.fardo.modules.system.config.mapper.SysDictMapper;
import com.fardo.modules.system.config.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@Service
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItemEntity> implements ISysDictItemService {

    @Autowired
    private SysDictItemMapper sysDictItemMapper;
    @Autowired
    private SysDictMapper sysDictMapper;

    @Override
    public List<SysDictItemEntity> selectItemsByMainId(String mainId) {
        return sysDictItemMapper.selectItemsByMainId(mainId);
    }

    @Override
    public List<SysDictItemEntity> findKeyByInfo(String key) {
        QueryWrapper<SysDictEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code",key);
        List<SysDictEntity> sysDictEntities = sysDictMapper.selectList(queryWrapper);
        List<SysDictItemEntity> sysDictItemEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sysDictEntities)){
            String id = sysDictEntities.get(0).getId();
            QueryWrapper<SysDictItemEntity> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("dict_id",id);
            sysDictItemEntities = sysDictItemMapper.selectList(queryWrapper1);
        }
        return sysDictItemEntities;
    }
}
