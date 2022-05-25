package com.wushiyii.boot.starter;

import com.wushiyii.core.model.RpcConfig;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RpcConfig.class})
public class RpcClientAutoConfiguration {

    @Bean
    public static BeanFactoryPostProcessor rpcConsumerPostProcessor(){
        return new MyRpcClientFactoryPostProcessor();
    }

}
