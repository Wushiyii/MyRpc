package com.wushiyii.core.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.wushiyii.core.serialize.RpcSerializer;

public class JSONSerializer implements RpcSerializer {

    @Override
    public byte[] serializer(Object obj) {
        return JSON.toJSONBytes(obj);
    }

    @Override
    public <T> T deserializer(byte[] content, Class<T> clazz) {
        return JSON.parseObject(content, clazz);
    }
}
