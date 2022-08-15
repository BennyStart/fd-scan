package com.fardo.modules.system.personal.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fardo.common.api.vo.ResultCode;
import com.fardo.common.api.vo.ResultVo;
import com.fardo.common.aspect.annotation.RequestAop;
import com.fardo.modules.system.config.service.SysParameterService;
import com.fardo.modules.system.personal.dto.SysPersonalInspectDTO;
import com.fardo.modules.system.personal.util.HttpUtil;
import com.fardo.modules.system.personal.util.Md5Utils;
import com.fardo.modules.system.personal.vo.SysPersonalInspectVo;
import com.fardo.modules.system.sys.vo.ParamVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人员核查
 *
 * @author guohh
 * 2021-08-31
 */
@Slf4j
@Api(tags = "api-人员核查")
@RestController
@RequestMapping("/api/system/personalInspect")
public class ApiPersonalInspectController {

    @Autowired
    private SysParameterService sysParameterService;

    @RequestAop(value = "根据身份证获取人员信息", clazz = SysPersonalInspectDTO.class)
    @ApiOperation("根据身份证获取人员信息")
    @PostMapping(value = "/getCardInfo")
    public ResultVo<List<SysPersonalInspectVo>> getCardInfo(ParamVo<SysPersonalInspectDTO> paramVo) {
        ResultVo<List<SysPersonalInspectVo>> resultVo = ResultVo.getResultVo(ResultCode.SUCCESS);
        List<SysPersonalInspectVo> voList = PopBasicInfo(paramVo.getData().getSfzh(), paramVo.getData().getXm());
        resultVo.setResults(voList);
        return resultVo;
    }


