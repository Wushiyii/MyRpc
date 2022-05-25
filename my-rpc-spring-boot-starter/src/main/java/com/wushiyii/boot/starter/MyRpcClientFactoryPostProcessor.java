package com.wushiyii.boot.starter;

import com.wushiyii.core.annotation.Consumer;
import com.wushiyii.core.model.MethodInfo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyRpcClientFactoryPostProcessor implements BeanClassLoaderAware, BeanFactoryPostProcessor, ApplicationContextAware {

    private ClassLoader classLoader;
    private ApplicationContext context;
    private ConfigurableListableBeanFactory beanFactory;

    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();




    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.beanFactory = configurableListableBeanFactory;
        generateMyRpcClientFactoryPostProcessor(beanFactory);
    }

    private void generateMyRpcClientFactoryPostProcessor(ConfigurableListableBeanFactory beanFactory) {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {

            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);

            String beanClassName = beanDefinition.getBeanClassName();
            if (Objects.nonNull(beanClassName) && !beanClassName.isEmpty()) {
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, classLoader);
                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback() {
                    @Override
                    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {

                        //处理consumer注解
                        Consumer consumer = field.getAnnotation(Consumer.class);
                        if (Objects.nonNull(consumer)) {
                            beanDefinitionMap.put(field.getName(), buildBeanDefinition(field, consumer));
                        }
                    }

                    private BeanDefinition buildBeanDefinition(Field field, Consumer consumer) {
                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Consumer.class);

                        Class<?> clazz = field.getType();
                        MethodInfo methodInfo = new MethodInfo();
                        methodInfo.setMethodClazz(clazz);
                        methodInfo.setMethodName(clazz.getName());

                        builder.addPropertyValue("methodInfo", methodInfo);

                        return builder.getBeanDefinition();
                    }
                });
            }
        }

        //注册beanDefinition
        beanDefinitionMap.forEach(((BeanDefinitionRegistry) beanFactory)::registerBeanDefinition);

    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }




}
