package com.zspc.core.spring.aop.service;

/**
 * 计算器类
 *
 * @author zhuansunpengcheng
 * @create 2019-06-27 2:54 PM
 **/
public class Calculator {

    /**
     * 计算两个数的除法
     * @param a 除数
     * @param b 被除数
     * @return 商
     */
    public int div(int a, int b){
        System.out.println("开始计算-->除数:"+a+",被除数:"+b+".");
        return a/b;
    }

}
