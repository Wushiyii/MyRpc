package com.wushiyii.core.registry;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.wushiyii.core.cache.NodeCache;
import com.wushiyii.core.model.ProviderInfo;
import com.wushiyii.core.model.NodeInfo;
import com.wushiyii.core.model.RpcConfig;
import com.wushiyii.core.util.MapUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

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
        NodeCache.init(this);
    }

    private final Set<String> subscribeSet = new CopyOnWriteArraySet<>();


    @Override
    @SneakyThrows
    public void registerProvider(ProviderInfo providerInfo) {
        log.info("registerProvider, providerInfo={}", providerInfo);

        NodeInfo nodeInfo = new NodeInfo(providerInfo, rpcConfig);

        Instance instance = new Instance();
        instance.setIp(nodeInfo.getNodeIp());
        instance.setPort(nodeInfo.getNodePort());
        instance.setServiceName(nodeInfo.getServiceName());
        instance.setInstanceId(nodeInfo.getServiceName());
        instance.setMetadata(MapUtil.objectToMap(nodeInfo));

        log.info("register nacos nameserver, serviceName={}, instance={}", nodeInfo.getServiceName(), instance);
        namingService.registerInstance(nodeInfo.getServiceName(), instance);
    }

    @SneakyThrows
    @Override
    public void subscribeProvider(String providerName) {
        if (!subscribeSet.contains(providerName)) {
            subscribeSet.add(providerName);

            namingService.subscribe(providerName, event -> {
                if (event instanceof NamingEvent) {
                    log.info("listened nacos naming event, event={}", JSON.toJSONString(event));
                    List<Instance> instances = ((NamingEvent) event).getInstances();
                    List<NodeInfo> nodeList = Optional.ofNullable(instances).orElse(new ArrayList<>())
                            .stream()
                            .map(ins -> MapUtil.mapToObject(ins.getMetadata(), NodeInfo.class))
                            .collect(Collectors.toList());
                    NodeCache.putNodeList(providerName, nodeList);
                }
            });
        }

    }

    @SneakyThrows
    @Override
    public List<NodeInfo> getNodeListByProviderName(String providerName) {
        log.info("getNodeListByProviderName providerName={}", providerName);
        List<Instance> instances = namingService.selectInstances(providerName, true);
        List<NodeInfo> nodeList = Optional.ofNullable(instances).orElse(new ArrayList<>())
                .stream()
                .map(ins -> MapUtil.mapToObject(ins.getMetadata(), NodeInfo.class))
                .collect(Collectors.toList());
        return nodeList;
    }
}
