package com.fardo.modules.system.personal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.modules.system.personal.dto.BlQualitysetUserDTO;
import com.fardo.modules.system.personal.entity.BlQualitysetUserEntity;
import com.fardo.modules.system.personal.mapper.BlQualitysetUserMapper;
import com.fardo.modules.system.personal.service.BlQualitysetUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/10/19-15:33
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Service
public class BlQualitysetUserServiceImpl extends ServiceImpl<BlQualitysetUserMapper, BlQualitysetUserEntity> implements BlQualitysetUserService {
    @Override
    public boolean saveOrUpdate(BlQualitysetUserDTO data, String id) {
        String id1 = data.getId();
        String jyfs = data.getJyfs();
        String type = data.getType();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = simpleDateFormat.format(new Date());
        //id为空新增反之修改
        boolean save = false;
        // if (StringUtils.isBlank(id1)){
        //     BlQualitysetUserEntity entity = new BlQualitysetUserEntity();
        //     entity.setJyfs(jyfs);
        //     entity.setType(type);
        //     entity.setEditTime(format);
        //     entity.setUserId(id);
        //     save = this.save(entity);
        // }else {
        //     BlQualitysetUserEntity entity = baseMapper.selectById(id1);
        //     entity.setJyfs(jyfs);
        //     entity.setType(type);
        //     entity.setEditTime(format);
        //     entity.setUserId(id);
        //     save = this.saveOrUpdate(entity);
        // }

        QueryWrapper<BlQualitysetUserEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("USER_ID", id);
        BlQualitysetUserEntity one = baseMapper.selectOne(queryWrapper);
        if (ObjectUtils.isEmpty(one)){
            BlQualitysetUserEntity entity = new BlQualitysetUserEntity();
            entity.setJyfs(jyfs);
            entity.setType(type);
            entity.setEditTime(format);
            entity.setUserId(id);
            save = this.save(entity);
        }else {
            BlQualitysetUserEntity entity = baseMapper.selectById(one.getId());
            entity.setJyfs(jyfs);
            entity.setType(type);
            entity.setEditTime(format);
            entity.setUserId(id);
            save = this.saveOrUpdate(entity);
        }

        return save;
    }
}
