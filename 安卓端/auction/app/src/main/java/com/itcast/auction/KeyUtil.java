package com.itcast.auction;

import java.util.Random;

/**
 * 生成唯一的主键——ID
 */
public class KeyUtil {

    /**
     * 生成订单ID = 时间 + 6位随机数
     */
    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;//6位随机数

        return System.currentTimeMillis() + String.valueOf(number);
    }

    /**
     * 生成商品ID = 时间 + 2位随机数
     */
    public static synchronized String genUniqueKeyProduct() {
        Random random = new Random();
        Integer number = random.nextInt(90) + 10;//2位随机数

        return System.currentTimeMillis() + String.valueOf(number);
    }
}
