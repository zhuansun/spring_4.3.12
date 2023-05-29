package com.zpsc.core.spring.aop.test;

import com.zspc.core.spring.aop.config.MainConfig;
import com.zspc.core.spring.aop.service.Calculator;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhuansunpengcheng
 * @create 2019-06-27 2:53 PM
 **/

public class AOPTest {
    @Test
    public void testAop() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
        Calculator calculator = (Calculator) applicationContext.getBean("calculator");
        int div = calculator.div(2, 1);
        System.out.println(div);
        applicationContext.close();
    }
}
