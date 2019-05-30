package com.weixin;


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionSupport;
import com.weixin.util.Order;
import com.weixin.util.PayReq;
import com.weixin.util.SecurityUtils;
import com.weixin.util.SitePropertiesUtils;
import com.weixin.util.Struts2Utils;
import com.weixin.util.WeChatPayUtil;
import com.weixin.util.WeixinmpController;

public class WeixinPayAction extends ActionSupport implements ServletRequestAware{
	private HttpServletRequest request;
	private String appid;//微信公众号的APPID
	private String appsecret;//微信appsecret
	private String openid;
	private String urlBack;//微信项目域名
	/** 微信公众平台API控制器实例 */
	@Autowired
	private WeixinmpController controllerSite;
	
	{
		resetRedirectAndAppid();
	}
	
	public String getPayPage(){
		try {
			System.out.println("8888888888888888888");
			//取openid
			getWeixInformation(request);
			if (appid == null) {
				return SUCCESS;
			}
			Order order = new Order();
			order.setAppid(appid);
			order.setMch_id("1449143602");
			order.setId(String.valueOf(new Date().getTime()));////本地订单
			String ip = Struts2Utils.getIp(); 
			System.out.println(ip);
			order.setIp(ip);
			order.setFinalAmount(BigDecimal.valueOf(0.01));//订单总金额
			order.setOpenid(openid);
			order.setUrl(urlBack);
			String prePayId = WeChatPayUtil.getPrepayId("https://api.mch.weixin.qq.com/pay/unifiedorder", order);
			System.out.println("prePayId输出:"+prePayId);
			//Map<String, String> map = new HashMap<String, String>();
			PayReq payReq = null;
			if (prePayId != null) {
				//取得支付参数
				payReq = new PayReq();
				payReq.setAppId(appid);
				payReq.setPartnerId("1449143602");
				payReq = WeChatPayUtil.genPayReq(payReq, prePayId);
			}
			System.out.println(payReq);
			request.setAttribute("payReq", payReq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
	public String getPay(){
		
		return SUCCESS;
	}
	
	public String getUrlBack(){
		System.out.println("微信回调地址");
		return SUCCESS;
	}
	
	/**
	 * 取openid
	 * @return
	 * @throws Exception
	 */
	public boolean getWeixInformation(HttpServletRequest request) throws Exception {
		if (openid == null) {
			openid = SecurityUtils.getOpenidBySession();
			if (null == openid) {
				String code = request.getParameter("code");

				System.out.println(code);
				if (code == null) {
					return false;
				}
				SecurityUtils.putOpenidToSession(appid, appsecret,code);
				openid = SecurityUtils.getOpenidBySession();
			}
		}
		return false;
	}
	
	/**
	 * 获取token
	 */
	private String gainToken() throws Exception{
		String accessToken=controllerSite.getAccessToken(false).access_token;//获取token
		return accessToken;
	}
	
	/**
	 * 取得微信固定参数
	 */
	private void resetRedirectAndAppid() {
		appid=SitePropertiesUtils.getProperty("appid", null, false);
		appsecret = SitePropertiesUtils.getProperty("appsecret", null, false);
		urlBack = SitePropertiesUtils.getProperty("url", null, false);
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public void setUrlBack(String urlBack) {
		this.urlBack = urlBack;
	}

	public void setControllerSite(WeixinmpController controllerSite) {
		this.controllerSite = controllerSite;
	}

}
