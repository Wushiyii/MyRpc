package com.wushiyii.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.wushiyii.model.MethodInfo;
import com.wushiyii.model.NodeInfo;
import com.wushiyii.model.RpcConfig;
import com.wushiyii.util.MapUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务发现：默认实现为Nacos
 */
@Slf4j
public class DefaultNacosRegistry implements Registry {

    private final RpcConfig rpcConfig;
    private final NamingService namingService;

    public DefaultNacosRegistry(RpcConfig rpcConfig) throws NacosException {
        this.rpcConfig = rpcConfig;
        this.namingService = NamingFactory.createNamingService(rpcConfig.getRegistryAddress());
    }


    @Override
    @SneakyThrows
    public void registerMethod(MethodInfo methodInfo) {
        log.info("registerMethod, methodInfo={}", methodInfo);

        NodeInfo nodeInfo = new NodeInfo(methodInfo, rpcConfig);

        Instance instance = new Instance();
        instance.setIp(nodeInfo.getNodeIp());
        instance.setPort(nodeInfo.getNodePort());
        instance.setHealthy(false);
        instance.setWeight(nodeInfo.getWeight());
        instance.setServiceName(nodeInfo.getMethodName());
        instance.setInstanceId(nodeInfo.getMethodName());
        instance.setMetadata(MapUtil.objectToMap(nodeInfo));

        namingService.registerInstance(nodeInfo.getMethodName(), instance);
    }
}
