package com.fardo.modules.system.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fardo.common.aspect.annotation.RedisLock;
import com.fardo.common.util.PinyinUtil;
import com.fardo.common.util.StringUtil;
import com.fardo.modules.system.sys.vo.DmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @(#)DmPyInitUtil <br>
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号:
 * 修订记录:
 * 更改者：suzc
 * 时　间：2021/5/26 17:27
 * 描　述：
 */
@Slf4j
@Component
public class DmPyInitUtil {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${mybatis-plus.configuration.database-id}")
    private String database;

    private static HashMap<String,String> TABLES = new HashMap<>();

    static {
        TABLES.put("T_BL_DM_BZ_XZQH","select id,mc from T_BL_DM_BZ_XZQH where py is null");
        TABLES.put("T_BL_DM_XWFS","select id,mc from T_BL_DM_XWFS where py is null");
        TABLES.put("T_BL_DM_BLGS","select bh as id,mc from T_BL_DM_BLGS where py is null");
        TABLES.put("T_BL_DM_BZ_CYZJ","select bh as id,mc from T_BL_DM_BZ_CYZJ where py is null");
        TABLES.put("T_BL_DM_BZ_GJ","select bh as id,mc from T_BL_DM_BZ_GJ where py is null");
        TABLES.put("T_BL_DM_BZ_MZ","select bh as id,mc from T_BL_DM_BZ_MZ where py is null");
        TABLES.put("T_BL_DM_BZ_WHCD","select bh as id,mc from T_BL_DM_BZ_WHCD where py is null");
        TABLES.put("T_BL_DM_BZ_XB","select bh as id,mc from T_BL_DM_BZ_XB where py is null");
        TABLES.put("T_BL_DM_BZ_ZZMM","select bh as id,mc from T_BL_DM_BZ_ZZMM where py is null");
        TABLES.put("T_BL_DM_DAFS","select bh as id,mc from T_BL_DM_DAFS where py is null");
        TABLES.put("T_BL_DM_FLYZDX","select bh as id,mc from T_BL_DM_FLYZDX where py is null");
        TABLES.put("T_BL_DM_RYGX","select bh as id,mc from T_BL_DM_RYGX where py is null");
        TABLES.put("T_BL_DM_RYLX","select bh as id,mc from T_BL_DM_RYLX where py is null");
        TABLES.put("T_SYS_AREA","select id,name as mc from T_SYS_AREA where py is null");
        TABLES.put("T_BL_DM_TZJXFS","select bh as id,mc from T_BL_DM_TZJXFS where py is null");
        TABLES.put("T_BL_DM_BZ_ZYLB","select bh as id,mc from T_BL_DM_BZ_ZYLB where py is null");
        TABLES.put("T_BL_DM_AY","select id,mc from T_BL_DM_AY where py is null");
    }

    @RedisLock
    @Transactional
    public void initPy() {
        log.info("开始初始化字典拼音数据");
        long t = System.currentTimeMillis();
        for(Map.Entry<String,String> entry : TABLES.entrySet()) {
            //判断是否有PY（拼音字段）
            String tableName = entry.getKey();
            try {
                String sql = "select PY from " + tableName;
                jdbcTemplate.queryForList(sql);
            } catch (DataAccessException e) {
                log.warn("没有找到py字段");
                if(DbType.MYSQL.getDb().equalsIgnoreCase(database)){
                    String sql = "alter table "+tableName+" add py varchar(100) COMMENT '名称拼音首字母'";
                    log.info(sql+";");
                    jdbcTemplate.update(sql);
                }else{
                    //添加py字段
                    String sql = "alter table "+tableName+" add py varchar2(100)";
                    log.info(sql+";");
                    jdbcTemplate.update(sql);
                    sql = "comment on column "+tableName+".py is '名称拼音首字母'";
                    log.info(sql+";");
                    jdbcTemplate.update(sql);
                }

            }
            //查找PY字段为空的数据进行字段赋值更新
            String selectSql  = entry.getValue();
            List<DmVo> list = jdbcTemplate.query(selectSql,new BeanPropertyRowMapper<>(DmVo.class));
            if(CollectionUtils.isNotEmpty(list)) {
                String updateSql = "update "+ tableName+" set py = ? where ";
                if(selectSql.contains("bh")) {
                    updateSql += "bh = ?";
                }else{
                    updateSql += "id = ?";
                }
                List<Object[]> orgs = new ArrayList<>(list.size());
                for(DmVo vo : list) {
                    if(StringUtil.isNotEmpty(vo.getMc())) {
                        String py = PinyinUtil.getPinYinHeadChar(vo.getMc());
                        Object[] org = new Object[2];
                        org[0] = py;
                        org[1] = vo.getId();
                        orgs.add(org);
                    }
                }
                jdbcTemplate.batchUpdate(updateSql,orgs);
            }
        }
        log.info("结束初始化字典拼音数据，耗时：{}ms", System.currentTimeMillis()-t);
    }
}
