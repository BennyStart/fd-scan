package com.fardo.modules.system.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.faduit.security.cipher.SM4Utils;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.common.constant.RequestConstant;
import com.fardo.common.enums.OperTypeEnum;
import com.fardo.common.exception.ApiException;
import com.fardo.common.util.*;
import com.fardo.common.util.security.SignUtils;
import com.fardo.common.util.security.des.DESCoderHelper;
import com.fardo.modules.api.SysUserLogRomoteApi;
import com.fardo.modules.shiro.authc.LoginUtil;
import com.fardo.modules.system.constant.CacheKeyConstants;
import com.fardo.modules.system.constant.SysConstants;
import com.fardo.modules.system.log.entity.SysUserLogEntity;
import com.fardo.modules.system.log.model.AnShenLogInfo;
import com.fardo.modules.system.log.vo.UserInfoLogVo;
import com.fardo.modules.system.sys.service.ApiSessionService;
import com.fardo.modules.system.sys.vo.LoginUserVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


/**
 * 请求controller切面处理
 *
 * @Author wangbt
 * @Date 2020年12月18日
 */
@Slf4j
@Order(value = 1)
@Aspect
@Component
public class RequestAspect {

    /**
     * 不需要登录可调用的接口列表
     */
    private static final List<String> IGNORE_LOGIN_API_SYSTEM_LIST = Arrays.asList("echo", "encrypt", "loginPwd", "exchangeKey", "loadRSAPublicKey", "upgradeQuery", "synConf", "loginSSO", "getLoginSsoInfo", "logo/query");
    private static final List<String> IGNORE_LOGIN_API_ZNBL_LIST = Arrays.asList();
    private static final String API_URL_SYSTEM_PREFIX = "/api/system/";
    private static final String API_URL_ZNBL_PREFIX = "/api/znbl/";
    /**
     * 返回结果压缩
     */
    private static final String IS_RESULT_COMPRESS = "1";

    @Autowired
    private Validator validator;

    @Autowired
    private ApiSessionService apiSessionService;

    @Autowired
    private SysUserLogRomoteApi userLogRomoteApi;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${client.check.sign}")
    private boolean checkSign;

    @Pointcut("@annotation(com.fardo.common.aspect.annotation.RequestAop)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Object[] args = point.getArgs();
        ParamVo paramVo = new ParamVo();
        Object result;
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        String api = request.getServletPath();
        RequestAop requestAop = method.getAnnotation(RequestAop.class);
        try {
            for (Object arg : args) {
                if (arg instanceof ParamVo) {
                    paramVo = (ParamVo) arg;
                    // 校验数字签名
                    this.checkSign(paramVo, request);
                    // 接口参数赋值
                    this.setApiParam(paramVo, request);
                    // 检测登录状态
                    String loginUserVo = this.checkLogin(paramVo, api);
                    // 解析业务参数
                    this.parseParam(paramVo, requestAop.clazz());
                    // 业务数据校验
                    this.checkParam(paramVo);
                    // 请求参数打印
                    log.info("【REQ:{},{}】{}", request.getServletPath(), paramVo.getClientVersion(), JSON.toJSONString(paramVo));
                    paramVo.setLoginUserVo(loginUserVo);
                }
            }
            //执行方法
            result = point.proceed();
        } catch (ApiException e) {
            result = e.getResultVo();
        }
        if (result instanceof ResultVo) {
            //执行时长(毫秒)
            long elapsedTime = System.currentTimeMillis() - beginTime;
            ResultVo resultVo = (ResultVo) result;
            // 记录日志
            if (log.isDebugEnabled()) {
                log.debug("【RES:{},耗时={}ms,{}-{}】", method.getName(), elapsedTime, paramVo.getSid(), JSON.toJSONString(resultVo));
            } else {
                log.info("【RES:{},耗时={}ms,{}-{}】", method.getName(), elapsedTime, paramVo.getSid(), resultVo.getResultMsg());
            }
            if (this.isSaveLog(resultVo, requestAop)) {
                // 保存日志
                this.saveLog(paramVo, resultVo, requestAop, api, elapsedTime);
            }
            return this.resultHandle(paramVo, resultVo);
        }
        return result;
    }

