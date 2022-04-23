package com.smallmq.utils;

public class SmsUtils {


    // 生成四位随机数
    public static String getRandomNum() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }
    // 向手机发送短信
    public static String sendSms(String phone) {
        String num = getRandomNum();
        System.out.println("向手机" + phone + "发送短信：" + num);
        return num;
    }
}
