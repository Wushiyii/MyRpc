package com.wushiyii.core.serialize.impl;

import com.wushiyii.core.serialize.RpcSerializer;
import com.wushiyii.core.serialize.SerializeType;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaSerializer implements RpcSerializer {

    @Override
    public String type() {
        return SerializeType.JAVA;
    }

    @SneakyThrows
    @Override
    public byte[] serializer(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        return bos.toByteArray();
    }

    @SneakyThrows
    @Override
    public <T> T deserializer(byte[] content, Class<T> clazz) {

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
        return (T)inputStream.readObject();
    }
}