    /**
     * 返回值处理
     *
     * @param paramVo
     * @param resultVo
     * @return
     */
    private ResultVo resultHandle(ParamVo paramVo, ResultVo resultVo) {
        String retStr = JSONObject.toJSONString(resultVo, SerializerFeature.WriteMapNullValue);
        // 是否只返回data数据
        boolean isData = false;
        // 请求成功才能加密返回，
        if (ResultCode.SUCCESS.getResultCode().equals(resultVo.getResultCode())) {
            //是否需要加密返回
            if (RequestConstant.EncryptReturn.equals(paramVo.getEncrypt()) || RequestConstant.EncryptAll.equals(paramVo.getEncrypt())) {
                retStr = this.returnEncrypt(retStr, paramVo.getSid(), paramVo.getEncryptType());
                isData = true;
            }
        }
        //判断是否需要压缩返回
        if (IS_RESULT_COMPRESS.equalsIgnoreCase(paramVo.getCompress())) {
            retStr = this.resultCompress(retStr);
            isData = true;
        }
        if (isData) {
            return new ResultVo(retStr);
        }
        return resultVo;
    }


    /**
     * 记录日志
     *
     * @param paramVo
     * @param resultVo
     * @param requestAop
     * @param api
     * @param elapsedTime
     */
    private void saveLog(ParamVo paramVo, ResultVo resultVo, RequestAop requestAop, String api, long elapsedTime) {
        SysUserLogEntity logEntity = new SysUserLogEntity();
        logEntity.setAppKey(paramVo.getApp_key());
        logEntity.setCostTime(elapsedTime);
        LoginUserVo userVo = LoginUtil.getLoginUser();
        if (userVo != null) {
            logEntity.setDepartId(userVo.getDepartVo().getId());
            UserInfoLogVo userInfo = new UserInfoLogVo(userVo);
            logEntity.setUserInfo(JSON.toJSONString(userInfo));
        }
        OperTypeEnum operTypeEnum = requestAop.operType();
        if (ResultCode.SUCCESS.getResultCode().equals(resultVo.getResultCode())) {
            logEntity.setOperResult("0");
        } else {
            logEntity.setOperResult("1");
            logEntity.setErrorCode(resultVo.getResultCode());
            logEntity.setErrorMsg(resultVo.getResultMsg());
        }
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        if (request == null) {
            return;
        }
        if (request.getAttribute(SysConstants.LOG_OPER_TYPE) != null) {
            operTypeEnum = (OperTypeEnum) request.getAttribute(SysConstants.LOG_OPER_TYPE);
        }
        String operDesc = request.getAttribute("operDesc") == null ? "" : request.getAttribute("operDesc").toString();
        logEntity.setOperDesc(operTypeEnum.getOperName() + operDesc);
        logEntity.setData1(request.getAttribute("resultData") == null ? "" : request.getAttribute("resultData").toString());
        logEntity.setOperName(operTypeEnum.getOperName());
        logEntity.setOperType(operTypeEnum.getOperType());
        logEntity.setOperModule(operTypeEnum.getOperModule());
        AnShenLogInfo anShenLogInfo = new AnShenLogInfo(operTypeEnum.getOperateType(), operTypeEnum.getResourceType(), operTypeEnum.getResourceName());
        logEntity.setData2(JSON.toJSONString(anShenLogInfo));
        logEntity.setRequestIp(paramVo.getRemoteIp());
        logEntity.setRequestUrl(api);
        if (StringUtil.isNotBlank(requestAop.removeParamField())) {
            JSONObject jsonParams = JSONObject.parseObject(paramVo.getParams());
            for (String field : requestAop.removeParamField().split(",")) {
                jsonParams.remove(field);
            }
            logEntity.setRequestParam(jsonParams.toJSONString());
        } else {
            logEntity.setRequestParam(paramVo.getParams());
        }
        logEntity.setVersion(paramVo.getClientVersion());
        this.userLogRomoteApi.save(logEntity);
    }


