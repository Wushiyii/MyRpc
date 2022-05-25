package com.wushiyii.boot.starter;

import com.alibaba.nacos.api.exception.NacosException;
import com.wushiyii.core.model.RpcConfig;
import com.wushiyii.core.registry.DefaultNacosRegistry;
import com.wushiyii.core.registry.Registry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RpcConfig.class})
public class RpcServerAutoConfiguration {

    @Autowired
    private RpcConfig rpcConfig;

    @Bean
    public Registry CreateRegistry(@Autowired RpcConfig rpcConfig) throws NacosException {
        return new DefaultNacosRegistry(rpcConfig);
    }

    @Bean
    public ServerBootstrap CreateServerBootstrap(@Autowired RpcConfig rpcConfig, @Autowired Registry registry) throws NacosException {
        return new ServerBootstrap(rpcConfig, registry);
    }

}