package com.wushiyii.core.serialize.impl;

import com.wushiyii.core.serialize.RpcSerializer;

public class ProtobufSerializer implements RpcSerializer {

    @Override
    public byte[] serializer(Object obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserializer(byte[] content, Class<T> clazz) {
        return null;
    }
}