    /**
     * 判断是否保存日志
     *
     * @param resultVo
     * @param requestAop
     * @return
     */
    private boolean isSaveLog(ResultVo resultVo, RequestAop requestAop) {
        //登录超时就不记录日志了
        if (ResultCode.LOGIN_TIME_OUT.getResultCode().equals(resultVo.getResultCode())) {
            return false;
        }
        if (StringUtil.isBlank(requestAop.operType().getOperType())) {
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            if (request == null || request.getAttribute(SysConstants.LOG_OPER_TYPE) == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回加密
     *
     * @param retStr
     * @param sid
     * @param encryptType
     * @return
     */
    private String returnEncrypt(String retStr, String sid, String encryptType) {
        try {
            String randomKey = apiSessionService.getSessionValue(sid, ApiSessionService.FieldOfRandomKey);
            if (randomKey == null || "".equals(randomKey.trim())) {
            } else {
                //国产加密方式
                if (RequestConstant.DOMESTIC_ENCRYPT_TYPE.equals(encryptType)) {
                    SM4Utils sm4 = new SM4Utils();
                    sm4.setSecretKey(randomKey);
                    sm4.setHexString(false);
                    retStr = sm4.encryptData_ECB(retStr);
                } else {
                    retStr = DESCoderHelper.getInstance().encryptNetString(retStr, randomKey);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("返回结果加密编码失败", e);
        }
        return retStr;
    }

    /**
     * 返回结果压缩
     *
     * @return
     */
    private String resultCompress(String retStr) {

        try {
            int beLength = retStr.length();
            retStr = GZIPUtils.compress(retStr);
            int afLength = retStr.length();
            log.info("压缩前字节数:{},压缩后字节数：{},压缩比:{}", beLength, afLength, beLength == 0 ? 0 : afLength * 1.0 / beLength);
        } catch (IOException e) {
            log.error("压缩报文出错", e);
        }
        return retStr;
    }

    /**
     * 检测登录状态
     *
     * @param paramVo
     * @param api
     * @return
     */
    private String checkLogin(ParamVo paramVo, String api) {
        String sid = paramVo.getSid();
        if (StringUtils.isNotEmpty(sid)) {
            //判断是否被踢
            boolean isKicked = apiSessionService.isKickedOutSid(sid);
            if (isKicked && !"logout".equalsIgnoreCase(api)) {
                // 被踢，注销接口不返回被踢提示
                throw new ApiException(ResultCode.KICK_ERROR);
            }
            // 激活sid对应会话
            apiSessionService.activeSession(sid, paramVo.getRemoteIp());
        }
        if (isInIgnoreLoginApi(api)) {
            return "";
        }
        if (StringUtils.isEmpty(sid)) {
            throw new ApiException(ResultCode.SID_ERROR);
        }
        String loginUserVo = apiSessionService.getLoginUser(sid);
        if (StringUtils.isEmpty(loginUserVo)) {
            throw new ApiException(ResultCode.LOGIN_TIME_OUT);
        }
        return loginUserVo;
    }

    /**
     * 数据校验
     *
     * @param paramVo
     */
    private void checkParam(ParamVo paramVo) {
        if (paramVo.getData() == null || StringUtils.isBlank(paramVo.getParams())) {
            return;
        }
        if (paramVo.getData() instanceof String) {
            return;
        }
        Set<ConstraintViolation<Object>> violationSet = validator.validate(paramVo.getData(), Default.class);
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<Object> model : violationSet) {
            sb.append(model.getMessage()).append(";");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            throw new ApiException(ResultCode.INVALIDPARAMETER.getResultCode(), sb.toString());
        }
        // 校验对象里的集合
        this.checkParamList(paramVo.getData());

    }

    /***
     * 校验对象里的集合
     * @param data
     */
    private void checkParamList(Object data) {
        Field[] fields = data.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Object object = ReflectHelper.getFieldValueByName(fields[i].getName(), data);
            if (object instanceof List) {
                List list = (List) object;
                list.forEach(o -> {
                    Set<ConstraintViolation<Object>> violationSet = validator.validate(o, Default.class);
                    StringBuilder sb = new StringBuilder();
                    for (ConstraintViolation<Object> model : violationSet) {
                        sb.append(model.getMessage()).append(";");
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                        throw new ApiException(ResultCode.INVALIDPARAMETER.getResultCode(), sb.toString());
                    }
                    this.checkParamList(o);
                });
            } else if (object instanceof Object) {
                Set<ConstraintViolation<Object>> violationSet = validator.validate(object, Default.class);
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<Object> model : violationSet) {
                    sb.append(model.getMessage()).append(";");
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    throw new ApiException(ResultCode.INVALIDPARAMETER.getResultCode(), sb.toString());
                }
            }
        }
    }


    /**
     * 参数解析
     *
     * @param paramVo
     */
    private void parseParam(ParamVo paramVo, Class clazz) throws Exception {
        String params = paramVo.getParams();
        if (StringUtils.isBlank(params)) {
            return;
        }
        String randomKey = apiSessionService.getSessionValue(paramVo.getSid(), ApiSessionService.FieldOfRandomKey);
        String encrypt = paramVo.getEncrypt();
        if (encrypt == null || "".equals(encrypt.trim())) {
            encrypt = RequestConstant.EncryptNo;
        }
        if (RequestConstant.EncryptRequest.equals(encrypt) || RequestConstant.EncryptAll.equals(encrypt)) {
            // 请求参数有加密码
            if (org.apache.commons.lang3.StringUtils.isBlank(randomKey)) {
                throw new ApiException(ResultCode.LOGIN_TIME_OUT);
            }
            if (RequestConstant.DOMESTIC_ENCRYPT_TYPE.equals(paramVo.getEncryptType())) {
                SM4Utils sm4 = new SM4Utils();
                sm4.setSecretKey(randomKey);
                sm4.setHexString(false);
                params = sm4.decryptData_ECB(params);
            } else {
                params = DESCoderHelper.getInstance().decryptNetString(params, randomKey);
            }
            paramVo.setParams(params);
        }
        paramVo.setData(JSONObject.parseObject(params, clazz));
    }

    /**
     * 校验数字签名
     *
     * @更新时间:2020年12月16日
     * @更新作者:wangbt
     */
    private void checkSign(ParamVo paramVo, HttpServletRequest request) throws IOException {
        if (!checkSign) {
            return;
        }
        Enumeration<String> keys = request.getParameterNames();
        Map<String, String> params = new HashMap<>();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            params.put(key, request.getParameter(key));
        }

        String app_key = paramVo.getApp_key();
        Map<Object, Object> apiSecretMap = redisUtil.hmget(CacheKeyConstants.API_SECRET_PREFIX);
        if (!apiSecretMap.containsKey(app_key)) {
            // app_key不存在
            log.error("app_key=" + app_key + " invalid!!!");
            throw new ApiException(ResultCode.IN_VALID_SIGN);
        }
        String secret = apiSecretMap.get(app_key).toString();
        String md5 = SignUtils.signRequest(params, secret);
        log.debug("验证签名-服务端签名值：" + md5);
        if (!md5.equalsIgnoreCase(paramVo.getSign())) {
            // 签名不相同,阻止执行业务
            // 抛出业务异常
            throw new ApiException(ResultCode.IN_VALID_SIGN);
        }
    }

    /**
     * 接口参数赋值
     *
     * @param paramVo
     */
    private void setApiParam(ParamVo paramVo, HttpServletRequest request) {
        paramVo.setRemoteIp(IPUtils.getIpAddr(request));
        paramVo.setRemotePort(String.valueOf(request.getRemotePort()));
        paramVo.setServerName(request.getServerName());
        paramVo.setServerPort(String.valueOf(request.getServerPort()));
    }

    /**
     * 是否忽略登录
     *
     * @param api
     * @return
     */
    private boolean isInIgnoreLoginApi(String api) {
        for (String ignoreApi : IGNORE_LOGIN_API_SYSTEM_LIST) {
            String checkApi = API_URL_SYSTEM_PREFIX + ignoreApi;
            if (checkApi.equalsIgnoreCase(api)) {
                return true;
            }
        }
        for (String ignoreApi : IGNORE_LOGIN_API_ZNBL_LIST) {
            String checkApi = API_URL_ZNBL_PREFIX + ignoreApi;
            if (checkApi.equalsIgnoreCase(api)) {
                return true;
            }
        }
        return false;
    }


}
