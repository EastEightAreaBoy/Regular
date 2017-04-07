package com.html.service;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Tess4jCode {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 识别验证码图片
	 * 
	 * @return String
	 */
	public String recognitionCode(){
		String result="";
		File imageFile = new File(System.getProperty("user.dir")+"\\checkCode.jpg");
		logger.info("识别的验证码图片位置：[{}]", imageFile.getAbsolutePath());
        ITesseract instance = new Tesseract();
        
        URL url = ClassLoader.getSystemResource("tessdata");
        String path = url.getPath().substring(1);
//        System.out.println(path);
        instance.setDatapath(path);
        instance.setLanguage("new");
        
        try {
            result = instance.doOCR(imageFile);
            //去掉空格
            if(result.length() > 4){
            	result = result.replaceAll("\\s", "");
            }
            logger.info("识别图片的结果：[{}]",result);
        } catch (TesseractException e) {
        	e.printStackTrace();
        }
        
		return result;
	}
	
	public static void main(String[] args) {
		Tess4jCode tc = new Tess4jCode();
		tc.recognitionCode();
	}
}
