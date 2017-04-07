package com.html.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.html.model.FormInfo;

public class HtmlService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 获取网页的数据，模拟浏览器发送请求。
	 * */
	public FormInfo getHtmlMessage(String loginUrl){
		FormInfo fi = new FormInfo();
		//获得连接
		Connection con = Jsoup.connect(loginUrl);
		con.header("Accept", "*/*");
		con.header("Connection", "keep-alive");
		con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		Response rs;
		try {
			rs = con.execute();//获取响应
			Map<String, String> cookies = rs.cookies();//获取cookies
			logger.debug("---------------------------------COOKIES-START-----------------------------");
			for (String key : cookies.keySet()) {
				logger.debug(key + "--->" + cookies.get(key));
			}
			logger.debug("---------------------------------COOKIES-END-----------------------------");
			fi.setCookies(cookies);//设置cookies, 其中的JSESSIONID为以后获取验证码，表单提交所使用
			logger.info("JSESSIONID--->"+cookies.get("JSESSIONID"));
			Document doc = Jsoup.parse(rs.body());//转换为Dom树
			Element els = doc.getElementById("form1");//获取PMO的form表单
			//获取form的提交地址：action
			fi.setAction(els.attr("action"));
			logger.info("action--->"+els.attr("action"));
			//获取lt
			fi.setIt(els.select("input[id=lt]").attr("value"));
			logger.info("lt--->"+els.select("input[id=lt]").attr("value"));
			//获取_eventId
			fi.setEventId(els.select("input[id=_eventId]").attr("value"));
			logger.info("_eventId--->"+els.select("input[id=_eventId]").attr("value"));
			//timezone //一般而言，如果当地时间早于UTC时间(在UTC时区以东，例如亚洲地区)，则返回值为负；如果当地时间晚于UTC时间(在UTC时区以西，例如美洲地区)，则返回值为正。
			fi.setTimezone("-480");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fi;
	}
	
	/**
	 * 获取验证码，生成验证码图片
	 */
	public void getCheckCodeImg(String imgUrl, String sessionID){
		InputStream in=null;
		BufferedOutputStream out=null;
		File file = new File(System.getProperty("user.dir")+"\\checkCode.jpg");
		try {
            // 获取图片URL
            URL url = new URL(imgUrl);
            // 获得连接
            URLConnection connection = url.openConnection();
            //设置请求中的sessionID,这一步很重要。
            connection.setRequestProperty("Cookie", "JSESSIONID="+sessionID);
            // 设置10秒的相应时间
            connection.setConnectTimeout(10 * 1000);
            // 获得输入流
            in = connection.getInputStream();
            // 获得输出流
            out = new BufferedOutputStream(new FileOutputStream(file));
            // 构建缓冲区
            byte[] buf = new byte[1024];
            int size;
            // 写入到文件
            while (-1 != (size = in.read(buf))) {
                out.write(buf, 0, size);
            }
            logger.info("图片生成成功.");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            } catch(IOException ex){
                ex.printStackTrace();
            }
        }
	}
	
	/** 
     * 向指定URL发送AJAX方法的请求 
     * 
     * @param codeUrl 发送请求的URL
     * @param code 验证码 
     * @param sessionID 会话标识 
     * @return
     */ 
	public boolean ajaxCheckCode(String codeUrl, String code, String sessionID){
		//获得连接
		Connection con = Jsoup.connect(codeUrl);
		con.header("Accept", "application/json, text/javascript, */*");
		con.header("Connection", "keep-alive");
		con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		con.header("X-Requested-With", "XMLHttpRequest");
		//设置请求中的sessionID,这一步很重要。
		con.cookie("JSESSIONID", sessionID);
		con.data("code", code);
		try {
			JSONObject jsonObj = JSON.parseObject(con.get().body().text());
			String result = jsonObj.getString("result");
			logger.info("图片验证的结果：[{}],发送的code:[{}],JSESSIONID的值:[{}]", con.get().body().text(), code, sessionID);
			if("true".equals(result)){
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 提交登录信息
	 * 
	 * @return
	 */
	public Map<String, String> postLoginUrl(String postUrl, FormInfo fi, String checkCode){
		Connection con=Jsoup.connect(postUrl);
		con.header("Accept", "*/*");
		con.header("Connection", "keep-alive");
		con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		//设置cookie和post上面的map数据
		con.ignoreContentType(true);
		con.method(Method.POST);
		con.data("lt", fi.getIt());
		con.data("_eventId",fi.getEventId());
		con.data("timezone",fi.getTimezone());//-480
		con.data("account",fi.getAccount());
		con.data("cipher", fi.getCipher());
		con.data("checkCode", checkCode);
		con.data("checkCodeTemp", fi.getCheckCodeTemp());
		con.cookie("JSESSIONID", fi.getCookies().get("JSESSIONID"));
		
		Map<String, String> resultMap = null;
		try {
			Response rs = con.execute();
			Map<String, String> cookies =  rs.cookies();
			//{Content-Language=en-US, Date=Fri, 07 Apr 2017 01:13:30 GMT, P3P=CP="CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR", Content-Length=149, Expires=Thu, 01 Jan 1970 00:00:00 GMT, Connection=keep-alive, Content-Type=text/html;charset=UTF-8, Server=nginx/1.9.12, Cache-Control=no-store, Pragma=No-cache}
			resultMap = rs.headers();//响应请求的Head数据
			logger.info("JSESSIONID:[{}]\n\t, 返回的状态码:[{}]\n\t, Response URL[{}]", fi.getCookies().get("JSESSIONID"), rs.statusCode(), rs.url().toString());
			logger.info("提交表单后返回的cookies[{}], Response Head[{}]", cookies, resultMap);
			//判断一下返回的结果中是不是有TGT，有就是成功了(～￣▽￣)～，没有就是失败了
			if(cookies.get("CASTGC")!=null && !"".equals(cookies.get("CASTGC"))){
				//合并一下返回的结果    resultMap.putAll(map);
				//System.out.println("####################"+rs.body());
				for(String k:cookies.keySet()){
					resultMap.put(k, cookies.get(k));
				}
				resultMap.put("JSESSIONID", fi.getCookies().get("JSESSIONID"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 获取子票据获取地址
	 * http://eoms.ultrapower.com.cn/ultrapmo/portal/index_new.action
	 * /ucas/login?service=http%3A%2F%2Feoms.ultrapower.com.cn%2Fultrapmo%2Fj_acegi_cas_security_check%3Bjsessionid%3DFE74E19E8A104907D131B411FA71AAA5%3Ft%3Dhttp%253A%252F%252Feoms.ultrapower.com.cn%252Fultrapmo%252Fportal%252Findex_new.action
	 * @return
	 */
	public Map<String, String> getSTLocation(String url){
		
		Connection con=Jsoup.connect(url);
		con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		con.header("Connection", "keep-alive");
		con.header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		con.header("Accept-Encoding", "gzip, deflate");
		con.header("Upgrade-Insecure-Requests", "1");
		con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
		con.method(Method.GET);
		Map<String, String> map = new HashMap<>();
		try {
			Response rs = con.execute();
			Map<String,String> m = con.request().cookies();
			//FIXME 这个获取的jseesionid和URL中的SESSIONID不一样很是不理解为啥，在浏览器上访问的时候发现Response中的和URL中的为同一个，不理解这里为啥不一样了。
//			String jsessionid = rs.cookies().get("JSESSIONID");//jsessionid != m.get("JSESSIONID")
			map.put("pmoJSESSIONID", m.get("JSESSIONID"));
			map.put("location", rs.url().toString());
			logger.info("getSTLocation此方法发送的url[{}]\n\t, 返回的子票据获取的地址:[{}]\n\t, 返回的JSESSIONID[{}]", url, rs.url(), m.get("JSESSIONID"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 获取子票据ST
	 * uMap  为cas的结果集包括cookies和head，pMap为PMO的结果集
	 *  http://eoms.ultrapower.com.cn/ultrapmo/j_acegi_cas_security_check;jsessionid=FE74E19E8A104907D131B411FA71AAA5?t=http%3A%2F%2Feoms.ultrapower.com.cn%2Fultrapmo%2Fportal%2Findex_new.action&ticket=ST-165313-hIGdkEWcWzfNhJ4pWUmC
	 * @return
	 */
	public String getST(Map<String, String> uMap, Map<String, String> pMap){
		if(null != pMap.get("location") && pMap.get("pmoJSESSIONID") != null){
			Connection con=Jsoup.connect(pMap.get("location"));
			con.header("Accept", "*/*");
			con.header("Connection", "keep-alive");
			con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			con.method(Method.GET);
			con.cookie("JSESSIONID", pMap.get("pmoJSESSIONID"));
			con.cookie("CASTGC", uMap.get("CASTGC"));
			con.cookie("P_UUID", "");
			try {
				Response rs = con.execute();
				logger.info("getST此方法发送的url[{}]\n\t, 获取的到的URL【{}】\n\t, 发送的JSESSIONID[{}]\n\t, 发送的TGT:[{}]",pMap.get("location"),rs.url().toString(),uMap.get("JSESSIONID"),uMap.get("CASTGC"));
				return rs.url().toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			logger.info("location:"+pMap.get("location"));
			logger.info("JSESSIONID:"+pMap.get("pmoJSESSIONID"));
		}
		return null;
	}
	
	public String getAimUrl(Map<String, String> uMap, Map<String, String> pMap){
		
		String stLocation = getST(uMap, pMap);
		
		if(null != stLocation){
			Connection con=Jsoup.connect(stLocation);
			con.header("Accept", "*/*");
			con.header("Connection", "keep-alive");
			con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			con.method(Method.GET);
			con.cookie("JSESSIONID", pMap.get("pmoJSESSIONID"));
			try {
				Response rs = con.execute();
				logger.info("getAimUrl此方法发送的url[{}]\n\t, 接收到的url[{}]\n\t, 发送的JSESSIONID[{}]", stLocation,rs.url().toString(),pMap.get("pmoJSESSIONID"));
				return rs.url().toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public Map<String, String> toAim(Map<String, String> uMap, Map<String, String> pMap){
		
		String aimLocation = getAimUrl(uMap, pMap);
		if(null != aimLocation){
			Connection con=Jsoup.connect(aimLocation);
			con.header("Accept", "*/*");
			con.header("Connection", "keep-alive");
			con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			con.method(Method.GET);
			con.cookie("JSESSIONID", pMap.get("pmoJSESSIONID"));
			try {
				Response rs = con.execute();
				Map<String, String> headers = rs.headers();
				logger.info("<(￣ˇ￣)/ 登录PMO,状态:[{}], URL:[{}], JSESSIONID:[{}]", rs.statusCode(), aimLocation, pMap.get("pmoJSESSIONID"));
				headers.put("body", rs.body());
				return headers;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
