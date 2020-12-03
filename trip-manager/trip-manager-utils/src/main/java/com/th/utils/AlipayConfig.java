package com.th.utils;
import java.io.FileWriter;
import java.io.IOException;

public class AlipayConfig {
    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016110300789073";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCDFqy79ABmlyFoK6+sGzwkyaLEecmWj05NxUnvUDkxbaaFiq1rSDoGAw0OlcUl6rzOl9slpEWNI4vXBHctafmC6hRskAYSXc5hLTJLYep67wTZPjM26JuO/C8sM6s9deOWH47KisLRzboPXoo7t9jHvZ7K+5P5NLD8uhoF/Lr8xFyrh3c01rlXV9CRnjtPU5tHB6SLab9kAEOP1IMDGcSke2vbdxHr/banyaTPIvmz5ahGQX+c42Dp4OzIduPyJdhtDU3HfkU8glmxHw2zoVUcjiAhXYrzxsPeiHlk8QEP/wiO9UYysIc3TzlWu7QtjxBC7UUDYpvm4vYEi9+dVaH/AgMBAAECggEAECPJjrajGf5/kXjXf0Q3Knxc+v3XsR7h0Dd7O+jPseNDPmvtoCjbZAQaJnKQPHPjXThbNg48tfzracTV+5RxsQmrDBbLeJVuJI7RA3EB0rL9ENZUre0Dwh4QnMubyMxsAf7OsOdWx0giIWRNdr7TBPy2wV0qQeOX6vXq4FPWnepN/2S49N2qwX/nkhzNv7aEXl6Q7WKgz7CA3udk7JBYwh1eU7nLlTWJMBZgb/+wur7JAvy36qcC9sDLLljvf9wf4vqswx4DV8Wx0o3alT7ldvjzLeWPac8zPF59sd/R7Vfs9kFPIsGij/+aJozST/1PEDr+orCzwn2NjM/w3G7DEQKBgQDJ+ffwBlulenlIy3PKXfkGcDN14alEyAP8jh6n/WKux+gzSSJxALyCDYmayQVv9lGxvpcqEDCWCGhb5HYrbgOCu2gfLE65JW2JcBGnBu9S5QDY25cY1AwucNqvnIP7YJJ0D0b+oaMzI+lbi79GhNSoFes1E79ev5zZ/zpg2LYpOQKBgQCmJsRHI5xOnV0l5dEaqZSTGQs31WPErieZCcKtr8qmfMoHtcPZ9ntpJ0GDfgFXiR4z1R4zr5FllgBrm5FduMezWMsqrcC2Y2HLZF/CI3awhpnONaqcqEJ3XZIOrlSZZeMZnehLb2XNmW/CoVyopPb4i509eTFDdfofoEEaX7689wKBgCJqTHwCqVUOBGKKAEXoM0mMjsg0VmJY7oz3LnQTceA0QfWTLaIxrO1AH0VXoWyNfSgbQKizL4QxFI8r45LM4R5CjqBZLWTQ4tQ7W1apkNgAG1/YMuvRHe4/3pNzyO1pbCsIe2Fh62wIX3fw3PvIIHAvo+9cWQxs++4NVkYwRwHJAoGAONma3xx4Jw4BCS6RVaYgiBRRmlERSgKs2dhVHEjB9iVMm0a+NJ35rAkaiXtbyi/IfcO2CclQ3olsq2UXYBIRgnQnT/sTrVRj6cv2V976hXwItF34epG37E+/4fYUTEhoZCN7kBQVqTuO2fGMWsfXGUPsEVSeXo7Hfnq+y99mzBECgYA3gQoalj7u7AtAaezDNnhYKsLCcfTU7Ak8N91i5OxCGFFkBCKtbEFOnxBXKeq99JbQ+krBYjTHSb3gxao2kqDgKDFmvAREU9aEb1BuMs5j+evZT/lSVH6JCZIlOtzEoUZwfsGD6AFh1kSkhhTpLjRjHD2gd302EVNmUF1L+mGphw==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqLeENj/lK06SV8fI65iX1sFouInNtTMYoTsLSE33uu4gm8VZDv/dQesyyGBXRNAm9mXIUCyHD+uIXjx0qmZ75q6bwSJkrgEFw/k9JHjXIwGEMvbk8/k07do1okiHPe9bKTbjNcIxVs5UtXJMyaggbazv4aHaryuROzEjzwr17SVceXfpr/xPdkGT2WoD4qw0r9F9U6rs1yecpLAEjMXksIwnhjGarVKU5tSg9HXiS8frL8yqB+TbIa4Uby0pRjvMRFUaen09PO5l6VgPhEhuS4dyqQ1fUesnVvFx/aZJeuHH6FDuPiRpZmLBwVTeGXX2IqkZNWFG+/tjOee/1yZlywIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://9w5v87.natappfree.cc/pay/notify_url";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://9w5v87.natappfree.cc/pay/return_url";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "D:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
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
