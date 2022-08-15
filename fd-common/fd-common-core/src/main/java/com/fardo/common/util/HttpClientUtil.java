package com.fardo.common.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
public class HttpClientUtil {


    private static final String APPLICATION_JSON = "application/json";
    private static final String CONTENT_TYPE_TEXT_JSON = "text/json";
    private static final String DEFAULT_OF_CHARSET = "UTF-8";

    public static  String postForm(String url, Map<String, String> params) {

        HttpClient httpClient = null;
        String body = "";
        try{
            httpClient = new SSLClient();
            // httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", jwToken);
            String json = JSON.toJSONString(params);
            HttpPost httpost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();

            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }

            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            body = invoke(httpClient, httpost);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return body;
    }

    public static String post(String url, Map<String, Object> params, Map<String,String> hearder) throws Exception {
        HttpClient httpClient = null;
        String body = null;
        try{
            httpClient = new SSLClient();
            // httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", jwToken);
            String json = JSON.toJSONString(params);
            HttpPost post = postJson(url, json,hearder);
            body = invoke(httpClient, post);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return body;
    }
    public static String post(String url, Map<String, String> params) throws Exception {
        HttpClient httpClient = null;
        String body = null;
        try{
            httpClient = new SSLClient();
            String json = JSON.toJSONString(params);
            HttpPost post = postJson(url, json);
            body = invoke(httpClient, post);
        }catch(Exception ex){
            ex.printStackTrace();
            throw ex;
        }
        return body;
    }



    private static String invoke(HttpClient httpclient, HttpUriRequest httpost) throws IOException {
        HttpResponse response = sendRequest(httpclient, httpost);
        String body = paseResponse(response);
        return body;
    }

    private static String paseResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity);
    }

    private static HttpResponse sendRequest(HttpClient httpclient, HttpUriRequest httpost) {
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpost);
            if(response.getStatusLine().getStatusCode() == 200){
                return response;
            }else {
                httpost.abort();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{

        }
        return null;
    }

    private static HttpPost postJson(String url, String json){
        try {
            //String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            StringEntity se = new StringEntity(json, DEFAULT_OF_CHARSET);
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            httpPost.setEntity(se);
            return httpPost;
        } catch (Exception e) {
            throw e;
        }
    }

    private static HttpPost postJson(String url, String json, Map<String, String> header){
        try {
            //String encoderJson = URLEncoder.encode(json, HTTP.UTF_8);
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON);
            if(header.size()>0){
                Set<String> keys = header.keySet();
                for(String key:keys){
                    httpPost.addHeader(key, header.get(key));
                }
            }

            StringEntity se = new StringEntity(json, DEFAULT_OF_CHARSET);
            se.setContentType(CONTENT_TYPE_TEXT_JSON);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, APPLICATION_JSON));
            httpPost.setEntity(se);
            return httpPost;
        } catch (Exception e) {
            throw  e;
        }
    }

    public static String sendPostWithFile(MultipartFile file, String requestUrl,String fileName, Map<String,String> params, Map<String,String> headers) {
        DataOutputStream out = null;
        final String newLine = "\r\n";
        final String prefix = "--";
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            String BOUNDARY = UUIDGenerator.generate();
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            if(headers!=null && headers.size()>0){
                for (String key : headers.keySet()) {
                    conn.setRequestProperty(key,headers.get(key));
                }
            }

            out = new DataOutputStream(conn.getOutputStream());

            // 添加参数file
            StringBuilder sb1 = new StringBuilder();
            sb1.append(prefix);
            sb1.append(BOUNDARY);
            sb1.append(newLine);
            sb1.append("Content-Disposition: form-data;name=\"file\";filename=\"" + fileName + "\"" + newLine);
            sb1.append("Content-Type:application/octet-stream");
            sb1.append(newLine);
            sb1.append(newLine);
            out.write(sb1.toString().getBytes());
            DataInputStream in = new DataInputStream(file.getInputStream());
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            out.write(newLine.getBytes());
            in.close();

            // 添加参数
            if(params!=null && params.size()>0){
                for (String key : params.keySet()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(prefix);
                    sb.append(BOUNDARY);
                    sb.append(newLine);
                    sb.append("Content-Disposition: form-data;name="+key);
                    sb.append(newLine);
                    sb.append(newLine);
                    sb.append(params.get(key));
                    out.write(sb.toString().getBytes());
                }

            }

            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            // 写上结尾标识
            out.write(end_data);
            out.flush();
            out.close();

            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuffer result = new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                result.append(line);
            }
            return result.toString();
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 使用httpclint 发送文件，如果不传输文件，直接设置fileParams=null，
     * 如果不设置请求头参数，直接设置headerParams=null，就可以进行普通参数的POST请求了
     *
     * @param url          请求路径
     * @param fileParams   文件参数
     * @param otherParams  其他字符串参数
     * @param headerParams 请求头参数
     * @return
     */
    public static String uploadFile(String url, Map<String, MultipartFile> fileParams, Map<String, String> otherParams, Map<String, String> headerParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost(url);
            //设置请求头
            if (headerParams != null && headerParams.size() > 0) {
                for (Map.Entry<String, String> e : headerParams.entrySet()) {
                    String value = e.getValue();
                    String key = e.getKey();
                    if (StringUtils.isNotBlank(value)) {
                        httpPost.setHeader(key, value);
                    }
                }
            }
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//加上此行代码解决返回中文乱码问题
            //    文件传输http请求头(multipart/form-data)
            if (fileParams != null && fileParams.size() > 0) {
                for (Map.Entry<String, MultipartFile> e : fileParams.entrySet()) {
                    String fileParamName = e.getKey();
                    MultipartFile file = e.getValue();
                    if (file != null) {
                        String fileName = file.getOriginalFilename();
                        builder.addBinaryBody(fileParamName, file.getInputStream(), ContentType.DEFAULT_BINARY, fileName);// 文件流
                    }
                }
            }
            //    字节传输http请求头(application/json)
            ContentType contentType = ContentType.create("application/json", Charset.forName("UTF-8"));
            if (otherParams != null && otherParams.size() > 0) {
                for (Map.Entry<String, String> e : otherParams.entrySet()) {
                    String value = e.getValue();
                    if (StringUtils.isNotBlank(value)) {
                        builder.addTextBody(e.getKey(), value, contentType);// 类似浏览器表单提交，对应input的name和value
                    }
                }
            }
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);// 执行提交
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
