package com.fardo.config.mybatis;

import com.fardo.common.util.StringUtil;
import com.fardo.modules.api.SysBaseRemoteApi;
import com.fardo.modules.api.SysDepartRomoteApi;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.depart.entity.SysDepartEntity;
import com.fardo.modules.system.role.enums.DataScopeEnum;
import com.fardo.modules.system.role.model.SysCustomDepartModel;
import com.fardo.modules.system.role.model.SysDataPermissionModel;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * mybatisSQL拦截器，添加数据权限
 * @Author mwangbt
 * @Date  21-04-12
 *
 */
@Slf4j
@Component
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class MybatisSqlInterceptor implements Interceptor {

    @Autowired
    private SysDepartRomoteApi sysDepartRomoteApi;
    @Autowired
    private SysBaseRemoteApi sysBaseRemoteApi;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getTarget() instanceof Executor && invocation.getArgs().length==4) {
            String sql = getSqlByInvocation(invocation);
            if(sql.contains("@dataAuthDepart")){
                Map<String, Object> parameterObject = (Map<String, Object>) invocation.getArgs()[1];
                if(parameterObject.get("departField") != null) {
                    sql = addPremissionParam(sql, parameterObject.get("departField").toString());
                    resetSql2Invocation(invocation, sql);
                }
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 通用权限字段添加
     *
     * @param sql
     * @return
     */
    private String addPremissionParam(String sql, String departField) {
        // 获取数据权限范围
        SysDataPermissionModel dataPermissionModel = sysBaseRemoteApi.getUserDataScope();
        LoginUserVo userVo = LoginUtil.getLoginUser();
        StringBuilder repSql = new StringBuilder();
        repSql.append(" ");
        if(dataPermissionModel == null || userVo == null || userVo.getDepartVo() == null || StringUtil.isEmpty(departField)) {
            repSql.append("1=2 ");
        } else {
            String departId = userVo.getDepartVo().getId();
            if(DataScopeEnum.DEFAULT.getCode().equals(dataPermissionModel.getDataScope())) {
                String alias = ""; //获取别名
                if(departField.contains(".")) {
                    alias = departField.substring(0, departField.indexOf(".")+1);
                }
                repSql.append(alias).append("create_by = '").append(userVo.getId()).append("' ");
            } else if(DataScopeEnum.DEPART.getCode().equals(dataPermissionModel.getDataScope())) {
                repSql.append(departField).append("='").append(departId).append("' ");
            } else if(DataScopeEnum.DEPART_WITH_SUB.getCode().equals(dataPermissionModel.getDataScope())) {
                String path = userVo.getDepartVo().getPath();
                repSql.append("exists (select 1 from t_sys_depart d where d.path like '").append(path).append("%' and d.id = ").append(departField).append(")");
            } else if(DataScopeEnum.CUSTOM.getCode().equals(dataPermissionModel.getDataScope())) {
                List<SysCustomDepartModel> customDepartModels = dataPermissionModel.getCustomDepartModels();
                if(!CollectionUtils.isEmpty(customDepartModels)) {
                    repSql.append("exists (select 1 from t_sys_depart d where 1=1 and ( ");
                    for(int i=0; i<customDepartModels.size(); i++) {
                        if(i != 0) {
                            repSql.append(" or ");
                        }
                        SysCustomDepartModel customDepartModel = customDepartModels.get(i);
                        if(DataScopeEnum.DEPART.getCode().equals(customDepartModel.getDataScope())) {
                            repSql.append("d.id = '").append(customDepartModel.getDepartId()).append("' ");
                        } else if(DataScopeEnum.CUSTOM.getCode().equals(customDepartModel.getDataScope())) {
                            String path = this.getPath(customDepartModel.getDepartId());
                            repSql.append("d.path like '").append(path).append("%' ");
                        }
                    }
                    repSql.append(" ) and d.id = ").append(departField).append(" )");
                } else {
                    repSql.append("1=2 ");
                }
            } else {
                repSql.append("1=2 ");
            }
        }
        sql = sql.replace("@dataAuthDepart", repSql);
        return sql;
    }

    /**
     * 获取当前sql
     * @param invocation
     * @return
     */
    private String getSqlByInvocation(Invocation invocation) {
        final Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = ms.getBoundSql(parameterObject);
        return boundSql.getSql();
    }

    /**
     * 将sql重新设置到invocation中
     * @param invocation
     * @param sql
     * @throws SQLException
     */
    private void resetSql2Invocation(Invocation invocation, String sql) throws SQLException {
        final Object[] args = invocation.getArgs();
        MappedStatement statement = (MappedStatement) args[0];
        Object parameterObject = args[1];
        BoundSql boundSql = statement.getBoundSql(parameterObject);
        MappedStatement newStatement = newMappedStatement(statement, new BoundSqlSource(boundSql));
        MetaObject msObject =  MetaObject.forObject(newStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(),new DefaultReflectorFactory());
        msObject.setValue("sqlSource.boundSql.sql", sql);
        args[0] = newStatement;
    }

    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder =
                new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.timeout(ms.getTimeout());

        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }


    private String getPath(String departId) {
        SysDepartEntity sysDepartEntity = sysDepartRomoteApi.findDepart(departId);
        return sysDepartEntity.getPath();
    }


}
