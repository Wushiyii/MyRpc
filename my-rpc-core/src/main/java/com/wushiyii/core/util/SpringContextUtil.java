package com.wushiyii.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;


@Slf4j
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static Object getBeanByName(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public static <T> T getBeanOfType(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz){
        return applicationContext.getBeansOfType(clazz);
    }

    public static <T> T getBeanByName(String beanName, Class<T> beanClass) {
        if (applicationContext.containsBean(beanName)) {
            return applicationContext.getBean(beanName, beanClass);
        }
        return null;
    }
}
