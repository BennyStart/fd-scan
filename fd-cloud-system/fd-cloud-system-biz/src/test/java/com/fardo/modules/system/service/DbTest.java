package com.fardo.modules.system.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fardo.common.api.vo.Result;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.system.config.entity.SysClientConfigGlobalEntity;
import com.fardo.modules.system.config.mapper.SysClientConfigGlobalMapper;
import com.fardo.modules.system.user.model.SysUserModel;
import com.fardo.modules.system.user.service.ISysUserService;
import com.fardo.modules.system.user.vo.SysUserParamVo;
import com.fardo.modules.system.vo.TabColumnVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @(#)SysUserServiceTest <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/3/9 17:35
 * 描　述：
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class DbTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Resource
    SysClientConfigGlobalMapper sysClientConfigGlobalMapper;

    private static final String TABLE_COMMENTS_SQL = "select comments from user_tab_comments where table_name = ?";
    private static final String COL_COMMENTS_SQL = "SELECT A.COLUMN_NAME,A.DATA_TYPE,A.DATA_LENGTH,B.COMMENTS FROM (select * from user_tab_columns where table_name = ?)A LEFT JOIN (SELECT * FROM user_col_comments WHERE TABLE_NAME = ?)B ON A.COLUMN_NAME = B.COLUMN_NAME";

    @Test
    public void testSync(){
        List<SysClientConfigGlobalEntity> list = sysClientConfigGlobalMapper.selectList(null);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("|数据项key|数据项名称|数据项字段|\n");
        for(SysClientConfigGlobalEntity po : list) {
            List<Map<String, Object>> mapList = jdbcTemplate.queryForList(po.getConfigValue());
            if(CollectionUtils.isEmpty(mapList)) continue;
            Map<String, Object> map = mapList.get(0);
            String zd = map.keySet().stream().collect(Collectors.joining(","));
            stringBuffer.append("|").append(po.getConfigKey()).append("|").append(po.getConfigDesc()).append("|").append(zd).append("|\n");
        }
        System.out.println(stringBuffer.toString());
    }


    @Test
    public void testGetTableDetail(){
        Set<String> tables = new LinkedHashSet<>();
        tables.add("T_BL_AJ");
        tables.add("T_BL_BL");
        tables.add("T_BL_BLEX");
        tables.add("T_BL_RY");
        tables.add("T_BL_RYBLGL");
        tables.add("T_BL_INTERPRETER");
        tables.add("T_BL_PROT_MAN");
        tables.add("T_BL_ENTERPER_MAN");
//        tables.add("T_BL_KEYWORD_RECORD");
//        tables.add("T_BL_DM_BLDLX");
//        tables.add("T_BL_DM_BLLX");
//        tables.add("T_BL_DM_XWDXLX");
//        tables.add("T_BL_DM_LX_XWDXLX");
//        tables.add("T_BL_NOTICE");
//        tables.add("T_BL_NOTICE_ITEM");
//        tables.add("T_BL_TEMPLATE");
//        tables.add("T_BL_TEMPLATECONTEXT");
//        tables.add("T_BL_WENSHU_CONFIG");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("# 表结构\n");
        for(String tableName : tables) {
            System.out.println("正在生产"+tableName);
            String comments = jdbcTemplate.queryForObject(TABLE_COMMENTS_SQL, new Object[]{tableName},String.class);
            stringBuffer.append("## "+tableName+"("+comments+")\n");
            stringBuffer.append("|字段|类型|长度|注释|\n");
            stringBuffer.append("|------------|------------|------------|------------|\n");
            List<TabColumnVo> list = jdbcTemplate.query(COL_COMMENTS_SQL,new Object[]{tableName,tableName}, new BeanPropertyRowMapper<>(TabColumnVo.class));
            for (TabColumnVo vo : list) {
               stringBuffer.append("|").append(vo.getColumnName()).append("|").append(vo.getDataType()).append("|").append(vo.getDataLength()).append("|");
               if(StringUtil.isNotEmpty(vo.getComments())) {
                   stringBuffer.append(vo.getComments());
               }
               stringBuffer.append("|\n");
            }
        }
        writeToFile(stringBuffer.toString());
    }

    private void writeToFile(String data) {
        try {
            File file = new File("C://笔录表结构.md");
            FileUtils.writeStringToFile(file,data,"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
