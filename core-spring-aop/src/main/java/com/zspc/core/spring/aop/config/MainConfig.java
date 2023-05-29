package com.zspc.core.spring.aop.config;

import com.zspc.core.spring.aop.service.Calculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author zhuansunpengcheng
 * @create 2019-06-27 2:51 PM
 **/
@Configuration
@EnableAspectJAutoProxy
public class MainConfig {


    @Bean
    public Calculator calculator() {
        return new Calculator();
    }


    @Bean
    public LogAspect logAspect() {
        return new LogAspect();
    }


}
