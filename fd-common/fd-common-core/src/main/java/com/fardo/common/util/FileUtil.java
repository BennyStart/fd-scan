package com.fardo.common.util;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fardo.common.api.entity.FdMultipartFile;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUtil {


    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtend(String filename) {
        return getExtend(filename, "");
    }

    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtend(String filename, String defExt) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');

            if ((i > 0) && (i < (filename.length() - 1))) {
                return (filename.substring(i+1)).toLowerCase();
            }
        }
        return defExt.toLowerCase();
    }

    /**
     * 获取文件名称[不含后缀名]
     *
     * @param
     * @return String
     */
    public static String getFilePrefix(String fileName) {
        int splitIndex = fileName.lastIndexOf(".");
        return fileName.substring(0, splitIndex).replaceAll("\\s*", "");
    }

    /**
     * 上传文件封装
     * @param request
     * @return
     */
    public static List<MultipartFile> getUploadFiles(HttpServletRequest request) {
        List<MultipartFile> files = null;
        if(request instanceof MultipartHttpServletRequest){
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            files = multipartRequest.getFiles("file");
        }
        List<MultipartFile> exFiles = getFilesFromBase64Param(request);
        if(files == null){
            files = new ArrayList<>();
        }
        files.addAll(exFiles);

        return files;
    }

    public static List<MultipartFile> getFilesFromBase64Param(HttpServletRequest request){
        List<MultipartFile> files = new ArrayList<>();
        String filesStr = request.getParameter("files");
        if(StringUtils.isNotEmpty(filesStr)){
            List<Map> fileList = JSONObject.parseArray(filesStr,Map.class);
            if(CollectionUtils.isNotEmpty(fileList)){
                for (Map map:fileList) {
                    String fileName = (String)map.get("fileName");
                    String fileContent = (String)map.get("fileContent");
                    FdMultipartFile fdMultipartFile = new FdMultipartFile(fileName,fileContent);
                    files.add(fdMultipartFile);
                }
            }
        }
        return files;
    }


    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static void multipartFileToFile(MultipartFile file, String fileFullPath) {
        File toFile = new File(fileFullPath);
        InputStream ins = null;
        OutputStream os = null;
        try {
            os = new FileOutputStream(toFile);
            ins = file.getInputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static MultipartFile fileToMultipartFile(String fileFullPath) {
        File file = new File(fileFullPath);
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem fileItem = factory.createItem("textField", "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(file);
            os = fileItem.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new CommonsMultipartFile(fileItem);
    }


    /**
     * 删除指定的文件夹
     *
     * @param fileFullName
     *            指定绝对路径的文件名
     * @return 如果删除成功true否则false
     */
    public static void deleteDir(String fileFullName) {
        File dirFile = new File(fileFullName);
        try {
            FileUtils.deleteDirectory(dirFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 文件复制
     *方法摘要：这里一句话描述方法的用途
     *@param
     *@return void
     */
    public static void copyFile(String inputFile,String outputFile) throws FileNotFoundException{
        File sFile = new File(inputFile);
        File tFile = new File(outputFile);
        FileInputStream fis = new FileInputStream(sFile);
        FileOutputStream fos = new FileOutputStream(tFile);
        int temp = 0;
        byte[] buf = new byte[10240];
        try {
            while((temp = fis.read(buf))!=-1){
                fos.write(buf, 0, temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFolder(String filePath) {
        File dir = new File(filePath);
        if(!dir.exists()){//判断文件目录是否存在
            dir.mkdirs();
        }
    }




}
