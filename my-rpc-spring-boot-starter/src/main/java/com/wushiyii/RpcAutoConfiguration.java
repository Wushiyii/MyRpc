package com.wushiyii;

import com.alibaba.nacos.api.exception.NacosException;
import com.wushiyii.model.RpcConfig;
import com.wushiyii.registry.DefaultNacosRegistry;
import com.wushiyii.registry.Registry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RpcConfig.class})
public class RpcAutoConfiguration {

    @Autowired
    private RpcConfig rpcConfig;

    @Bean
    public Registry CreateRegistry(@Autowired RpcConfig rpcConfig) throws NacosException {
        return new DefaultNacosRegistry(rpcConfig);
    }

}