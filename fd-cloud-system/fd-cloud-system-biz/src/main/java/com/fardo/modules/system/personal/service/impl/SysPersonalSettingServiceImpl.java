package com.fardo.modules.system.personal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.modules.system.personal.dto.SysPersonalDTO;
import com.fardo.modules.system.personal.dto.SysPersonalSettingDTO;
import com.fardo.modules.system.personal.dto.SysPersonalSettingListDTO;
import com.fardo.modules.system.personal.entity.SysPersonalSettingEntity;
import com.fardo.modules.system.personal.mapper.SysPersonalSettingMapper;
import com.fardo.modules.system.personal.service.ISysPersonalSettingService;
import com.fardo.modules.system.personal.vo.SysPersonalInfoVo;
import com.fardo.modules.system.personal.vo.SysPersonalSettingListVo;
import com.fardo.modules.system.personal.vo.SysPersonalSettingVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/8/31-10:44
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Service
public class SysPersonalSettingServiceImpl extends ServiceImpl<SysPersonalSettingMapper, SysPersonalSettingEntity> implements ISysPersonalSettingService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(SysPersonalSettingDTO data, String id) {
        //用户id
        String userId = data.getUserId();
        //角色id
        String userRole = data.getUserRole();
        //部门id
        String deptId = data.getDeptId();
        //获取地址集合
        List<SysPersonalSettingListDTO> list = data.getList();
        //时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String format = simpleDateFormat.format(new Date());
        //判断不为空
        boolean flag = true;
        if (!CollectionUtils.isEmpty(list)) {
            //先做删除
            QueryWrapper<SysPersonalSettingEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            queryWrapper.eq("user_role", userRole);
            baseMapper.delete(queryWrapper);
            //批量新增
            SysPersonalSettingEntity sysPersonalSettingEntity = new SysPersonalSettingEntity();
            for (SysPersonalSettingListDTO dto : list) {
                //获取常用地点id,为空新增,不为空为修改
                // String addressId = dto.getId();
                // if (StringUtils.isNotBlank(addressId)){
                //     //修改根据id查询信息
                //     sysPersonalSettingEntity = baseMapper.selectById(addressId);
                //     sysPersonalSettingEntity.setAddress(dto.getAddress());
                //     sysPersonalSettingEntity.setType(dto.getTypeSort());
                //     sysPersonalSettingEntity.setUserId(userId);
                //     sysPersonalSettingEntity.setUserRole(userRole);
                //     sysPersonalSettingEntity.setUpdateBy(id);
                //     sysPersonalSettingEntity.setUpdateTime(format);
                //     flag = this.saveOrUpdate(sysPersonalSettingEntity);
                // }else {
                //新增
                sysPersonalSettingEntity = new SysPersonalSettingEntity();
                sysPersonalSettingEntity.setAddress(dto.getAddress());
                sysPersonalSettingEntity.setType(dto.getTypeSort());
                sysPersonalSettingEntity.setUserId(userId);
                sysPersonalSettingEntity.setUserRole(userRole);
                sysPersonalSettingEntity.setDeptId(deptId);
                sysPersonalSettingEntity.setCreateBy(id);
                sysPersonalSettingEntity.setCreateTime(format);
                sysPersonalSettingEntity.setSort(dto.getSort());
                flag = this.save(sysPersonalSettingEntity);
                // }
            }
        }
        return flag;
    }

    @Override
    public SysPersonalSettingVo getList(SysPersonalDTO data) {
        //根据用户id和角色id获取个人常用地址列表
        QueryWrapper<SysPersonalSettingEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", data.getUserId());
        queryWrapper.eq("user_role", data.getUserRole());
        queryWrapper.eq("dept_id", data.getDeptId());
        queryWrapper.orderByAsc("sort");
        List<SysPersonalSettingEntity> sysPersonalSettingEntities = baseMapper.selectList(queryWrapper);
        //存放值
        List<SysPersonalSettingListVo> list = new ArrayList<>();
        SysPersonalSettingListVo vo = new SysPersonalSettingListVo();
        SysPersonalSettingVo settingVo = new SysPersonalSettingVo();
        //判断返回不为空时处理值转换
        if (!CollectionUtils.isEmpty(sysPersonalSettingEntities)) {
            for (SysPersonalSettingEntity sysPersonalSettingEntity : sysPersonalSettingEntities) {
                vo = new SysPersonalSettingListVo();
                sysPersonalSettingEntity.setTypeSort(sysPersonalSettingEntity.getType());
                BeanUtils.copyProperties(sysPersonalSettingEntity, vo);
                list.add(vo);
                settingVo.setUserId(sysPersonalSettingEntity.getUserId());
                settingVo.setUserRole(sysPersonalSettingEntity.getUserRole());
                settingVo.setDeptId(sysPersonalSettingEntity.getDeptId());
                settingVo.setList(list);
            }
        }
        return settingVo;
    }

    @Override
    public SysPersonalInfoVo getSysPersonalInfo(String userId, String userRole, String deptId) {
        QueryWrapper<SysPersonalSettingEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("user_role", userRole);
        queryWrapper.eq("dept_id", deptId);
        queryWrapper.eq("type", "0");
        SysPersonalSettingEntity sysPersonalSettingEntity = baseMapper.selectOne(queryWrapper);
        SysPersonalInfoVo vo = new SysPersonalInfoVo();
        if (!ObjectUtils.isEmpty(sysPersonalSettingEntity)) {
            BeanUtils.copyProperties(sysPersonalSettingEntity, vo);
        }
        return vo;
    }
}
