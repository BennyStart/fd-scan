/**
 * @(#)DocToPdf.java
 * 版权声明  厦门法度信息科技有限公司, 版权所有 违者必究
 * 版本号: 
 * 修订记录:
 * 更改者：suzc
 * 时　间：2019年2月22日
 * 描　述：创建
 */

package com.fardo.common.util;

/**
 * @作者:suzc
 * @文件名:DocToPdf
 * @版本号:1.0
 * @创建日期:2019年2月22日
 * @描述:
 */

import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class DocToPdf {
	
	private static final Logger logger = LoggerFactory.getLogger(DocToPdf.class);
	
    public static boolean getLicense() {
        boolean result = false;
        try {
        	String licenseStr =
                    "<License>\n" +
                    "  <Data>\n" +
                    "    <Products>\n" +
                    "      <Product>Aspose.Total for Java</Product>\n" +
                    "      <Product>Aspose.Words for Java</Product>\n" +
                    "    </Products>\n" +
                    "    <EditionType>Enterprise</EditionType>\n" +
                    "    <SubscriptionExpiry>20991231</SubscriptionExpiry>\n" +
                    "    <LicenseExpiry>20991231</LicenseExpiry>\n" +
                    "    <SerialNumber>8bfe198c-7f0c-4ef8-8ff0-acc3237bf0d7</SerialNumber>\n" +
                    "  </Data>\n" +
                    "  <Signature>sNLLKGMUdF0r8O1kKilWAGdgfs2BvJb/2Xp8p5iuDVfZXmhppo+d0Ran1P9TKdjV4ABwAgKXxJ3jcQTqE/2IRfqwnPf8itN8aFZlV3TJPYeD3yWE7IT55Gz6EijUpC7aKeoohTb4w2fpox58wWoF3SNp6sK6jDfiAUGEHYJ9pjU=</Signature>\n" +
                    "</License>";
            InputStream license = new ByteArrayInputStream(licenseStr.getBytes("UTF-8"));
            License asposeLic = new License();
            asposeLic.setLicense(license);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void doc2pdf(String docFilePath, String pdfFilePath) {
        
        if (!getLicense()) {          // 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        FileOutputStream os = null;
        try {
            long old = System.currentTimeMillis();
            File file = new File(pdfFilePath);  //新建一个空白pdf文档
            os = new FileOutputStream(file);
            File docFile = new File(docFilePath);
            Document doc = new Document(docFile.getAbsolutePath());                    //Address是将要被转化的word文档
            doc.save(os, SaveFormat.PDF);//全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF, EPUB, XPS, SWF 相互转换
            long now = System.currentTimeMillis();
            logger.info("共耗时：{}秒", ((now - old) / 1000.0));  //转化用时
        } catch (Exception e) {
           logger.error(e.getMessage(), e);
        }finally{
        	if(os != null){
        		try {
					os.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
        	}
        }
    }
    
    public static void main(String[] args){
    	DocToPdf.doc2pdf("C:/行政案件权利义务告知书.doc","c:/pdf1.pdf");
   }
}
