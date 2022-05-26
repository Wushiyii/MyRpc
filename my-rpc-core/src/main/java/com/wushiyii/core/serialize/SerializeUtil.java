package com.wushiyii.core.serialize;

import com.wushiyii.core.model.RpcConfig;

public class SerializeUtil {

    private static RpcSerializer rpcSerializer;

    public static void init(RpcConfig rpcConfig) {
        SerializeUtil.rpcSerializer = SerializerFactory.getSerializerByName(rpcConfig.getSerialize());
    }


    public static byte[] serialize(Object obj) {
        return rpcSerializer.serializer(obj);
    }

    public static  <T> T deserializer(byte[] content, Class<T> clazz){
        return rpcSerializer.deserializer(content, clazz);
    }


}
