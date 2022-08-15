package com.fardo.modules.system.area.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.modules.system.area.entity.SysAreaEntity;
import com.fardo.modules.system.area.mapper.SysAreaMapper;
import com.fardo.modules.system.area.service.ISysAreaService;
import com.fardo.modules.system.area.vo.QuerySpecifiedLevelAreaDataVo;
import com.fardo.modules.system.constant.SysConstants;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysAreaServiceImpl extends ServiceImpl<SysAreaMapper, SysAreaEntity> implements ISysAreaService {
    @Resource
    SysAreaMapper sysAreaMapper;

    /**
     * 顶级区域代码
     **/
    private static final String SF_TOP_AREA_CODE = "-1";
    /**
     * 省级区域代码 代码长度
     **/
    private static final int SF_PROVINCE_AREA_CODE_LENGTH = 2;
    /**
     * 市级区域代码 代码长度
     **/
    private static final int SF_CITY_AREA_CODE_LENGTH = 4;
    /**
     * 区/县级区域代码 代码长度
     **/
    private static final int SF_COUNTY_AREA_CODE_LENGTH = 6;

    @Override
    public List<SysAreaEntity> findByParentAreaCode(String parentAreaId) {
        return sysAreaMapper.findByParentAreaCode(parentAreaId);
    }

    @Override
    public SysAreaEntity getSysAreaEntity(String id) {
        return sysAreaMapper.selectById(id);
    }

    /**
     * 功能描述：通过区域编号查询指定等级区域<br>
     *
     * @param vo
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2021/12/27 15:51
     */
    @Override
    public List<SysAreaEntity> getSpecifiedLevelListByAreaCode(QuerySpecifiedLevelAreaDataVo vo) {

        List<SysAreaEntity> res = Lists.newArrayList();
        String id = vo.getId();
        if (StringUtils.isBlank(id) || id.length() % 2 != 0) {
            return res;
        }

        LambdaQueryWrapper<SysAreaEntity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(SysAreaEntity::getEnable, SysConstants.YES);
        lambdaQueryWrapper.orderByAsc(SysAreaEntity::getId);
        // 区
        if (SF_COUNTY_AREA_CODE_LENGTH == id.length()) {
            lambdaQueryWrapper.eq(SysAreaEntity::getId, id);
            res = baseMapper.selectList(lambdaQueryWrapper);
        }// 省 /市
        else {
            if (vo.getAreaLevel() == null) {
                return res;
            }
            switch (vo.getAreaLevel()) {
                case PROVINCE:
                    // 顶级部门
                    if (SF_TOP_AREA_CODE.equals(id)) {
                        lambdaQueryWrapper.eq(SysAreaEntity::getParentid, id);
                        res = baseMapper.selectList(lambdaQueryWrapper);
                    }
                    break;
                case CITY:
                    // 顶级部门
                    if (SF_TOP_AREA_CODE.equals(id)) {
                        lambdaQueryWrapper.eq(SysAreaEntity::getParentid, id);
                        res = baseMapper.selectList(lambdaQueryWrapper);
                        List<String> cityIds = res.stream().filter(item -> !SF_TOP_AREA_CODE.equals(item.getId())).map(SysAreaEntity::getId).collect(Collectors.toList());
                        if (!cityIds.isEmpty()) {
                            LambdaQueryWrapper<SysAreaEntity> city = Wrappers.lambdaQuery();
                            city.in(SysAreaEntity::getParentid, cityIds);
                            res.addAll(baseMapper.selectList(city));
                        }
                    }
                    // 省
                    else if (SF_PROVINCE_AREA_CODE_LENGTH == id.length()) {
                        lambdaQueryWrapper.eq(SysAreaEntity::getParentid, id);
                        lambdaQueryWrapper.or(vo.isContainSelf(), wrapper -> wrapper.eq(SysAreaEntity::getId, id));
                        res = baseMapper.selectList(lambdaQueryWrapper);
                    }
                    break;
                case COUNTY:
                    // 不为顶级部门
                    if (!SF_TOP_AREA_CODE.equals(id)) {
                        lambdaQueryWrapper.likeRight(SysAreaEntity::getId, id);
                        lambdaQueryWrapper.ne(!vo.isContainSelf(), SysAreaEntity::getId, id);
                    }
                    res = baseMapper.selectList(lambdaQueryWrapper);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return res;
    }

    /**
     * 功能描述：获取完整行政区划名称<br>
     *
     * @param code
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2022/2/18 9:53
     */
    @Override
    public String getFullAreaNameByAreaCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        StringBuilder fullAreaName = new StringBuilder();
        String parentId = null;
        while (!SF_TOP_AREA_CODE.equals(parentId)) {
            SysAreaEntity area = getSysAreaEntity(code);
            if (area == null) {
                return fullAreaName.toString();
            }
            // 赋值父ID   并使用父ID继续查上级 维护fullAreaName;
            parentId = area.getParentid();
            code = area.getParentid();
            fullAreaName = new StringBuilder(StringUtils.isNotBlank(area.getName()) ? area.getName() : "").append(fullAreaName);
        }
        return fullAreaName.toString();
    }
}
