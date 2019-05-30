package com.weixin.util;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
/**
 * 微信配置文件
 * @author tangQingWang
 * @version V1.0  
 * @date 2017-3-13下午2:39:28
 */
public class SitePropertiesUtils {

	/** 配置文件 */
	private static Properties properties;

	public void init() {

		InputStream in = null;
		try {
			// 加载用户配置文件weixinmp.properties  weixinvenues.properties
			URL url = this.getClass().getResource("/weixinvenues.properties");
			if (url == null) {
				throw new RuntimeException("缺少配置文件：/weixinvenues.properties");
			}
			in = url.openStream();
			properties = new Properties();
			properties.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static String getProperty(String key, String defaultValue,
			boolean allowNull) {
		SitePropertiesUtils propertiesUtils = new SitePropertiesUtils();
		propertiesUtils.init();
		// 首先从用户配置文件读取，读取到为NULL时再从默认配置读取
		Properties pro;
		if (properties.containsKey(key)) {
			pro = properties;
		} 
		else {
			throw new RuntimeException("配置文件weixinvenues.properties缺少："+key);
		}
		// 读取配置值
		String value = pro.getProperty(key);
		if (value == null) {
			value = defaultValue;
		}
		if (value == null) {
			if (!allowNull) {
				throw new RuntimeException("配置文件（"
						+ "/weixinvenues.properties" + "）缺少属性：key="
						+ key);
			}
		}
		return value;
	}
	/**
	 * 根据配置文件名称，以及KEY 获取KEY对应的值
	 * @param name
	 * @param key
	 * @return
	 */
	public static String getPropertiesValueByKey(String name,String key){
		Properties prop = new Properties();
		InputStream in = SitePropertiesUtils.class.getResourceAsStream( "/"+name); 
		try {
			prop.load(in);
		} catch (IOException e) {
			throw new RuntimeException("配置文件"+name+"缺少属性："+key);
		}
		String url = prop.getProperty(key);
		return url;
	}
}
