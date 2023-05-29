package com.zspc.core.spring.ext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhuansunpengcheng
 * @create 2019-07-10 2:49 PM
 **/
@Configuration
@ComponentScan(basePackages = "com.zspc.core.spring.ext")
public class MainConfig {


    @Bean
    public Person person(){
        return new Person();
    }

}
