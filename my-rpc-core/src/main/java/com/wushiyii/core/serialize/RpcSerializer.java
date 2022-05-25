package com.wushiyii.core.serialize;

public interface RpcSerializer {

    byte[] serializer(Object obj);

    <T> T deserializer(byte[] content, Class<T> clazz);

}
