package com.fardo.modules.system.personal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.personal.dto.BlQualitysetUserDTO;
import com.fardo.modules.system.personal.entity.BlQualitysetUserEntity;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/10/19-15:32
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
public interface BlQualitysetUserService extends IService<BlQualitysetUserEntity> {

    boolean saveOrUpdate(BlQualitysetUserDTO data,String id);
}
