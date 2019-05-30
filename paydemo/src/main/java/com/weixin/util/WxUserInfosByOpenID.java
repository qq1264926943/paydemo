package com.weixin.util;

import java.io.Serializable;
/**
 * 微信接口获取微信用户信息
 * @author tangQingWang
 * @version V1.0  
 * @date 2017-3-14下午5:15:24
 */
public class WxUserInfosByOpenID implements Serializable {
    private static final long serialVersionUID = 1L;
    /**用户的唯一标识 */
    public String openid ;
    /**用户的昵称 */
    public String nickname;
    /**性别 */
    public String sex;
    /**省 */
    public String province;
    /**城市 */
    public String city;
    /**国家 */
    public String country;
    /**用户头像 */
    public String headimgurl;
    //语言
    public String language;
		
    
}
