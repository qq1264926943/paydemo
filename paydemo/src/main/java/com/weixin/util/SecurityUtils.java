package com.weixin.util;

/**
 * 特殊化工具
 * @author tangQingWang
 * @version V1.0  
 * @date 2017-3-16下午4:04:25
 */
public class SecurityUtils {

	/**
	 * 将用户放入session中.
	 * 
	 * @param user
	 */
	public static void putOpenidToSession(String appid, String appsecret,String code) {
		System.out.println("putOpenidToSession code:" + code);
		HttpRequestUtils httpRequestUtils = new HttpRequestUtils();
		String openid = httpRequestUtils.getOpenid(appid, code, appsecret);
		System.out.println("putOpenidToSession openid:" + openid);
		Struts2Utils.getSession().setAttribute("openid", openid);
	}

	/**
	 * 将用户放入session中.
	 * 
	 * @param user
	 */
	public static String getOpenidBySession() {
		Object ob = Struts2Utils
				.getSessionAttribute("openid");
		return ob == null ? null : ob.toString();
	}
	
}
