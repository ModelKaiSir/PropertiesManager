package org.qiukai.properties.manager.root;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.qiukai.properties.manager.controller.MainController;
import org.qiukai.properties.manager.notify.Notify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Aspect
@Component
public class ExceptionHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);


    @Pointcut("execution(* org..*.PropertiesManager.*(..))")
    public void cutPoint(){

    }

    @Around("cutPoint()")
    public Object runtimeHandler(ProceedingJoinPoint point){

        try{
            return point.proceed();
        }catch (Throwable e){
            LOGGER.error(this.getClass().getSimpleName(), e);
            Notify.info(String.format("错误：%s. 信息：%s.", e.getClass().getName(), e.getMessage()));
        }
        return null;
    }
}
