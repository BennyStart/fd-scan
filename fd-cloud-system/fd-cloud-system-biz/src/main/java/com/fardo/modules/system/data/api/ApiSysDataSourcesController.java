package com.fardo.modules.system.data.api;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.system.vo.DynamicDataSourceModel;
import com.fardo.common.util.dynamic.db.DataSourceCachePool;
import com.fardo.common.util.dynamic.db.DynamicDBUtil;
import com.fardo.modules.system.data.entity.SysDataSourceEntity;
import com.fardo.modules.system.data.service.ISysDataSourceService;
import com.fardo.modules.system.data.vo.SysDataSourceVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 多数据源管理
 * @Author: maozf
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "api-多数据源管理")
@RestController
@RequestMapping("/api/system/sys/dataSource")
public class ApiSysDataSourcesController {

    @Autowired
    private ISysDataSourceService sysDataSourceService;

    private static final String SELECT_TEST_SQL = "select * from sys_dept_info_fd";

    private static final String SF_SQLSERVER = "SQLSERVER";

    @RequestAop(value = "分页列表查询", clazz = SysDataSourceVo.class)
    @ApiOperation("分页列表查询")
    @PostMapping(value = "/list")
    public ResultVo<IPage<SysDataSourceEntity>> list(ParamVo<SysDataSourceVo> paramVo) {
        ResultVo<IPage<SysDataSourceEntity>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        IPage<SysDataSourceEntity> page = sysDataSourceService.page(new Page<>(paramVo.getData().getPageNo(), paramVo.getData().getPageSize()));
        resultVo.setResults(page);
        return resultVo;
    }

    @RequestAop(value = "新增编辑", clazz = SysDataSourceEntity.class)
    @ApiOperation(value = "新增编辑", notes = "新增编辑")
    @PostMapping(value = "/add")
    public ResultVo<String> add(ParamVo<SysDataSourceEntity> paramVo) {
        ResultVo<String> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        SysDataSourceEntity data = paramVo.getData();
        boolean b = true;
        if (StringUtils.isNotBlank(data.getId())) {
            b = sysDataSourceService.saveOrUpdate(data);
        } else {
            b = sysDataSourceService.save(data);
        }
        if (b) {
            result.setResultMsg("操作成功");
        } else {
            result.setResultMsg("操作失败");
        }
        return result;
    }

    @RequestAop(value = "删除", clazz = SysDataSourceEntity.class)
    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping(value = "/delete")
    public ResultVo<String> delete(ParamVo<SysDataSourceEntity> paramVo) {
        ResultVo<String> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        SysDataSourceEntity data = paramVo.getData();
        String id = data.getId();
        if (StringUtils.isBlank(id)) {
            result.setResultMsg("id不能为空");
            return result;
        }
        SysDataSourceEntity sysDataSource = sysDataSourceService.getById(id);
        DataSourceCachePool.removeCache(sysDataSource.getCode());
        sysDataSourceService.removeById(id);
        result.setResultMsg("操作成功");
        return result;
    }

    @RequestAop(value = "查看详情", clazz = SysDataSourceEntity.class)
    @ApiOperation(value = "查看详情", notes = "查看详情")
    @PostMapping(value = "/get")
    public ResultVo<SysDataSourceEntity> get(ParamVo<SysDataSourceEntity> paramVo) {
        ResultVo<SysDataSourceEntity> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        SysDataSourceEntity data = paramVo.getData();
        String id = data.getId();
        if (StringUtils.isBlank(id)) {
            result.setResultMsg("id不能为空");
            return result;
        }
        SysDataSourceEntity sysDataSource = sysDataSourceService.getById(id);
        result.setResults(sysDataSource);
        result.setResultMsg("操作成功");
        return result;
    }

    @RequestAop(value = "连接测试", clazz = DynamicDataSourceModel.class)
    @ApiOperation(value = "连接测试", notes = "连接测试")
    @PostMapping(value = "/test")
    public ResultVo<String> test(ParamVo<DynamicDataSourceModel> paramVo) {
        ResultVo<String> result = new ResultVo<>();
        result.setResultCode(ResultCode.SUCCESS);
        DynamicDataSourceModel dbSource = paramVo.getData();
        if (StringUtils.isBlank(dbSource.getCode()) ||
                StringUtils.isBlank(dbSource.getDbType()) ||
                StringUtils.isBlank(dbSource.getDbDriver()) ||
                StringUtils.isBlank(dbSource.getDbUrl()) ||
                StringUtils.isBlank(dbSource.getDbName()) ||
                StringUtils.isBlank(dbSource.getDbUsername()) ||
                StringUtils.isBlank(dbSource.getDbPassword())
        ) {
            result.setResultMsg("参数不能为空");
            return result;
        }

        DruidDataSource dataSource = DynamicDBUtil.getJdbcDataSource(dbSource);
        if (dataSource != null && dataSource.isEnable()) {
            try {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                String testSql = "select 1 from dual";
                if(dbSource.getDbType().toUpperCase().contains(SF_SQLSERVER)){
                    testSql = "select 1";
                }
                jdbcTemplate.queryForList(testSql);
                result.setResultMsg("连接成功");
                log.info("--------getDbSourceBydbKey------------------创建DB数据库连接-------------------");
            } catch (Exception e) {
                e.printStackTrace();
                result.setResultMsg("连接失败");
                return result;
            }

        } else {
            result.setResultMsg("连接失败");
        }
        return result;
    }

}
