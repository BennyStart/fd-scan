package com.fardo.modules.system.config.service;

import com.fardo.modules.system.config.entity.SysDictItemEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
public interface ISysDictItemService extends IService<SysDictItemEntity> {
    public List<SysDictItemEntity> selectItemsByMainId(String mainId);

    List<SysDictItemEntity> findKeyByInfo(String key);
}
