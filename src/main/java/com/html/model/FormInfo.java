package com.html.model;

import java.util.Map;

public class FormInfo {
	
	private String account;
	private String cipher;
	private String generator;
	private String action;
	private String It;
	private String eventId;
	private String timezone;
	private String checkCodeTemp;
	private String method;
	private Map<String, String> cookies;
	
	@Override
	public String toString() {
		return "FormInfo [account=" + account + ", cipher=" + cipher + ", generator=" + generator + ", action=" + action
				+ ", It=" + It + ", eventId=" + eventId + ", timezone=" + timezone + ", checkCodeTemp=" + checkCodeTemp
				+ ", method=" + method + ", cookies=" + cookies + "]";
	}
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getCipher() {
		return cipher;
	}
	public void setCipher(String cipher) {
		this.cipher = cipher;
	}
	public String getGenerator() {
		return generator;
	}
	public void setGenerator(String generator) {
		this.generator = generator;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getIt() {
		return It;
	}
	public void setIt(String it) {
		It = it;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getCheckCodeTemp() {
		return checkCodeTemp;
	}
	public void setCheckCodeTemp(String checkCodeTemp) {
		this.checkCodeTemp = checkCodeTemp;
	}
	public Map<String, String> getCookies() {
		return cookies;
	}
	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}

}
