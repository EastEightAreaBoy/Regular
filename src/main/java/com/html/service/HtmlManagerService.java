package com.html.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.html.model.FormInfo;

public class HtmlManagerService {
	private String sendUrl = "http://pmo.ultrapower.com.cn/ucas";
	//java.net.URLEncoder.encode(title,"utf-8").getBytes(), "ISO-8859-1")+");
	
	/** 
     * 向指定URL发送POST方法的请求 
     * @param url 发送请求的URL
     * @param param 发送请求中的参数 (name1=value1&name2=value2)
     * @return 
     */
	private HttpURLConnection sendPost(String strUrl, String param){
		PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection connection = null;
		try {
			URL url = new URL(strUrl);
			// 获得连接
			connection  = (HttpURLConnection)url.openConnection();
        	// 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数  name1=value1&name2=value2
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 建立实际的连接
            connection.connect();
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
		return connection;
	}
	
	public int getResponseCode(HttpURLConnection connection){
		try {
			return connection.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public StringBuffer getResult(HttpURLConnection connection){
		BufferedReader in = null;
		StringBuffer result = null;
		try {
			in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
			char[] arr = new char [1024];
			int length = -1;
			//结果集
			result = new StringBuffer(1024);
			while ((length = in.read(arr)) != -1) {
				result.append(arr, 0 ,length);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Map getResponseHead(HttpURLConnection connection){
      // 获取所有响应头字段
      Map<String, List<String>> map = connection.getHeaderFields();
      return map;
	}
	/** 
     * 向指定URL发送AJAX方法的请求 
     * @param codeUrl 发送请求的URL
     * @param code 验证码 
     * @return
     */ 
	public boolean ajaxCheckCode(String codeUrl, String code){
		PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection connection = null;
		try {
			URL url = new URL(codeUrl);
			// 获得连接
			connection  = (HttpURLConnection)url.openConnection();
        	// 设置通用的请求属性
            connection.setRequestProperty("accept", "application/json, text/javascript, */*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			Cookie:"P_UUID=; JSESSIONID=900F55620BDD12A8A632CA64354D7489"
            connection.setRequestProperty("cookie","");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 建立实际的连接
            connection.connect();
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
		return false;
	}
	
	public void sendForm(FormInfo fi){
		PrintWriter out = null;
        BufferedReader in = null;
        //String result = "";
		try {
			URL url = new URL(sendUrl+"/"+fi.getAction());
			// 获得连接
			HttpURLConnection connection  = (HttpURLConnection)url.openConnection();
        	// 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(connection.getOutputStream());
            // 发送请求参数  name1=value1&name2=value2
            out.print(createParam(fi));
            // flush输出流的缓冲
            out.flush();
            
            // 建立实际的连接
            connection.connect();
            int statcCode = connection.getResponseCode();
            if(statcCode==302){
            	System.out.println("登录成功");
            }else{
            	System.out.println("登录失败");
            }
//            // 获取所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }

            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
            char[] arr = new char [1024];
            int length = -1;
            //结果集
            StringBuffer result = new StringBuffer(1024);
            while ((length = in.read(arr)) != -1) {
            	result.append(arr, 0 ,length);
            }
            System.out.println(result);
            
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
	
	public FormInfo getFormMessage() throws IOException{
		
		Document doc = getDocument();
		
		Element els = doc.getElementById("form1");
		FormInfo fi = new FormInfo();
		//获取form的提交地址：action
		fi.setAction(els.attr("action"));
		System.out.println("action--->"+els.attr("action"));
		//获取lt
		fi.setIt(els.select("input[id=lt]").attr("value"));
		System.out.println("lt--->"+els.select("input[id=lt]").attr("value"));
		//获取_eventId
		fi.setEventId(els.select("input[id=_eventId]").attr("value"));
		System.out.println("_eventId--->"+els.select("input[id=_eventId]").attr("value"));
		//timezone
		fi.setTimezone("-480");//这里获取不到，可能是这个value是浏览器计算后的值。-480
		System.out.println("timezone--->"+doc.getElementById("timezone").attr("value"));
		
		//System.out.println("checkCodeTemp--->"+els.select("input[name=checkCodeTemp]"));
		
		//获取登录form表单
//		Elements login_bg = els.select("div[class=login_bg]");
		String imgUrl = "http://pmo.ultrapower.com.cn/ucas/user/auth/generator.htm?timestamp="+System.currentTimeMillis();
		System.out.println("IMG URL--->"+imgUrl);
		File file = new File(System.getProperty("user.dir")+"\\checkCode.jpg");
		try {
            // 获取图片URL
            URL url = new URL(imgUrl);
            // 获得连接
            URLConnection connection = url.openConnection();
            // 设置10秒的相应时间
            connection.setConnectTimeout(10 * 1000);
            // 获得输入流
            InputStream in = connection.getInputStream();
            // 获得输出流
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            // 构建缓冲区
            byte[] buf = new byte[1024];
            int size;
            // 写入到文件
            while (-1 != (size = in.read(buf))) {
                out.write(buf, 0, size);
            }
            out.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return fi;
	} 
	
	private String createParam(FormInfo from){
		StringBuffer sb = new StringBuffer();
		sb.append("lt="+from.getIt());
		sb.append("&_evendId="+from.getEventId());
		sb.append("&timezone="+from.getTimezone());
		sb.append("&account="+from.getAccount());
		sb.append("&cipher="+from.getCipher());
		sb.append("&checkCode="+from.getCheckCodeTemp());
		sb.append("&checkCodeTemp="+from.getCheckCodeTemp());
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	private Document getDocument() throws IOException{
		Document doc = Jsoup.connect(sendUrl+"/login").get();
		return doc;
	}
	
	public static void main(String[] args) {
		HtmlManagerService hm = new HtmlManagerService();
		try {
			FormInfo fi = hm.getFormMessage();
			//fi.setIt("_c59DF8FB4-D459-3E79-315D-BDADEC03AAF4_kC6702A7F-6B7A-2FF5-35F2-1EA5407E6B18");
			fi.setAccount("wangzongjie");
			fi.setCipher("77897753f3c976e2335585dd9c19a79a");
//			fi.setCheckCodeTemp(checkCodeTemp);
//			fi.setTimezone("-480");
			hm.sendForm(fi);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
