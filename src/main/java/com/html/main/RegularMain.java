package com.html.main;

import java.util.Map;

import org.apache.log4j.Logger;

import com.html.model.FormInfo;
import com.html.service.HtmlService;
import com.html.service.Tess4jCode;

public class RegularMain {
	private final static Logger logger = Logger.getLogger(RegularMain.class)/*LoggerFactory.getLogger(RegularMain.getClass())*/;
	
	public static void main(String[] args) {
		logger.info("--------------------开始----------------------");
		HtmlService hs = new HtmlService();
		//获取表单信息
		FormInfo fi = hs.getHtmlMessage("http://pmo.ultrapower.com.cn/ucas/login");
		fi.setAccount("wangzongjie");
		fi.setCipher("77897753f3c976e2335585dd9c19a79a");
		double d = Math.random();
		fi.setCheckCodeTemp(d*10000+"");//随机数，应该是为了防止不提交验证码的值吧。
		//获取验证码，生成验证码图片
		hs.getCheckCodeImg("http://pmo.ultrapower.com.cn/ucas/user/auth/generator.htm", fi.getCookies().get("JSESSIONID"));
		
		Tess4jCode tc = new Tess4jCode();
		//获取识别后的验证码
		String code = tc.recognitionCode();
		//String t = "?t=http%3A%2F%2Feoms.ultrapower.com.cn%2Fultrapmo%2Fportal%2Findex_new.action";
		//验证验证码的正确性
		if(hs.ajaxCheckCode("http://pmo.ultrapower.com.cn/ucas/user/auth/validator.htm", code, fi.getCookies().get("JSESSIONID"))){
			String url = "http://pmo.ultrapower.com.cn/ucas/login";
			//提交模拟表单
			Map<String, String> uMap = hs.postLoginUrl(url, fi, code);
			if(null != uMap.get("CASTGC")){
				Map<String, String> pMap = hs.getSTLocation("http://eoms.ultrapower.com.cn/ultrapmo/portal/index_new.action");

				Map<String, String> result = hs.toAim(uMap, pMap);
			}
		}
		
	}

}
