
package com.wushiyii.core.serialize;

import com.wushiyii.core.serialize.impl.HessianSerializer;
import com.wushiyii.core.serialize.impl.JSONSerializer;
import com.wushiyii.core.serialize.impl.JavaSerializer;
import com.wushiyii.core.serialize.impl.ProtobufSerializer;

public class SerializerFactory {

    public static RpcSerializer getSerializerByName(String serializerName) {

        switch (serializerName) {
            case SerializeType.JAVA:
                return new JavaSerializer();
            case SerializeType.PROTOBUF:
                return new ProtobufSerializer();
            case SerializeType.HESSIAN:
                return new HessianSerializer();
            case SerializeType.JSON:
            default:
                return new JSONSerializer();
        }

    }

        
}