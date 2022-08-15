package com.fardo.modules.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fardo.common.exception.ApiException;
import com.fardo.common.exception.FdException;
import com.fardo.common.util.*;
import com.fardo.modules.system.config.service.SysParameterService;
import com.fardo.modules.system.constant.ConfigConstants;
import com.fardo.modules.system.sys.enums.SysResultCodeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@Data
@Slf4j
public class FileServiceUtils {

    private static String URL;
    private static String APP_KEY;
    private static String SECRET;
    private static String ENCRY_TYPE;


    private static void refresh() {
        SysParameterService sysParameterService = SpringContextUtils.getBean(SysParameterService.class);
        URL = sysParameterService.getSysParam(ConfigConstants.FILE_SERVICE_BASE_URL);//文件服务地址
        APP_KEY = sysParameterService.getSysParam(ConfigConstants.FILE_SERVICE_APPKEY);//文件服务地址
        SECRET = sysParameterService.getSysParam(ConfigConstants.FILE_SERVICE_SECRET);//文件服务地址
        ENCRY_TYPE = sysParameterService.getSysParam(ConfigConstants.FILE_SERVICE_ENCRY_TYPE);//文件服务地址
    }

    public static String saveFile(MultipartFile file) throws Exception{
        return saveFile(file, null);
    }

    /**
     * 上传文件到文件服务器
     * @param file  文件内容
     * @return
     * @throws Exception
     */
    public static String saveFile(MultipartFile file, String fileName) throws Exception {
        //刷新配置
        refresh();
        if(file==null){
            log.info("文件为空");
            return "";
        }
        String url = URL;
        String secret = SECRET;
        String appkey = APP_KEY;
        String encryType = ENCRY_TYPE;
        if (StringUtils.isBlank(url) || StringUtils.isBlank(secret) || StringUtils.isBlank(appkey) || StringUtils.isBlank(encryType)) {
            throw new FdException("获取文件失败");
        }
        if(StringUtil.isEmpty(fileName)) {
            fileName = file.getOriginalFilename();
        }
        if(!url.endsWith("/")){
            url = url + "/";
        }
        url = url +"minio/upload";
        long ts = System.currentTimeMillis()/1000L;
        log.debug("时间戳：{}", ts);
        String uri="/minio/upload";
        String version = "V1";

        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.put("appKey",appkey);
        sortedMap.put("fileName", fileName);
        sortedMap.put("uri", uri);
        sortedMap.put("version", version);
        sortedMap.put("ts", String.valueOf(ts));
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entry2 = sortedMap.entrySet();
        for (Map.Entry<String, String> entry : entry2) {
            sb.append(entry.getValue());
        }
        sb.append(secret);

        String token = HashAlgorithm.stringToHash(encryType, sb.toString());
        log.debug(token);
        Map<String,String> params = new HashMap<>();
        params.put("appKey",appkey);
        params.put("version",version);
        params.put("ts",ts+"");
        params.put("token",token);
        params.put("fileName",fileName);

        Map<String, MultipartFile> fileParams = new HashMap<>();
        fileParams.put("file",file);
        String result = HttpClientUtil.uploadFile(url, fileParams, params, null);
        if(StringUtil.isNotBlank(result)){
            JSONObject obj = JSONObject.parseObject(result);
            String code = obj.getString("code");
            if("0".equals(code)){
                String data = obj.getString("data");
                try {
                    JSONObject jsonData = JSON.parseObject(data);
                    return jsonData.getString("objectName");
                } catch (Exception e) {
                    return data;
                }
            }else{
                log.error("上传文件服务返回，返回{}",result);
                throw new ApiException(SysResultCodeEnum.File_SERVE_ERROR);
            }
        }

        return "";
    }


