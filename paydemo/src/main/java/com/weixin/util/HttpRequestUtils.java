package com.weixin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestUtils {

	private final static String END_POION = "http://120.25.58.35";
	private final static String port = "8080";
	private static Logger logger = LoggerFactory
			.getLogger(HttpRequestUtils.class);
	private StringBuffer httpRequestUri = new StringBuffer();

	private StringBuffer param = new StringBuffer();

	// private static HttpRequestUtils instance;

	// /**
	// * 防止被重写
	// */
	// private HttpRequestUtils() {
	// }
	//
	// /**
	// * 懒汉模式，线程安全
	// *
	// * @return
	// */
	// public static synchronized HttpRequestUtils getInstance() {
	// if (instance == null) {
	// instance = new HttpRequestUtils();
	// }
	// return instance;
	// }

	/*private void resetUri(String url, Map<String, String> params) {
		httpRequestUri = new StringBuffer();
		param = new StringBuffer();
		httpRequestUri.append(END_POION).append(":").append(port).append(url);
		if (params != null) {
			for (Entry<String, String> entry : params.entrySet()) {
				param.append(entry.getKey()).append("=")
						.append(entry.getValue()).append("&");
			}
			param.append("1=1");
		}
	}*/

	public String getOpenid(String appid, String code, String secret) {
		logger.debug("回调微信页面参数appid:" + appid + "；code：" + code + ";secret："
				+ secret);
		StringBuffer buffer = new StringBuffer();
		System.out.println("已执行");
		System.out.println("Url数据:"+"https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code");
		String jsonStr = sendPost("https://api.weixin.qq.com/sns/oauth2/access_token",
				"appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code");
		/*buffer.append(
				"https://api.weixin.qq.com/sns/oauth2/access_token?appid=")
				.append(appid).append("&secret=").append(secret)
				.append("&code=").append(code)
				.append("&grant_type=authorization_code");
		System.out.println(("回调微信页面URL:" + buffer.toString()));
		String jsonStr = sendGet(buffer.toString());*/
		System.out.println(("回调微信页面返回JSON:" + jsonStr));
		System.out.println("Json="+jsonStr.toString());
		String openid = StringUtils.substringBetween(jsonStr, "openid\":\"",
				"\",\"scope");
		logger.debug("回调微信页面截取到的openid:" + openid);
		return openid;
	}
	
	/**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
        	CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }    
	
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的action 头文字必须带/
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	public String sendGet(String url) {
		String result = "";
		BufferedReader in = null;
		try {
			// String urlNameString = url;
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			// 建立实际的连接
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				logger.debug(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			System.out.println("发送GET请求出现异常！" + e);
			e.printStackTrace();
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的action 头文字必须带/
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 */
//	public JSONObject sendPost(String url, Map<String, String> params) {
//
//		resetUri(url, params);
//		logger.debug("请求的url：" + httpRequestUri);
//		PrintWriter out = null;
//		BufferedReader in = null;
//		String result = "";
//		try {
//			URL realUrl = new URL(httpRequestUri.toString());
//			// 打开和URL之间的连接
//			URLConnection conn = realUrl.openConnection();
//			// 设置通用的请求属性
//			conn.setRequestProperty("accept", "*/*");
//			conn.setRequestProperty("connection", "Keep-Alive");
//			conn.setRequestProperty("user-agent",
//					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//			// 发送POST请求必须设置如下两行
//			conn.setDoOutput(true);
//			conn.setDoInput(true);
//			// 获取URLConnection对象对应的输出流
//			out = new PrintWriter(conn.getOutputStream());
//			// 发送请求参数
//			out.print(param);
//			// flush输出流的缓冲
//			out.flush();
//			// 定义BufferedReader输入流来读取URL的响应
//			in = new BufferedReader(
//					new InputStreamReader(conn.getInputStream()));
//			String line;
//			while ((line = in.readLine()) != null) {
//				result += line;
//			}
//		} catch (Exception e) {
//			System.out.println("发送 POST 请求出现异常！" + e);
//			e.printStackTrace();
//		}
//		// 使用finally块来关闭输出流、输入流
//		finally {
//			try {
//				if (out != null) {
//					out.close();
//				}
//				if (in != null) {
//					in.close();
//				}
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//		}
//		if (result == null || result.trim().length() == 0) {
//			return null;
//		}
//		JSONObject jo = JSONObject.fromObject(result.replace("null", "[]"));
//		return jo;
//	}

}
