package com.fardo.modules.system.config.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.modules.system.config.mapper.BlDmBllxConfigMapper;
import com.fardo.modules.system.config.service.BlDmBllxConfigService;
import com.fardo.modules.znbl.bl.entity.BlDmBllxConfigEntity;
import com.fardo.modules.znbl.bl.vo.BlBllxConfigObjectVo;
import com.fardo.modules.znbl.bl.vo.BlDmBllxConfigObjectVo;
import com.fardo.modules.znbl.ywxt.entity.BlDmBldlxEntity;
import com.fardo.modules.znbl.ywxt.entity.BlDmBllxEntity;
import com.fardo.modules.znbl.ywxt.model.BlBldlxModel;
import com.fardo.modules.znbl.ywxt.model.BlBllxChildrenModel;
import com.fardo.modules.znbl.ywxt.model.BlBllxModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/7/14-15:22
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
@Service
public class BlDmBllxConfigServiceImpl extends ServiceImpl<BlDmBllxConfigMapper, BlDmBllxConfigEntity> implements BlDmBllxConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(List<BlDmBllxConfigObjectVo> list) {
        //先做删除
        baseMapper.delete();
        //新增
        Integer insert = 0;
        for (BlDmBllxConfigObjectVo blDmBllxConfigObjectVo : list) {
            BlDmBllxConfigEntity bl = new BlDmBllxConfigEntity();
            BeanUtils.copyProperties(blDmBllxConfigObjectVo, bl);
            insert = baseMapper.insert(bl);
        }
        return insert;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveOrUpdate(List<BlBllxConfigObjectVo> list) {
        Integer i = 0;
        for (BlBllxConfigObjectVo blBllxConfigObjectVo : list) {
            //是否显示（0-否，1-是）
            String sfxs = blBllxConfigObjectVo.getSfxs();
            //笔录类型编号
            String bh = blBllxConfigObjectVo.getBh();
            if (bh.contains("-")) {
                String[] split = bh.split("-");
                bh = split[0];
                //更新类型对象
                String typeBh = split[1];
                if (StringUtils.isNotBlank(typeBh)) {
                    i = baseMapper.editBh(bh, typeBh, sfxs);
                }
            } else {
                //根据笔录类型更新是否显示
                BlDmBllxEntity blDmBllxEntity = baseMapper.getBlDmBllxEntity(bh);
                if (!ObjectUtils.isEmpty(blDmBllxEntity) && StringUtils.isNotBlank(blDmBllxEntity.getId())) {
                    i = baseMapper.updateById(bh, sfxs);
                } else {
                    BlDmBldlxEntity blDmBldlxEntity = baseMapper.getBlDmBldlxEntity(bh);
                    if (!ObjectUtils.isEmpty(blDmBldlxEntity)) {
                        i = baseMapper.updateByBlDmBldlxId(bh, sfxs);
                    }
                }
            }
        }
        return i;
    }

    @Override
    public List<BlBldlxModel> getBldlxTree() {
        //查询是否显示的笔录类型
        List<BlDmBldlxEntity> maps = baseMapper.selectList();
        //将数据转换
        List<BlBldlxModel> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(maps)) {
            BlBldlxModel blBldlxModel = null;
            for (BlDmBldlxEntity map : maps) {
                blBldlxModel = new BlBldlxModel();
                //获取编号
                blBldlxModel.setId(map.getId());
                //获取名称
                blBldlxModel.setName(map.getMc());
                blBldlxModel.setSfxs(map.getSfxs());
                blBldlxModel.setPh(map.getPh());
                list.add(blBldlxModel);
            }
        }
        return list;
    }

    @Override
    public List<BlBllxModel> getBlBllxEntity(String dlx) {
        //查询笔录类型
        List<BlDmBllxEntity> blBllxEntities = baseMapper.getBlBllxEntityList(dlx);
        //将数据转换
        List<BlBllxModel> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(blBllxEntities)) {
            BlBllxModel blBldlxModel = null;
            for (BlDmBllxEntity blDmBllxEntity : blBllxEntities) {
                blBldlxModel = new BlBllxModel();
                if (StringUtils.isNotBlank(blDmBllxEntity.getLxId())) {
                    //获取编号
                    blBldlxModel.setId(blDmBllxEntity.getId() + "-" + blDmBllxEntity.getLxId());
                    if (StringUtils.isNotBlank(blDmBllxEntity.getLxMc())) {
                        //获取名称
                        blBldlxModel.setName(blDmBllxEntity.getMc() + "-" + blDmBllxEntity.getLxMc());
                        //是否显示
                        blBldlxModel.setSfxs(blDmBllxEntity.getDxsfxs());
                    } else {
                        //获取名称
                        blBldlxModel.setName(blDmBllxEntity.getMc());
                        //是否显示
                        blBldlxModel.setSfxs(blDmBllxEntity.getSfxs());
                    }
                } else {
                    //获取编号
                    blBldlxModel.setId(blDmBllxEntity.getId());
                    //获取名称
                    blBldlxModel.setName(blDmBllxEntity.getMc());
                    //是否显示
                    blBldlxModel.setSfxs(blDmBllxEntity.getSfxs());
                }
                list.add(blBldlxModel);
            }
        }
        return list;
    }

    @Override
    public List<BlBllxModel> getBlBllxBackUpEntity(String dlx) {
        //查询笔录类型
        List<BlDmBllxEntity> blBllxEntities = baseMapper.getBlBllxBackUpEntityList(dlx);
        //将数据转换
        List<BlBllxModel> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(blBllxEntities)) {
            BlBllxModel blBldlxModel = null;
            for (BlDmBllxEntity blDmBllxEntity : blBllxEntities) {
                blBldlxModel = new BlBllxModel();
                //获取编号
                blBldlxModel.setId(blDmBllxEntity.getId());
                //获取名称
                blBldlxModel.setName(blDmBllxEntity.getMc());
                //是否显示
                blBldlxModel.setSfxs(blDmBllxEntity.getSfxs());
                //子集
                List<BlBllxChildrenModel> blBllxChildren = baseMapper.getBlBllxChildren(blDmBllxEntity.getId());
                blBldlxModel.setChildren(blBllxChildren);
                list.add(blBldlxModel);
            }
        }
        return list;
    }
}
