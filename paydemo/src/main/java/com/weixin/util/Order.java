package com.weixin.util;

import java.math.BigDecimal;

/**
 * 本地组合订单实体类
 * @author Dege
 *
 */
public class Order {
	private String id;//商户订单号
	private String ip;
	private BigDecimal finalAmount;//订单总金额
	private String openid;
	private String appid;
	private String mch_id;//商户号
	private String url;//微信回调
	public String getId() {
		return id;
	}
	public String getIp() {
		return ip;
	}
	public BigDecimal getFinalAmount() {
		return finalAmount;
	}
	public void setFinalAmount(BigDecimal bigDecimal) {
		this.finalAmount = bigDecimal;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "Order [id=" + id + ", ip=" + ip + ", finalAmount="
				+ finalAmount + ", openid=" + openid + ", appid=" + appid
				+ ", mch_id=" + mch_id + ", url=" + url + "]";
	}
	
}
