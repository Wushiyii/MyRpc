# MyRpc

MyRpc是一个基于Netty、Nacos、Protobuf实现的简单易懂的RPC框架。

### 特性
- 使用Nacos作为注册中心
- 多种序列化方式（Protobuf、Hessian、Jdk Serializer）
- 基于Netty实现TCP交互（基于长度解析）
- 负载均衡实现了随机、轮询算法
- 序列化、负载均衡支持SPI实现
- TCP报文协议简单、易实现

### Example
依赖：
```xml
<dependency>
  <groupId>com.wushiyii</groupId>
  <artifactId>my-rpc-spring-boot-starter</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

配置文件：
```properties
server.port=9001
my.rpc.registryAddress=127.0.0.1:8848
my.rpc.rpcPort=23747
my.rpc.serialize=protobuf
my.rpc.loadBalance=round_robin
```

服务提供：
```java
@Provider
public class UserFacadeImpl implements IUserFacade {

    @Override
    public UserDTO getUserById(Long id) {

        UserDTO dto = new UserDTO();
        dto.setUsername("小明");
        dto.setAge(20);
        dto.setId(id);
        return dto;
    }
}
```

服务消费：
```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Consumer
    private IUserFacade userFacade;

    @GetMapping("getUserById")
    public UserDTO getUserById(Long userId) {
        return userFacade.getUserById(userId);
    }
}
```

### 实现细节
RPC实现基本分为这几方面：

- 服务发布/导出：
扫描所有`@Provider`注解
```java
private void registerAllProvider(ApplicationContext context) {
    
    //扫描所有bean，取到服务发布的注解
    Map<String, Object> beansMap = context.getBeansWithAnnotation(Provider.class);

    beansMap.forEach((name, bean) -> {
        Class<?> clazz = bean.getClass();
    
        Class<?> interfaceClazz = bean.getClass().getInterfaces()[0];
        String providerName = interfaceClazz.getName();
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setProviderName(providerName);
        providerInfo.setProviderClazz(clazz);
    
        registry.registerProvider(providerInfo);
        ProviderInfoCache.inject(providerName, providerInfo);
    });
    
}
```
- 服务注册发现： 

发布服务到`Nacos`；通过服务名到`Nacos`拉取服务节点，订阅节点
```java
public void registerProvider(ProviderInfo providerInfo) {

    NodeInfo nodeInfo = new NodeInfo(providerInfo, rpcConfig);

    Instance instance = new Instance();
    instance.setIp(nodeInfo.getNodeIp());
    instance.setPort(nodeInfo.getNodePort());
    instance.setServiceName(nodeInfo.getServiceName());
    instance.setInstanceId(nodeInfo.getServiceName());
    instance.setMetadata(MapUtil.objectToMap(nodeInfo));
    
    namingService.registerInstance(nodeInfo.getServiceName(), instance);
}

@SneakyThrows
@Override
public List<NodeInfo> getNodeListByProviderName(String providerName) {
    List<Instance> instances = namingService.selectInstances(providerName, true);
    List<NodeInfo> nodeList = Optional.ofNullable(instances).orElse(new ArrayList<>())
    .stream()
    .map(ins -> MapUtil.mapToObject(ins.getMetadata(), NodeInfo.class))
    .collect(Collectors.toList());
    return nodeList;
}
```

- 消费调用代理实现(BeanDefinition + JDK proxy)
解析`@Consumer`注解，实现注册`BeanDefinition`
```java
private void parseConsumer(Field field) {
    //处理consumer注解
    Consumer consumer = AnnotationUtils.getAnnotation(field, Consumer.class);
    if (Objects.nonNull(consumer)) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RpcConsumerBean.class);
        beanDefinitionBuilder.setInitMethodName("init");
        beanDefinitionBuilder.addPropertyValue("interfaceClass", field.getType());

        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setProviderClazz(field.getType());
        providerInfo.setProviderName(field.getType().getName());

        beanDefinitionBuilder.addPropertyValue("providerInfo", providerInfo);

        beanDefinitionMap.put(field.getName(), beanDefinitionBuilder.getBeanDefinition());
    }
}
```
`Java Proxy`实现代理，封装RPC调用
```java
private static class ClientInvocationHandler implements InvocationHandler {
    private final ProviderInfo providerInfo;

    public ClientInvocationHandler(ProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();
        request.setMethodName(method.getName());
        request.setProviderName(providerInfo.getProviderName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        request.setCommandId(UUID.randomUUID().toString());

        RpcResponse response = RpcInvocationHandler.invoke(request);
        if (Objects.isNull(response)) {
            throw new RuntimeException("Rpc can not get response, request=" + request);
        }
        if (Objects.nonNull(response.getEx())) {
            throw new RuntimeException(response.getEx());
        }

        return SerializeUtil.deserializer(SerializeUtil.serialize(response.getResponseData()),method.getReturnType());
    }
}
```


- 报文设计与解码器

报文协议很简单，仅由2字节的长度字段与内容字段组成，长度字段代表内容字段总长度。
入下文: 00 0c为长度(12), 后续在解析12个长度即为报文内容(HELLO, WORLD)
```
 +-------------------------------------------------+----------------+
 | 00 0c 48 45 4c 4c 4f 2c 20 57 4f 52 4c 44       |..HELLO, WORLD  |
 +-------------------------------------------------+----------------+
```
解码器使用:`LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2)`
自动根据长度字段解码，并去除无用长度字段，解码后组装协议对象
```java
public class MyRpcDecoder extends LengthFieldBasedFrameDecoder {

    public MyRpcDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf msg = (ByteBuf) super.decode(ctx, in);

        byte[] bytes = new byte[msg.readableBytes()];

        msg.readBytes(bytes);

        return new MyRpcProtocol(bytes);
    }
}
```


- TCP交互实现

通过`Netty`实现同步调用，`CompletableFuture`实现同步调用。
```java
public RpcResponse invokeSync(RpcRequest request) {

    CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
    try {
        //请求窗口
        holdMap.put(request.getCommandId(), responseFuture);

        //序列化
        byte[] byteRequest = SerializeUtil.serialize(request);
        MyRpcProtocol protocol = new MyRpcProtocol(byteRequest);
        ctx.writeAndFlush(protocol);

        //CompletableFuture阻塞请求
        RpcResponse rpcResponse = responseFuture.get(10, TimeUnit.SECONDS);

        if (Objects.isNull(rpcResponse)) {
            throw new RuntimeException("call rpc timeout, address=" + nodeInfo.toAddress() + ", request=" + protocol);
        }
        return rpcResponse;

    } catch (Exception e) {
        log.error("call rpc occur error", e);
        throw new RuntimeException("call rpc occur error", e);
    } finally {
        holdMap.remove(request.getCommandId());
    }
}
```
Netty监听返回，通过临时Map获取对于请求，并组装`CompletableFutre`

```java
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    MyRpcProtocol responseMsg = (MyRpcProtocol) msg;
    RpcResponse rpcResponse = SerializeUtil.deserializer(responseMsg.getContent(), RpcResponse.class);
    CompletableFuture<RpcResponse> holdFuture = holdMap.get(rpcResponse.getCommandId());
    if (Objects.nonNull(holdFuture)) {
        holdFuture.complete(rpcResponse);
    }
}
```

