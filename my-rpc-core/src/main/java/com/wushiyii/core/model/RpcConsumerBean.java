package com.wushiyii.core.model;

import com.wushiyii.core.invocation.RpcInvocationHandler;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;

public class RpcConsumerBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;

    private ProviderInfo providerInfo;

    private Object object;


    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setProviderInfo(ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
    }

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void init() throws Exception {
        object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new ClientInvocationHandler(providerInfo));
    }



    private static class ClientInvocationHandler implements InvocationHandler {
        private final ProviderInfo providerInfo;

        public ClientInvocationHandler(ProviderInfo providerInfo) {
            this.providerInfo = providerInfo;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            RpcRequest request = new RpcRequest();
            request.setMethodName(method.getName());
            request.setProviderName(providerInfo.getProviderName());
            request.setParameterTypes(method.getParameterTypes());
            request.setParameters(args);
            request.setReturnType(method.getReturnType());
            request.setCommandId(UUID.randomUUID().toString());

            RpcResponse response = RpcInvocationHandler.invoke(request);
            if (Objects.isNull(response)) {
                throw new RuntimeException("Rpc can not get response, request=" + request);
            }
            if (Objects.nonNull(response.getEx())) {
                throw new RuntimeException(response.getEx());
            }

            return response.getResponseData();
        }
    }
}
