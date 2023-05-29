package com.zspc.core.spring.ext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 我的beanFactory的后置通知
 *
 * @author zhuansunpengcheng
 * @create 2019-07-10 2:46 PM
 **/
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        System.out.println("我的beanFactory开始执行...");
        System.out.println("beanDefinition的count：---->" + beanFactory.getBeanDefinitionCount());
        System.out.println("beanDefinition:---->" + Arrays.asList(beanFactory.getBeanDefinitionNames()).toString());
        System.out.println("beanPostProcessor的count：--->" + beanFactory.getBeanPostProcessorCount());

    }
}
