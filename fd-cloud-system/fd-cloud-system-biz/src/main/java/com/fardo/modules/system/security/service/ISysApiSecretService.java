/**
 * @(#)SysParameterService.java 版权声明  巨龙软件工程有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：李木泉
 * 时　间：2016-11-15
 * 描　述：创建
 */

package com.fardo.modules.system.security.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fardo.common.system.vo.PageVo;
import com.fardo.modules.system.security.entity.SysApiSecretEntity;
import com.fardo.modules.system.security.model.ApiSecretPageModel;
import com.fardo.modules.system.security.vo.ApiSecretIdsVo;
import com.fardo.modules.system.security.vo.ApiSecretSaveVo;

public interface ISysApiSecretService extends IService<SysApiSecretEntity> {

    IPage<ApiSecretPageModel> queryPage(PageVo pageVo);

    void save(ApiSecretSaveVo vo);

    void delete(ApiSecretIdsVo vo);

    void initApiSecretMap();

}
