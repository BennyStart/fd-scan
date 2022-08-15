package com.fardo.common.api.entity;

import com.fardo.common.util.security.Base64;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;

/**
 * @作者:Lijun
 * @创建日期:2018年11月02日
 * @描述:
 */
public class FdMultipartFile implements MultipartFile {
    /**
     *
     */
    private String fileName;
    /**
     *
     */
    private String fileContent;

    public FdMultipartFile(String fileName, String fileContent) {
        super();
        this.fileName = fileName;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        String contentType  = new MimetypesFileTypeMap().getContentType(new File(fileName));
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        Long fileSize = new Long(0);
        InputStream in = null;
        try {
            in = this.getInputStream();
            fileSize = Long.valueOf(in.available());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileSize;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return Base64.decodeBase64(fileContent);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.getBytes());
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            if(!dest.exists()&&dest.isDirectory()){//判断文件目录是否存在
                dest.mkdirs();
            }
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(this.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