    public static boolean deleteFile(String storeName) throws Exception {
        //刷新配置
        refresh();
        String url = URL;
        String secret = SECRET;
        String appkey = APP_KEY;
        String encryType = ENCRY_TYPE;
        if(!url.endsWith("/")){
            url = url + "/";
        }
        url = url +"minio/delete";
        long ts = System.currentTimeMillis()/1000L;
        String uri="/minio/delete";
        String version = "V1";

        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.put("appKey",appkey);
        sortedMap.put("fileName", storeName);
        sortedMap.put("uri", uri);
        sortedMap.put("version", version);
        sortedMap.put("ts", String.valueOf(ts));
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entry2 = sortedMap.entrySet();
        for (Map.Entry<String, String> entry : entry2) {
            sb.append(entry.getValue());
        }
        sb.append(secret);

        String token = HashAlgorithm.stringToHash(encryType, sb.toString());
        Map<String,String> params = new HashMap<>();
        params.put("appKey",appkey);
        params.put("version",version);
        params.put("ts",ts+"");
        params.put("token",token);
        params.put("fileName",storeName);

        String result = HttpClientUtil.postForm(url, params);
        if(StringUtils.isBlank(result)){
            return false;
        }
        JSONObject obj = JSONObject.parseObject(result);
       // {"code":"0","msg":null,"requestId":"a011091e6c3e477386cefb923ecdbe05","data":"删除成功"}
        if("0".equals(obj.getString("code"))){
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        String url = "http://127.0.0.1:8586";
        String appkey = "qman";
        String secret = "7ei839ILI893ei6s9ldQ834wirRIR98";
        String encryType = "sha256";
        String stormName = "aaa.pdf";
        deleteFile(stormName);

    }
    public static void downLoadFile(String storeName, HttpServletResponse response)throws Exception {
        downLoadFile(storeName, response,null);
    }
    /**
     * 将文件服务保存的文件写到流中
     * @param storeName 文件服务保存的key
     * @param response
     * @throws Exception
     */
    public static void downLoadFile(String storeName, HttpServletResponse response, String saveAsFileName)throws Exception {
        //刷新配置
        refresh();
        String url = URL;
        String secret = SECRET;
        String appkey = APP_KEY;
        String encryType = ENCRY_TYPE;
        if (StringUtils.isBlank(storeName)||StringUtils.isBlank(url) || StringUtils.isBlank(secret) || StringUtils.isBlank(appkey) || StringUtils.isBlank(encryType)) {
            throw new FdException("获取文件失败");
        }

        if (!url.endsWith("/")) {
            url = url + "/";
        }
        url = url + "minio/download";


        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("POST");
        // 设置是否从httpUrlConnection读入，默认情况下是true;
        connection.setDoInput(true);
        // 设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认是false;
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        // 设置字符编码
        connection.setRequestProperty("Accept-Charset", "utf-8");
        // 设置内容类型
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        long ts = System.currentTimeMillis() / 1000L;
        // String fileName="group1/M00/00/00/wKh4F1-BbGeAQHuFAAAACvdCPaU591.txt";
        String uri = "/minio/download";
        String version = "V1";

        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.put("appKey", appkey);
        sortedMap.put("fileName", storeName);
        sortedMap.put("uri", uri);
        sortedMap.put("version", version);
        sortedMap.put("ts", String.valueOf(ts));
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entry2 = sortedMap.entrySet();
        for (Map.Entry<String, String> entry : entry2) {
            sb.append(entry.getValue());
        }
        sb.append(secret);
        String token = HashAlgorithm.stringToHash(encryType, sb.toString());
        String paramsStr = "version=" + URLEncoder.encode(version, "UTF-8") + "&ts=" + URLEncoder.encode(ts + "", "UTF-8")
                + "&fileName=" + URLEncoder.encode(storeName + "", "UTF-8") + "&token=" + URLEncoder.encode(token + "", "UTF-8")
                + "&appKey=" + URLEncoder.encode(appkey, "UTF-8");
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        // 发送请求params参数
        out.write(paramsStr);
        out.flush();

        int code = connection.getResponseCode();
        if (code == 200 || code == 206) {
            // 1-pdf,2-mp4
            String fileName = storeName;
            if(StringUtil.isNotEmpty(saveAsFileName)) {
                fileName = saveAsFileName;
            }
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", new String(fileName.getBytes("UTF-8"), "ISO-8859-1")));
            String contentType = "";
            if (storeName.endsWith("pdf")) {
                contentType = "application/pdf;charset=utf-8";
            } else if (storeName.endsWith("mp4")) {
                contentType = "video/mpeg4;charset=utf-8";
            } else if (storeName.endsWith("xml")) {
                contentType = "text/xml;charset=utf-8";
            } else if(storeName.endsWith("JPG")||storeName.endsWith("jpg") ||storeName.endsWith("jpeg") ||storeName.endsWith("JPEG")){
                contentType ="image/jpeg;charset=utf-8";
            } else if(storeName.endsWith("png")||storeName.endsWith("PNG") ){
                contentType ="image/png;charset=utf-8";
            } else if (storeName.endsWith("gif")||storeName.endsWith("GIF") ){
                contentType ="image/gif;charset=utf-8";
            }else if(storeName.endsWith("doc") || storeName.endsWith("docx")) {
                contentType = "application/msword;charset=utf-8";
            }else if(storeName.endsWith("zip")) {
                contentType = "application/octet-stream;charset=utf-8";
            } else {
                throw new FdException("获取文件失败");
            }
            response.setContentType(contentType);
            response.setCharacterEncoding("UTF-8");
            InputStream is = connection.getInputStream();
            byte[] bytes = new byte[1024];
            OutputStream outStream = response.getOutputStream();
            int index;
            while ((index = is.read(bytes)) != -1) {
                outStream.write(bytes, 0, index);
                outStream.flush();
            }
            outStream.close();
            is.close();
        }
    }


    public static void createFile(String filePath, String storeName)throws Exception {
        //刷新配置
        refresh();
        String url = URL;
        String secret = SECRET;
        String appkey = APP_KEY;
        String encryType = ENCRY_TYPE;
        if (StringUtils.isBlank(storeName)||StringUtils.isBlank(filePath)||StringUtils.isBlank(url) || StringUtils.isBlank(secret) || StringUtils.isBlank(appkey) || StringUtils.isBlank(encryType)) {
            throw new FdException("获取文件失败");
        }
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        url = url + "minio/download";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        // 设置是否从httpUrlConnection读入，默认情况下是true;
        connection.setDoInput(true);
        // 设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认是false;
        connection.setDoOutput(true);
        connection.setUseCaches(false);
        // 设置字符编码
        connection.setRequestProperty("Accept-Charset", "utf-8");
        // 设置内容类型
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        long ts = System.currentTimeMillis() / 1000L;
        String uri = "/minio/download";
        String version = "V1";
        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.put("appKey", appkey);
        sortedMap.put("fileName", storeName);
        sortedMap.put("uri", uri);
        sortedMap.put("version", version);
        sortedMap.put("ts", String.valueOf(ts));
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entry2 = sortedMap.entrySet();
        for (Map.Entry<String, String> entry : entry2) {
            sb.append(entry.getValue());
        }
        sb.append(secret);
        String token = HashAlgorithm.stringToHash(encryType, sb.toString());
        String paramsStr = "version=" + URLEncoder.encode(version, "UTF-8") + "&ts=" + URLEncoder.encode(ts + "", "UTF-8")
                + "&fileName=" + URLEncoder.encode(storeName + "", "UTF-8") + "&token=" + URLEncoder.encode(token + "", "UTF-8")
                + "&appKey=" + URLEncoder.encode(appkey, "UTF-8");
        connection.connect();
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        // 发送请求params参数
        out.write(paramsStr);
        out.flush();

        int code = connection.getResponseCode();
        if (code == 200 || code == 206) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                FileUtil.createFolder(filePath);
                File file = new File(filePath+ File.separator +storeName);
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bis = new BufferedInputStream(connection.getInputStream());
                int c;
                byte[] buffer = new byte[8 * 1024];
                while ((c = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, c);
                    bos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public static String getFileUrl(String fileStorePath) {
        //刷新配置
        refresh();
        String url = URL;
        String secret = SECRET;
        String appkey = APP_KEY;
        String encryType = ENCRY_TYPE;
        if(StringUtil.isBlank(url)|| StringUtil.isBlank(secret)||StringUtil.isBlank(appkey)||StringUtil.isBlank(encryType)||StringUtil.isBlank(fileStorePath)){
            log.info("文件服务上传必须参数未配置");
            return "";
        }
        if(!url.endsWith("/")){
            url = url + "/";
        }
        url = url +"minio/url";
        long ts = System.currentTimeMillis()/1000L;
        String uri="/minio/url";
        String version = "V1";

        SortedMap<String, String> sortedMap = new TreeMap<String, String>();
        sortedMap.put("appKey",appkey);
        sortedMap.put("fileName", fileStorePath);
        sortedMap.put("uri", uri);
        sortedMap.put("version", version);
        sortedMap.put("ts", String.valueOf(ts));
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entry2 = sortedMap.entrySet();
        for (Map.Entry<String, String> entry : entry2) {
            sb.append(entry.getValue());
        }
        sb.append(secret);

        String token = HashAlgorithm.stringToHash(encryType, sb.toString());
        Map<String,String> params = new HashMap<>();
        params.put("appKey",appkey);
        params.put("version",version);
        params.put("ts",ts+"");
        params.put("token",token);
        params.put("fileName",fileStorePath);
        String result = HttpClientUtil.uploadFile(url, null, params, null);
        log.debug("到文件服务器获取文件url:{},params:{},result:{}", new Object[]{url,params,result});
        if(StringUtil.isNotBlank(result)){
            JSONObject obj = JSONObject.parseObject(result);
            String code = obj.getString("code");
            if("0".equals(code)){
                String data = obj.getString("data");
                return data;
            }else{
                log.error("到文件服务器获取文件url，返回{}",result);
                throw new ApiException(SysResultCodeEnum.File_SERVE_ERROR);
            }
        }
        return "";
    }
}
