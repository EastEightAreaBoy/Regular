package com.html.main;

import java.util.Map;

import com.html.model.FormInfo;
import com.html.service.HtmlService;
import com.html.service.Tess4jCode;

public class RegularMain {

	public static void main(String[] args) {
		HtmlService hs = new HtmlService();
		//获取表单信息
		FormInfo fi = hs.getHtmlMessage("http://pmo.ultrapower.com.cn/ucas/login");
		fi.setAccount("wangzongjie");
		fi.setCipher("77897753f3c976e2335585dd9c19a79a");
		fi.setCheckCodeTemp("1989");//FIXME 不知道这个值怎么做的。
		
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
				System.out.println(pMap);
				Map<String, String> result = hs.toAim(uMap, pMap);
			}
		}
		
	}

}
