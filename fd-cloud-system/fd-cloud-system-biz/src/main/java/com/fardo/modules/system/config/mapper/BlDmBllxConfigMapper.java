package com.fardo.modules.system.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fardo.modules.znbl.bl.entity.BlDmBllxConfigEntity;
import com.fardo.modules.znbl.bl.vo.BlDetailVo;
import com.fardo.modules.znbl.bl.vo.BllxChilderVo;
import com.fardo.modules.znbl.ywxt.entity.BlDmBldlxEntity;
import com.fardo.modules.znbl.ywxt.entity.BlDmBllxEntity;
import com.fardo.modules.znbl.ywxt.model.BlBllxChildrenModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * TODO
 *
 * @author guohh
 * @date 2021/7/14-15:21
 * 版本号: v.1.1.0
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 */
public interface BlDmBllxConfigMapper extends BaseMapper<BlDmBllxConfigEntity> {

    void delete();

    Integer editBh(@Param("bh") String bh,@Param("typeBh") String typeBh,@Param("sfxs") String sfxs);

    List<BlDetailVo> getBlDetailVo();

    List<BllxChilderVo> getBlbh(@Param("bh") String bh);

    List<BlDmBldlxEntity> selectList();

    List<BlDmBllxEntity> getBlBllxEntityList(@Param("dlx") String dlx);

    List<BlDmBllxEntity> getBlBllxBackUpEntityList(@Param("dlx") String dlx);

    BlDmBllxEntity getBlDmBllxEntity(@Param("bh") String bh);

    Integer updateById(@Param("bh") String bh,@Param("sfxs") String sfxs);

    BlDmBldlxEntity getBlDmBldlxEntity(@Param("bh") String bh);

    Integer updateByBlDmBldlxId(@Param("bh") String bh,@Param("sfxs") String sfxs);

    List<BlBllxChildrenModel> getBlBllxChildren(@Param("bllxBh") String bllxBh);
}
