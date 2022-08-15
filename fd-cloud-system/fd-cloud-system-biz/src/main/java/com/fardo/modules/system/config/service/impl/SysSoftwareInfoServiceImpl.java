package com.fardo.modules.system.config.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fardo.common.util.DateUtils;
import com.fardo.common.util.StringUtil;
import com.fardo.common.util.UUIDGenerator;
import com.fardo.modules.system.config.entity.SysSoftwareDownloadEntity;
import com.fardo.modules.system.config.entity.SysSoftwareInfoEntity;
import com.fardo.modules.system.config.mapper.SysSoftwareDownloadMapper;
import com.fardo.modules.system.config.mapper.SysSoftwareInfoMapper;
import com.fardo.modules.system.config.model.SysSoftwareInfoModel;
import com.fardo.modules.system.config.service.ISysSoftwareDownloadService;
import com.fardo.modules.system.config.service.ISysSoftwareInfoService;
import com.fardo.modules.system.config.vo.SysSoftwareInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("sysSoftwareInfoService")
public class SysSoftwareInfoServiceImpl extends ServiceImpl<SysSoftwareInfoMapper, SysSoftwareInfoEntity> implements ISysSoftwareInfoService {

    @Autowired
    private SysSoftwareInfoMapper softeareInfoMapper;
    @Autowired
    private ISysSoftwareDownloadService softwareDownloadService;
    @Autowired
    private SysSoftwareDownloadMapper softwareDownloadMapper;

   /* @Override
    public IPage<SysSoftwareInfoModel> getPageList(SysSoftwareInfoVo sysSoftwareInfoVo) {
        IPage page = new Page(sysSoftwareInfoVo.getPageNo(), sysSoftwareInfoVo.getPageSize());
        List<SysSoftwareInfoModel>list = softeareInfoMapper.getPageList(page,sysSoftwareInfoVo);
        Integer total = softeareInfoMapper.getPageListTotal(sysSoftwareInfoVo);
        IPage<SysSoftwareInfoModel> result = new Page<>(page.getCurrent(), page.getSize(), total);
        result.setRecords(list);
        return result;
    }*/

    @Override
    @Transactional
    public void addSysSoftwareInfo(SysSoftwareInfoVo sysSoftwareInfoVo) {
        sysSoftwareInfoVo.setId(UUIDGenerator.generate());
        SysSoftwareInfoEntity sysSoftwareInfoEntity = new SysSoftwareInfoEntity();
        BeanUtils.copyProperties(sysSoftwareInfoVo, sysSoftwareInfoEntity);
        sysSoftwareInfoEntity.setReleaseDate(DateUtils.getCurrentTime().substring(0,8));
        saveSoftDown(sysSoftwareInfoVo);
        softeareInfoMapper.insert(sysSoftwareInfoEntity);
    }

    private void saveSoftDown(SysSoftwareInfoVo sysSoftwareInfoVo){
        if (CollectionUtils.isEmpty(sysSoftwareInfoVo.getSoftDownList())){
            throw new RuntimeException("softDownList不能为空");
        }
        ArrayList<SysSoftwareDownloadEntity> list = new ArrayList<>();
        sysSoftwareInfoVo.getSoftDownList().forEach(  e -> {
            e.setId(UUIDGenerator.generate());
            e.setSoftwareId(sysSoftwareInfoVo.getId());
            e.setIfUpgrade("1");
            list.add( e);
        });
        softwareDownloadMapper.deleteBySoftId(sysSoftwareInfoVo.getId());
        softwareDownloadService.saveBatch(list);


       // saveSoftDown(sysSoftwareInfoVo);
    }


    @Override
    @Transactional
    public void updateSysSoftwareInfo(SysSoftwareInfoVo sysSoftwareInfoVo) {
        SysSoftwareInfoEntity sysSoftwareInfoEntity = softeareInfoMapper.selectById(sysSoftwareInfoVo.getId());
        if(sysSoftwareInfoEntity ==null){
            throw new RuntimeException("未找到对应实体");
        }
        BeanUtils.copyProperties(sysSoftwareInfoVo, sysSoftwareInfoEntity);
        saveSoftDown(sysSoftwareInfoVo);
        softeareInfoMapper.updateById(sysSoftwareInfoEntity);

    }

    @Override
    @Transactional
    public void deleteSoftwareInfo(String id) {
        softeareInfoMapper.deleteById(id);
        softwareDownloadMapper.deleteBySoftId(id);
    }

    @Override
    public IPage<SysSoftwareInfoModel> getPageList(SysSoftwareInfoVo softeareInfoVo) {
        LambdaQueryWrapper<SysSoftwareInfoEntity> lambdaQueryWrapper = new LambdaQueryWrapper<SysSoftwareInfoEntity>();
        IPage<SysSoftwareInfoModel> modelIPage = new Page<>();
        IPage<SysSoftwareInfoEntity> page = new Page<>(softeareInfoVo.getPageNo(), softeareInfoVo.getPageSize());
        if(StringUtil.isNotBlank(softeareInfoVo.getSoftwareName())){
            lambdaQueryWrapper.like(SysSoftwareInfoEntity::getSoftwareName,softeareInfoVo.getSoftwareName());
        }
        IPage<SysSoftwareInfoEntity> pageList = softeareInfoMapper.selectPage(page, lambdaQueryWrapper);
        List<SysSoftwareInfoModel> list = new ArrayList<>(pageList.getRecords().size());
        pageList.getRecords().forEach(m->{
            SysSoftwareInfoModel model = new SysSoftwareInfoModel();
            BeanUtils.copyProperties(m,model);
            if(StringUtil.isNotBlank(m.getId())){
                LambdaQueryWrapper<SysSoftwareDownloadEntity> SoftDownLambdaQueryWrapper = new LambdaQueryWrapper<>();
                SoftDownLambdaQueryWrapper.eq(SysSoftwareDownloadEntity::getSoftwareId,m.getId());
                List<SysSoftwareDownloadEntity> softDownList = softwareDownloadMapper.selectList(SoftDownLambdaQueryWrapper);
                model.setSoftDownList(softDownList);
            }
            list.add(model);
        });
        modelIPage.setRecords(list);
        modelIPage.setSize(pageList.getSize());
        modelIPage.setTotal(pageList.getTotal());
        modelIPage.setPages(pageList.getPages());
        return modelIPage;
    }
}
