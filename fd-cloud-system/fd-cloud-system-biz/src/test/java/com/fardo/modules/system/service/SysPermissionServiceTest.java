package com.fardo.modules.system.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fardo.common.constant.CommonConstant;
import com.fardo.common.util.StringUtil;
import com.fardo.common.util.UUIDGenerator;
import com.fardo.modules.system.permission.entity.SysPermissionEntity;
import com.fardo.modules.system.permission.enums.PermissionEnum;
import com.fardo.modules.system.permission.enums.PermissionTypeEnum;
import com.fardo.modules.system.permission.mapper.SysPermissionMapper;
import com.fardo.modules.system.permission.service.ISysPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.jeecgframework.poi.excel.ExcelExportUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @(#)SysPermissionServiceTest <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/30 10:36
 * 描　述：
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class SysPermissionServiceTest {
    @Autowired
    private ISysPermissionService sysPermissionService;
    @Resource
    private SysPermissionMapper permissionMapper;

    private static HashMap<String,String> MAP = new HashMap<>();

    @Test
    public void testInit() {
        File file = new File("D:\\requirement\\04法度笔录4.0\\材料\\菜单功能.xlsx");
        ImportParams params = new ImportParams();
        params.setSheetNum(1);
        List<SysPermissionEntity> list = ExcelImportUtil.importExcel(file,SysPermissionEntity.class, params);
        if(CollectionUtil.isNotEmpty(list)) {
            sysPermissionService.remove(null);
            for(SysPermissionEntity p : list) {
                p.setDelFlag(CommonConstant.DEL_FLAG_0);
                p.setId(UUIDGenerator.generate());
                if(!PermissionTypeEnum.BUTTON.getCode().equals(p.getMenuType().toString())) {
                    MAP.put(p.getName(),p.getId());
                }
                if(StringUtil.isNotEmpty(p.getParentId())) {
                    String parentId = MAP.get(p.getParentId());
                    if(StringUtil.isEmpty(parentId)) {
                        log.error("上级菜单不存在：{}", p.getParentId());
                        throw new RuntimeException("上级菜单不存在");
                    }
                    p.setParentId(parentId);
                }
            }
        }
        this.sysPermissionService.saveBatch(list);
    }

    @Test
    public void testInitCode() {
        List<SysPermissionEntity> list = sysPermissionService.list(null);
        HashMap<String,String> map = new HashMap<>();
        map.put("查询","01");
        map.put("新增","02");
        map.put("编辑","03");
        map.put("详情","04");
        map.put("删除","05");
        map.put("批量删除","06");
        map.put("高级查询","08");
        map.put("导入","10");
        map.put("导出","09");
        map.put("上传","11");
        map.put("审批","12");
        map.put("新增当前人笔录","13");
        map.put("新增其他人笔录","14");
        map.put("笔录浏览记录","15");
        map.put("笔录移交记录","19");
        map.put("自定义字段","16");
        map.put("移交","17");
        map.put("授权","19");
        map.put("激活","13");
        map.put("锁定","14");
        map.put("重置密码","13");
        map.put("设置权限","13");
        map.put("分配用户","14");
        map.put("收藏","20");
        map.put("分享","21");
        map.put("审核","22");
        map.put("批注","23");
        map.put("设置无效笔录","24");
        map.put("交叉分析","25");
        map.put("碰撞分析","26");

        if(CollectionUtil.isNotEmpty(list)) {
            List<SysPermissionEntity> templist = new ArrayList<>(list);
             for (SysPermissionEntity po : list) {
                if(2 == po.getMenuType()) {
                    if(StringUtil.isEmpty(po.getPerms())) {
                        String pcode = templist.stream().filter(p-> p.getId().equals(po.getParentId())).map(SysPermissionEntity::getPerms).collect(Collectors.joining());
                        if(StringUtil.isNotEmpty(pcode)) {
                            if(pcode.length() < 6) {
                                pcode +="00";
                            }
                            if(map.containsKey(po.getName())) {
                                pcode += map.get(po.getName());
                                SysPermissionEntity update = new SysPermissionEntity();
                                update.setPerms(pcode);
                                update.setId(po.getId());
                                permissionMapper.updateById(update);
                            }
                        }
                    }
                }
            }
        }
    }

}
