package com.wushiyii.boot.starter;

import com.wushiyii.core.annotation.Consumer;
import com.wushiyii.core.model.ProviderInfo;
import com.wushiyii.core.model.RpcConsumerBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyRpcClientFactoryPostProcessor implements BeanClassLoaderAware, BeanFactoryPostProcessor {

    private ClassLoader classLoader;
    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();




    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.beanFactory = configurableListableBeanFactory;
        generateMyRpcClientFactoryPostProcessor(beanFactory);
    }

    private void generateMyRpcClientFactoryPostProcessor(ConfigurableListableBeanFactory beanFactory) {

        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {

            //获取扫描到的所有beanDefinition / beanClassName
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();


            if (Objects.nonNull(beanClassName) && !beanClassName.isEmpty()) {
                //获取beanName对应的class
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);

                ReflectionUtils.doWithFields(clazz, this::parseConsumer);
            }
        }

        //注册beanDefinition
        beanDefinitionMap.forEach(((BeanDefinitionRegistry) beanFactory)::registerBeanDefinition);

    }

    private void parseConsumer(Field field) {
        //处理consumer注解
        Consumer consumer = AnnotationUtils.getAnnotation(field, Consumer.class);
        if (Objects.nonNull(consumer)) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RpcConsumerBean.class);
            beanDefinitionBuilder.setInitMethodName("init");
            beanDefinitionBuilder.addPropertyValue("interfaceClass", field.getType());

            ProviderInfo providerInfo = new ProviderInfo();
            providerInfo.setProviderClazz(field.getType());
            providerInfo.setProviderName(field.getType().getName());

            beanDefinitionBuilder.addPropertyValue("providerInfo", providerInfo);

            beanDefinitionMap.put(field.getName(), beanDefinitionBuilder.getBeanDefinition());
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

}
