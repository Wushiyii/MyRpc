package com.wushiyii.core.serialize;

public interface RpcSerializer {

    String type();

    byte[] serializer(Object obj);

    <T> T deserializer(byte[] content, Class<T> clazz);

}
