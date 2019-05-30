package com.zfb;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 */

public class AlipayConfig{

    // ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016080100143949";//例：2016082600317257

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCvkVecqfE3F5sGQXbUH+0cixbNQPQShs1QbSS7/BoPNVvDHomJNKnhNocKMh9El0xKCRvB8X6gIHWTbYlY40UGKvUqmP9YYCYTyIt6TknheSJjAgiVg2UonDYv5VsjoBtCokcueVI1cVSk2C1lq+KaBw8ZQBwiaNQl8wM378qGRs6xSb0qjGvEeRO+YzSgrV47w0iBCFYv0l7svjnrL2B/beySGaAw/HQSYjTrVQIDn+mC1+f9Hlr67mXZ70Sx4AiBc3abs0Z/m4birPIJZKy7EYj4zFDOvnpvvOgMagzrxwPPee3rfpBiHkcUwtrq++yYwHdDFAdbGdLS1qwb9k3jAgMBAAECggEAVwWPi5OggFIvAPbJ03LPsGowryQW1mgCtG6k2pimwgsMpLYqLdvwqDTC0obtgK4NnlTNcWAmB3CPVrfgcDKWL+xzAN49Eqz1srCgkcrkS+Ii7ThA0PsD1RE4upzQg+QEHw8YgSyZVuwJrHuKZO7Brt742OcyCswy+2xsl/k/RTZWK4tHz0pl5kSJpsuKoEUQD1QbjiKVGj1whpL93WARKE0icVXDVVuL4vdwEBJUQNgfDXoATt+os4AsZXnSzL7A77Ldh98rLGVfTVSiENMcnGVWbB6/YcBMX0v35rbxZwx8+amlWq/+vwAXFup6QazeTUMZ+TJftUdYMgo6tp7qQQKBgQD8rvK/bCeh8YcUbZK5r0yCaaolsVtWEJWBtuDCa839T7jEQ9xi0yL5OSfhyLc5onkRnTuW/6arHsJ5Ta92+HktDm+Y/du9sWEA2cQ/ZBSdV3FSnbvtXVtVjEP9ZkjHs7POhVVzTFEez7IZBhz5D/AxS8FC9UhsQ3Zq8mATK2qoawKBgQCx30ZJ+mWBCBsSnPcNr1b1MZy5AGzjZdx50U9TPjTrdVMJHBWL9f1HyuvvJb/zipH91pjl1fgdqXmxTYAj64LuYBpM71r+Y/23ucE0OedkSPkuHd5FPWBihQauy/eIYRp1U+zxXfoUvng8oPdkXExChMAZ0nE4vh6uckVB9dwuaQKBgEtA1y7HSS5jvjUJRQ9j88dLLM84+Niy67PMATgl3x035vEcvL/53YqXyRbnLhpOFvx19U9NKBgCeyyhREaMTZprmDTaNd1SvG8Ca/3tEm6hojBWpadVjGP3+C+5jKddD4nMD9zct7QTaxkkxvAuHPyxcQc+AamsldHYCWDsJ0GtAoGAcxh93ArJuZesJPwqpNFLnHdQ6SkMTIyyAKVfBgdfqOiPJXrlul4fWWBGEMZFkeqdkzpUT4yH83GhjAgRHa0cWPEYs6XCJJBBMaxiB1BcmTja/GtZU0Zm4vkfHOZgsv/aZgc7b+eWleu10814p5lNw1ExGMRifQwQwGKyy/6yTdkCgYEAvDqy+Ya9z7Pn5Evo91N24ng1woxiElxgTR3bqWY05yHf3zVd9PeMvVlecWaAjLceQt3IUAuYiUad9OVVV6SYux4eWdnzcfFoVN8A/klaLQkMh4GjsWh1JdhQHRpZ8f3aGLkaJ8bLAGXqDHGjOvpUzaQ7FtsCi1ZtZYXbPzhFpT0=";//"商户私钥！！！！私钥！！！不是公钥！！！";
    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm
    // 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0AunOvqEdIWAmCMn07zLFv0VygRmcrkYTYCkVJVmaQ9sh7XTR2ZU+18bYVh2/xrfklnST+1QyCR+g5gMcXtkN8NOJTU7+PcucHAFSoqyJvhvoPdzrXEbaPonOoaXurceGoRox1oyIYIR7S6fTTR8kNk70i6S7xNMg0UANBrBZAP9ei9pflSf2eXRGuHEXaE+ZFa/EXvLmlC/SfvTTZHxT+b3Vu0NiXqX2LavnYDucADBy2AVEdp8pc3X+RPaaoc7zvpGsXSSo5k1JVe838HPbKd/cLpQsnZ5n/q/d+XrsWhonkc3NyQcO+w586b0m+MbC5Z76Lsy4ogDrjcWKHi50QIDAQAB";//"支付宝公钥，记得是支付宝公钥!!!!!!!支付宝公钥";
    // 服务器异步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    /**
     * 返回的时候此页面不会返回到用户页面，只会执行你写到控制器里的地址
     */
    public static String notify_url = "http://2177x4017o.imwork.net/paydemo/zfbpay/zfbNotify.do";
    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    /**
     * 此页面是同步返回用户页面，也就是用户支付后看到的页面，上面的notify_url是异步返回商家操作，谢谢
     * 要是看不懂就找度娘，或者多读几遍，或者去看支付宝第三方接口API，不看API直接拿去就用，遇坑不怪别人
     */
    public static String return_url = "http://2177x4017o.imwork.net/paydemo/zfbpay/zfbReturn.do";
    // 签名方式
    public static String sign_type = "RSA2";
	// 返回格式
	public static String format = "json";
    // 字符编码格式
    public static String charset = "gbk";
    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
    // 日志地址
    public static String log_path = "D:/data/";
    // ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * 
     * @param sWord
     *            要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_"
                    + System.currentTimeMillis() + ".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
