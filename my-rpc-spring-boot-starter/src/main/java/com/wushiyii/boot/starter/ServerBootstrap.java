package com.wushiyii.boot.starter;

import com.google.common.base.Stopwatch;
import com.wushiyii.core.annotation.Provider;
import com.wushiyii.core.cache.ProviderInfoCache;
import com.wushiyii.core.model.ProviderInfo;
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
            registerAllProvider(context);

            //启动服务器
            startServer(context);
        }
    }

    private void registerAllProvider(ApplicationContext context) {
        log.info("registerAllProvider start ");
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<String, Object> beansMap = context.getBeansWithAnnotation(Provider.class);

        beansMap.forEach((name, bean) -> {
            Class<?> clazz = bean.getClass();

            Class<?> interfaceClazz = bean.getClass().getInterfaces()[0];
            String providerName = interfaceClazz.getName();
            ProviderInfo providerInfo = new ProviderInfo();
            providerInfo.setProviderName(providerName);
            providerInfo.setProviderClazz(clazz);

            registry.registerProvider(providerInfo);
            ProviderInfoCache.inject(providerName, providerInfo);
        });
        log.info("registerAllProvider stop, spend time={}", stopwatch.stop().elapsed());
    }


    private void startServer(ApplicationContext context) {
        new Thread(() -> new NettyServer(rpcConfig).start()).start();
    }


}
