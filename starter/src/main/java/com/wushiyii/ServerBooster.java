package com.wushiyii;

import com.google.common.base.Stopwatch;
import com.wushiyii.annotation.Provider;
import com.wushiyii.model.MethodInfo;
import com.wushiyii.model.RpcConfig;
import com.wushiyii.registry.Registry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class ServerBooster implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RpcConfig rpcConfig;
    @Autowired
    private Registry registry;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        //root容器初始化时才注册接口
        if(Objects.isNull(event.getApplicationContext().getParent())){
            ApplicationContext context = event.getApplicationContext();

            //注册接口到注册中心
            registerAllMethod(context);

            //订阅注册中心
            subscribe(context);

            //启动服务器
            startServer(context);
        }
    }

    private void registerAllMethod(ApplicationContext context) {
        log.info("registerAllMethod start ");
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Object> beansMap = context.getBeansWithAnnotation(Provider.class);

        beansMap.forEach((name, bean) -> {
            Class<?> clazz = bean.getClass();

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethodName(name);
            methodInfo.setMethodClazz(clazz);

            log.info("registerMethod, methodInfo={}", methodInfo);
            registry.registerMethod(methodInfo);
        });
        log.info("registerAllMethod stop, spend time={}", stopwatch.stop().elapsed());
    }


    private void subscribe(ApplicationContext context) {
        log.info("subscribe start ");
        Stopwatch stopwatch = Stopwatch.createStarted();




        log.info("subscribe stop, spend time={}", stopwatch.stop().elapsed());

    }

    private void startServer(ApplicationContext context) {

    }


}
