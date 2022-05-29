
package com.wushiyii.core.serialize;

import com.wushiyii.core.serialize.impl.HessianSerializer;
import com.wushiyii.core.serialize.impl.JavaSerializer;
import com.wushiyii.core.serialize.impl.ProtobufSerializer;

import java.util.Objects;
import java.util.ServiceLoader;

public class SerializerFactory {

    public static RpcSerializer getSerializerByName(String serializerName) {
        RpcSerializer serializer = getSerializerByNameInterval(serializerName);
        if (Objects.nonNull(serializer)) {
            return serializer;
        }

        //SPI 拉取
        ServiceLoader<RpcSerializer> rpcSerializers = ServiceLoader.load(RpcSerializer.class);
        for (RpcSerializer rpcSerializer : rpcSerializers) {
            if (Objects.equals(rpcSerializer.type(), serializerName)) {
                return rpcSerializer;
            }
        }

        //默认
        return new ProtobufSerializer();
    }

    private static RpcSerializer getSerializerByNameInterval(String serializerName) {
        switch (serializerName) {
            case SerializeType.JAVA:
                return new JavaSerializer();
            case SerializeType.HESSIAN:
                return new HessianSerializer();
            case SerializeType.PROTOBUF:
                return new ProtobufSerializer();
        }
        return null;
    }

        
}