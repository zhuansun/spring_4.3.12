package com.zspc.core.spring.tx.test;

import com.zspc.core.spring.tx.config.MainConfig;
import com.zspc.core.spring.tx.service.PersonService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author zhuansunpengcheng
 * @create 2019-07-04 5:42 PM
 **/
public class TXTest {
    @Test
    public void test(){
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfig.class);
        PersonService personService = applicationContext.getBean(PersonService.class);
        personService.insert();
        applicationContext.close();
    }
}
