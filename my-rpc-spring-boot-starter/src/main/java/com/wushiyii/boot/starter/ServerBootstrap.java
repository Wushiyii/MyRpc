package com.wushiyii.boot.starter;

import com.google.common.base.Stopwatch;
import com.wushiyii.core.annotation.Provider;
import com.wushiyii.core.model.MethodInfo;
import com.wushiyii.core.model.RpcConfig;
import com.wushiyii.core.netty.NettyServer;
import com.wushiyii.core.registry.Registry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class ServerBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private final RpcConfig rpcConfig;
    private final Registry registry;

    public ServerBootstrap(RpcConfig rpcConfig, Registry registry) {
        this.rpcConfig = rpcConfig;
        this.registry = registry;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        //root容器初始化时才注册接口
        if(Objects.isNull(event.getApplicationContext().getParent())){
            ApplicationContext context = event.getApplicationContext();

            //注册接口到注册中心
            registerAllMethod(context);

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

            registry.registerMethod(methodInfo);
        });
        log.info("registerAllMethod stop, spend time={}", stopwatch.stop().elapsed());
    }


    private void startServer(ApplicationContext context) {
        new Thread(() -> new NettyServer(rpcConfig).start()).start();
    }


}
