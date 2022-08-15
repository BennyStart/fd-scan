package com.fardo.modules.system.area.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.system.area.entity.SysAreaEntity;
import com.fardo.modules.system.area.vo.QuerySpecifiedLevelAreaDataVo;

import java.util.List;

public interface ISysAreaService extends IService<SysAreaEntity> {

    List<SysAreaEntity> findByParentAreaCode(String parentAreaId);

    SysAreaEntity getSysAreaEntity(String id);

    /**
     * 功能描述：通过区域编号查询指定等级区域<br>
     *
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2021/12/27 15:51
     **/
    List<SysAreaEntity> getSpecifiedLevelListByAreaCode(QuerySpecifiedLevelAreaDataVo vo);

    /**
     * 功能描述：获取完整行政区划名称<br>
     *
     * @return:
     * @since: 1.0.0
     * @Author: luyf
     * @Date: 2022/2/18 9:53
     **/
    String getFullAreaNameByAreaCode(String code);
}
