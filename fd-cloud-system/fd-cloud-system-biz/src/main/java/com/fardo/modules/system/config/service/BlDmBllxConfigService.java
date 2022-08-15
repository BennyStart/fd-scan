package com.fardo.modules.system.config.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.modules.znbl.bl.entity.BlDmBllxConfigEntity;
import com.fardo.modules.znbl.bl.vo.BlBllxConfigObjectVo;
import com.fardo.modules.znbl.bl.vo.BlDetailVo;
import com.fardo.modules.znbl.bl.vo.BlDmBllxConfigObjectVo;
import com.fardo.modules.znbl.ywxt.model.BlBldlxModel;
import com.fardo.modules.znbl.ywxt.model.BlBllxModel;

import java.util.List;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/7/14-15:21
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
public interface BlDmBllxConfigService extends IService<BlDmBllxConfigEntity> {

    Integer save(List<BlDmBllxConfigObjectVo> list);

    Integer saveOrUpdate(List<BlBllxConfigObjectVo> list);

    List<BlBldlxModel> getBldlxTree();

    List<BlBllxModel> getBlBllxEntity(String dlx);

    List<BlBllxModel> getBlBllxBackUpEntity(String dlx);
}
