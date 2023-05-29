package com.zspc.core.spring.aop.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Arrays;

/**
 * 切面类
 *
 * @author zhuansunpengcheng
 * @create 2019-06-27 2:57 PM
 **/
@Aspect
public class LogAspect {

    //抽取公共的切入点表达式
    @Pointcut("execution(* com.zspc.core.spring.aop.service.Calculator.*(..))")
    public void pointCut() {
    }

    //@Before在目标方法之前切入；切入点表达式（指定在哪个方法切入）
    @Before("pointCut()")
    public void logStart(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        System.out.println("前置通知运行。。。参数列表是：{" + Arrays.asList(args) + "}");
    }

    @After("pointCut()")
    public void logEnd(JoinPoint joinPoint) {
        System.out.println("后置通知运行。。。@After");
    }

    //JoinPoint一定要出现在参数表的第一位
    @AfterReturning(value = "pointCut()", returning = "result")
    public void logReturn(JoinPoint joinPoint, Object result) {
        System.out.println("返回通知运行。。。@AfterReturning:运行结果：{" + result + "}");
    }

    @AfterThrowing(value = "pointCut()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        System.out.println("" + joinPoint.getSignature().getName() + "异常。。。异常信息：{" + exception + "}");
    }


    @Around(value = "pointCut()")
    public Object logAround(ProceedingJoinPoint joinPoint){


        System.out.println("环绕通知开始");
        Object[] args = joinPoint.getArgs();
        Integer a = (Integer) args[0];
        Integer b = (Integer) args[1];

        Integer[] newArgs = new Integer[]{a+10,b+4};

        Object result = null;
        try {
            result = joinPoint.proceed(newArgs);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            result = 0;
        }

        System.out.println("环绕通知结束");
        return result;
    }



}
