package com.fardo.modules.system.service;

import com.fardo.common.constant.CommonConstant;
import com.fardo.common.system.base.entity.BaseEntity;
import com.fardo.modules.system.config.entity.SysDictEntity;
import com.fardo.modules.system.config.entity.SysDictItemEntity;
import com.fardo.modules.system.config.service.ISysDictItemService;
import com.fardo.modules.system.config.service.ISysDictService;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @(#)SysDictServiceTest <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/24 9:06
 * 描　述：
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SysDictServiceTest {
    @Autowired
    ISysDictItemService itemService;
    @Autowired
    ISysDictService dictService;

    private static List<SysDictEntity> DICTS = new ArrayList<>();

    private static List<String> excluedDict = new ArrayList<>();

    static {
        excluedDict.add("ORG_TYPE");
        excluedDict.add("DATA_SCOPE");
        excluedDict.add("PROVINCE_AREA");
        excluedDict.add("PIC_TZ");
        excluedDict.add("PIC_TYPE");
        excluedDict.add("BL_LY");
        excluedDict.add("BL_SQZT");
        excluedDict.add("POLICE_TYPE");
        excluedDict.add("PRINT_FONT");
        excluedDict.add("PRINT_FONT_SIZE");
        excluedDict.add("PRINT_QR_LOCATION");
        excluedDict.add("PRINT_WSBS");
        excluedDict.add("PRINT_PAGENUM_LOCATION");
        excluedDict.add("PRINT_QZTS_LOCATION");
        excluedDict.add("PRINT");
        excluedDict.add("PRINT_NUM");
        excluedDict.add("PRINT_MJQMGS");
        excluedDict.add("PRINT_JWQM");
        excluedDict.add("PRINT_BWHRQM");
        excluedDict.add("PRINT_DAFS");
        excluedDict.add("PRINT_BWHRXM");
        excluedDict.add("BL_SPZT");
        excluedDict.add("BL_SPJG");
        excluedDict.add("PROT_ORG_SF");
        excluedDict.add("BL_SCZT");
        excluedDict.add("BL_WCZT");
    }

    @Test
    public void generateDict(){
        File file = new File("D:\\requirement\\04法度笔录4.0\\材料\\字典.xlsx");
        ImportParams params = new ImportParams();
        params.setSheetNum(1);
        List<SysDictEntity> dictList = ExcelImportUtil.importExcel(file,SysDictEntity.class, params);
        List<SysDictEntity> toSaveDictList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dictList)) {
           for(SysDictEntity dictEntity : dictList) {
               if(excluedDict.contains(dictEntity.getDictCode()))
                   continue;
               wrapSysDictEntity(dictEntity);
               toSaveDictList.add(dictEntity);
           }
           dictService.saveBatch(toSaveDictList);
        }
        file = new File("D:\\requirement\\04法度笔录4.0\\材料\\字典项.xlsx");
        List<SysDictItemEntity> itemList = ExcelImportUtil.importExcel(file,SysDictItemEntity.class, params);
        List<SysDictItemEntity> toSaveItemList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(itemList)) {
            for(SysDictItemEntity itemEntity : itemList) {
                if(excluedDict.contains(itemEntity.getDictId()))
                    continue;
                wrapSysDictItemEntity(itemEntity);
                toSaveItemList.add(itemEntity);
            }
            itemService.saveBatch(toSaveItemList);
        }
    }

    private void wrapSysDictEntity(SysDictEntity dictEntity){
        dictEntity.setDelFlag(CommonConstant.DEL_FLAG_0);
        dictEntity.setType(0);
    }

    private void wrapSysDictItemEntity(SysDictItemEntity itemEntity){
        if(DICTS.isEmpty()) {
            DICTS = dictService.list();
        }
        itemEntity.setDictId(DICTS.stream().filter(d -> d.getDictCode().equals(itemEntity.getDictId())).map(BaseEntity::getId).collect(Collectors.joining()));
        itemEntity.setStatus(1);
    }

}
