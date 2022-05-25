package com.wushiyii.core.netty.handler;

import com.wushiyii.core.cache.MethodInfoCache;
import com.wushiyii.core.model.*;
import com.wushiyii.core.netty.protocol.MyRpcProtocol;
import com.wushiyii.core.serialize.SerializeUtil;
import com.wushiyii.core.util.SpringContextUtil;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class ServerProtocolRunnable implements Runnable {

    private final ChannelHandlerContext ctx;
    private final MyRpcProtocol msg;
    private final SerializeUtil serializeUtil;

    public ServerProtocolRunnable(ChannelHandlerContext ctx, Object msg, RpcConfig rpcConfig) {
        this.ctx = ctx;
        this.msg = (MyRpcProtocol) msg;
        this.serializeUtil = SerializeUtil.getInstance(rpcConfig.getSerialize());
    }


    @Override
    public void run() {
        try {

            //序列化rpc请求
            RpcRequest rpcRequest = serializeUtil.deserializer(msg.getContent(), RpcRequest.class);

            //反射调用
            RpcResponse rpcResponse = invoke0(rpcRequest);

            //序列化rpc返回值
            byte[] rpcResponseByte = serializeUtil.serialize(rpcResponse);
            MyRpcProtocol responseProtocol = new MyRpcProtocol(C.RESPONSE_PROTOCOL_TYPE, rpcResponseByte);

            //channel写入返回值
            ctx.writeAndFlush(responseProtocol);
        } catch (Exception e) {
            log.error("ServerProtocolRunnable occur error, msg={}", msg, e);
        }
    }

    private RpcResponse invoke0(RpcRequest rpcRequest) {
        RpcResponse rpcResponse = new RpcResponse();

        try {
            MethodInfo methodInfo = MethodInfoCache.getByMethodName(rpcRequest.getMethodName());
            Method method = methodInfo.getMethodClazz().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object responseData = method.invoke(SpringContextUtil.getBeanOfType(methodInfo.getMethodClazz()), rpcRequest.getParameters());

            rpcResponse.setCommandId(rpcRequest.getCommandId());
            rpcResponse.setResponseData(responseData);

        } catch (Exception e) {
            log.error("invoke server occur error, rpcRequest={}", rpcRequest, e);
            rpcResponse.setEx(e);
        }
        return rpcResponse;
    }


}
