package com.wushiyii.core.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import com.wushiyii.core.serialize.RpcSerializer;
import com.wushiyii.core.serialize.SerializeType;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HessianSerializer implements RpcSerializer {

    @Override
    public String type() {
        return SerializeType.HESSIAN;
    }

    @SneakyThrows
    @Override
    public byte[] serializer(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(bos);
        hessianOutput.writeObject(obj);
        return bos.toByteArray();
    }

    @SneakyThrows
    @Override
    public <T> T deserializer(byte[] content, Class<T> clazz) {
        ByteArrayInputStream bis = new ByteArrayInputStream(content);
        HessianInput hessianInput = new HessianInput(bis);

        return (T) hessianInput.readObject(clazz);
    }
}
