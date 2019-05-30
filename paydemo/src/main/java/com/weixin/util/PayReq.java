package com.weixin.util;

public class PayReq {
	private String appId;
	private String partnerId;
	private String prepayId;
	private String packageValue;
	private String nonceStr;
	private String timeStamp;
	private String sign;
	public String getAppId() {
		return appId;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public String getPrepayId() {
		return prepayId;
	}
	public String getPackageValue() {
		return packageValue;
	}
	public String getNonceStr() {
		return nonceStr;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "PayReq [appId=" + appId + ", partnerId=" + partnerId
				+ ", prepayId=" + prepayId + ", packageValue=" + packageValue
				+ ", nonceStr=" + nonceStr + ", timeStamp=" + timeStamp
				+ ", sign=" + sign + "]";
	}
	
}
