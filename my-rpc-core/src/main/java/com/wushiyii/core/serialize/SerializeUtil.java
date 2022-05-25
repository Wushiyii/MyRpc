package com.wushiyii.core.serialize;

public class SerializeUtil {

    public static volatile SerializeUtil instance;

    private final RpcSerializer rpcSerializer;

    public SerializeUtil(String serializerName) {
        this.rpcSerializer = SerializerFactory.getSerializerByName(serializerName);
    }

    public static SerializeUtil getInstance(String serializerName) {
        if (null == instance) {
            synchronized (SerializeUtil.class) {
                if (null == instance) {
                    instance = new SerializeUtil(serializerName);
                }
            }
        }
        return instance;
    }



    public byte[] serialize(Object obj) {
        return rpcSerializer.serializer(obj);
    }

    public <T> T deserializer(byte[] content, Class<T> clazz){
        return rpcSerializer.deserializer(content, clazz);
    }


}
