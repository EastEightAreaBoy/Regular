package com.html.service;

import com.html.model.FormInfo;

import junit.framework.TestCase;

public class HtmlServiceTest extends TestCase {

	HtmlService hs =null;
	FormInfo info=null;
	{
		hs = new HtmlService();
	}
	public void testGetHtmlMessage() {
		info = hs.getHtmlMessage("http://pmo.ultrapower.com.cn/ucas/login");
		System.out.println("********************"+info);
	}

	public void testGetCheckCodeImg() {
		info = hs.getHtmlMessage("http://pmo.ultrapower.com.cn/ucas/login");
		hs.getCheckCodeImg("http://pmo.ultrapower.com.cn/ucas/user/auth/generator.htm", info.getCookies().get("JSESSIONID"));
	}

	public void testAjaxCheckCode() {
		hs.ajaxCheckCode("http://pmo.ultrapower.com.cn/ucas/user/auth/validator.htm", "l57x", "F10E76B621FF0C6FD9B42F431B241489");
	}

}