    /**
     * 人员核查
     * <p>
     * 返回示例：{"code":"00","msg":"成功","data":"","datas":"[{\"ELC_LICENCE_NAME\":\"中华人民共和国居民身份证\",\"ELC_LICENCE_DEPT\":\"省公安厅\",\"ELC_LICENCE_CODE\":\"\",\"ELC_LICENCE_FILE\":{\"URL\":\"http://59.202.42.251/licensename/elclicencepdf/gat/sfz/ML0000129ac937bcd5c821d223.pdf\",\"SIGN_CERT\":\"04269148C9D90054D99DB7CEAC7D89DE5AF07453DED334DF945B7F0947070265ED558614B2FE1E17798A5888DF58E78248C2F1A2CA58618DA\",\"SIGN_VALUE\":\"on6MAhF4PcChxrvdLXMAziHsHKtkbdWKq3yvVB7hUWk3RrItSW5zF\\nvj2vftYiuA==\",\"TSA\":\"\",\"FILE_HASHCODE\":\"\"},\"ELC_LICENCE_STRUCT\":{\"SIGN_CERT\":\"04269148C9D90054D99DB2CD5ED34B09359DE5AF07453DED334DF945B7F0947070265ED558614B2FE1E17798A5888DF58E78248C2F1A2CA58618DA\",\"SIGN_VALUE\":\"5jy6M6r1dtKQ9osAuUP+9+Nj17OSkGxCNj1bAVYq+ahw53bj4TLVHn9JJBXIgmUYjc50fLfuw==\",\"TSA\":\"\",\"DATA\":{\"CZRKQFJG\":\"杭州市公安局西湖分局\",\"CZRKMZ\":\"汉族\",\"CZRKCSRQ\":\"19901022\",\"CZRKXB\":\"女\",\"CZRKGMSFHM\":\"330282199010226282\",\"CZRKXM\":\"周艳\",\"CZRKYXQXQSRQ\":\"20170804\",\"CZRKYXQXJZRQ\":\"20370804\",\"CZRKZZ\":\"杭州市西湖区文三路\"}}}]","requestId":"625008bb4e2f426a98b308a4e4350c68","dataCount":1,"totalDataCount":0,"totalPage":1}
     *
     * @param czrkgmsfhm
     * @param czrkxm
     * @return
     */
    public List<SysPersonalInspectVo> PopBasicInfo(String czrkgmsfhm, String czrkxm) {
        log.info("====进入第人口核查调用====");
        long time = System.currentTimeMillis();
        Map<String, String> params = new HashMap<>();
        //密钥a地址
        String aUrl = sysParameterService.getSysParam("tz_a_secret_url");
        if (StringUtils.isBlank(aUrl)) {
            log.info("tz_a_secret_url----密钥a地址为空！");
        }
        //密钥b地址
        String bUrl = sysParameterService.getSysParam("tz_b_secret_url");
        if (StringUtils.isBlank(bUrl)) {
            log.info("tz_b_secret_url----密钥b地址为空！");
        }
        //密钥
        String appSecret = sysParameterService.getSysParam("tz_app_secret");
        if (StringUtils.isBlank(appSecret)) {
            log.info("tz_app_secret----密钥为空！");
        }
        //key
        String appKey = sysParameterService.getSysParam("tz_app_key");
        if (StringUtils.isBlank(appKey)) {
            log.info("tz_app_key----key为空！");
        }
        //台州人口核查地址
        String url = sysParameterService.getSysParam("tz_person_inspect_url");
        if (StringUtils.isBlank(url)) {
            log.info("tz_person_inspect_url----台州人口核查地址为空！");
        }
        SysPersonalInspectVo vo = new SysPersonalInspectVo();
        //获取密钥b参数
        String s = PopBasicBInfo(appKey, appSecret, aUrl, bUrl, time);
        List<SysPersonalInspectVo> voList = new ArrayList<>();
        //判断参数是否不为空
        if (StringUtils.isNotBlank(s)) {
            String sign = Md5Utils.hash(appKey + s + time);
            params.put("appKey", appKey);
            params.put("sign", sign);
            params.put("requestTime", +time + "");
            if (StringUtils.isBlank(czrkxm)) {
                czrkxm = "";
            }
            params.put("czrkxm", czrkxm);
            params.put("czrkgmsfhm", czrkgmsfhm);
            log.info("人口核查url：{}，参数：{}", url, params);
            String result = HttpUtil.sendHttpPost(url, params);
            log.info("人口核查返回参数：{}", result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String code = jsonObject.getString("code");
            log.info("人口核查返回状态码参数：{}", code);
            //返回00成功
            if (code.equals("00")) {
                String datas = jsonObject.getString("datas");
                log.info("解析datas层的数据：{}", datas);
                JSONArray jsonArray = JSONArray.parseArray(datas);
                log.info("解析datas层的数据转jsonArray：{}", jsonArray);
                for (Object o : jsonArray) {
                    log.info("====数据处理====");
                    vo = new SysPersonalInspectVo();
                    JSONObject jsonObject1 = JSONObject.parseObject(String.valueOf(o));
                    String elc_licence_name = jsonObject1.getString("ELC_LICENCE_NAME");
                    String elc_licence_struct = jsonObject1.getString("ELC_LICENCE_STRUCT");
                    log.info("解析ELC_LICENCE_STRUCT层的数据：{}", elc_licence_struct);
                    JSONObject jsonObject2 = JSONObject.parseObject(elc_licence_struct);
                    log.info("解析ELC_LICENCE_STRUCT层的数据转JSONObject：{}", jsonObject2);
                    String data = jsonObject2.getString("DATA");
                    log.info("解析data层的数据：{}", data);
                    JSONObject jsonObject3 = JSONObject.parseObject(data);
                    log.info("解析data层的数据转JSONObject：{}", jsonObject3);

                    String czrkmz = jsonObject3.getString("CZRKMZ");
                    String czrkcsrq = jsonObject3.getString("CZRKCSRQ");
                    String czrkxb = jsonObject3.getString("CZRKXB");
                    String czrkgmsfhm1 = jsonObject3.getString("CZRKGMSFHM");
                    String czrkxm1 = jsonObject3.getString("CZRKXM");
                    String czrkzz = jsonObject3.getString("CZRKZZ");
                    vo.setCsrq(czrkcsrq);//出生日期
                    vo.setMz(czrkmz);//民族
                    vo.setSfzh(czrkgmsfhm1);//身份证号
                    vo.setXb(czrkxb);//性别
                    vo.setXm(czrkxm1);//姓名
                    vo.setZjlx(elc_licence_name);//证件类型
                    vo.setZz(czrkzz);//住址
                    voList.add(vo);
                }
            } else {
                log.info("人口核查返回失败状态码：{}，错误信息：{}", code, jsonObject.getString("msg"));
            }
        }
        return voList;
    }

    /**
     * 获取密钥b的refreshSecret
     *
     * @param appKey
     * @param appSecret
     * @param aUrl
     * @param bUrl
     * @param time
     * @return
     */
    private String PopBasicBInfo(String appKey, String appSecret, String aUrl, String bUrl, long time) {
        log.info("====进入密钥b调用====");
        //获取密钥a
        String s = PopBasicAInfo(appKey, appSecret, aUrl, time);
        //判断密钥a返回不为空执行获取密钥b
        if (StringUtils.isNotBlank(s)) {
            String bSign = Md5Utils.hash(appKey + s + time);
            log.info("密钥b：{}",bSign);
            Map<String, String> bParams = new HashMap<>();
            bParams.put("appKey", appKey);
            bParams.put("sign", bSign);
            bParams.put("requestTime", +time + "");
            log.info("密钥b调用使用密钥a的url：{}，参数：{}", bUrl, bParams);
            String bResult = HttpUtil.sendHttpPost(bUrl, bParams);
            log.info("密钥b调用返回参数：{}", bResult);
            if (StringUtils.isBlank(bResult)) {
                log.info("密钥b调用返回参数为空：{}", bResult);
                return null;
            } else {
                JSONObject jsonObject = JSONObject.parseObject(bResult);
                String code = jsonObject.getString("code");
                log.info("密钥b调用返回状态码参数：{}", code);
                String datas = jsonObject.getString("datas");
                JSONObject jsonObject1 = JSONObject.parseObject(datas);
                String requestSecret = jsonObject1.getString("requestSecret");
                log.info("密钥b调用返回requestSecret参数：{}", requestSecret);
                return requestSecret;
            }
        }
        log.info("====密钥a调用为空导致密钥b也为空====");
        return null;
    }


    /**
     * 获取密钥a的refreshSecret
     *
     * @param appKey
     * @param appSecret
     * @param aUrl
     * @return
     */
    public String PopBasicAInfo(String appKey, String appSecret, String aUrl, long time) {
        log.info("====进入密钥a调用====");
        Map<String, String> aParams = new HashMap<>();
        log.info("密钥a调用使用--密钥a地址：{}，密钥为：{}，key为：{}", aUrl, appSecret, appKey);
        String aSign = Md5Utils.hash(appKey + appSecret + time);
        aParams.put("appKey", appKey);
        aParams.put("sign", aSign);
        aParams.put("requestTime", +time + "");
        log.info("密钥a调用使用密钥a的url：{}，参数：{}", aUrl, aParams);
        String aResult = HttpUtil.sendHttpPost(aUrl, aParams);
        log.info("密钥a调用返回参数：{}", aResult);
        if (StringUtils.isBlank(aResult)) {
            log.info("密钥a调用返回参数为空：{}", aResult);
            return null;
        }else {
            JSONObject jsonObject = JSONObject.parseObject(aResult);
            String code = jsonObject.getString("code");
            log.info("密钥a调用返回状态码参数：{}", code);
            String datas = jsonObject.getString("datas");
            JSONObject jsonObject1 = JSONObject.parseObject(datas);
            String refreshSecret = jsonObject1.getString("refreshSecret");
            log.info("密钥a调用返回refreshSecret参数：{}", refreshSecret);
            return refreshSecret;
        }
    }

}
