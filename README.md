# 流程

**1、使用BIO SOCKET接口实现基本的通信，采用JDK自带的序列化协议；**

JDK自带序列化协议方式通过对象实现Serializable接口的方式来实现，对象序列化之后，除了保存对象的信息，也会保存类的相关信息，比如序列化ID、类名。其中，序列化ID可以采用默认生成方式和自定义方式，自定义方式通过声明`private static final long serialVersionUID `的方式来定义序列化ID，自定义方式使用方便，鲁棒性更好，类中即使增加新的属性值也能够将对象正确序列化。

- [x] 注意研究server端绑定不同的ip(localhost, 私网ip，公网ip)，client能否连接成功。
server监听0.0.0.0时，client连接localhost、私网ip、公网ip都可以连接成功；
server监听127.0.0.1时，client连接localhost才能成功，私网ip、公网ip都无法连接成功；
server监听私网ip是，client连接私网ip、公网ip均能成功，连接127.0.0.1无法成功；
server无法监听公网ip

测试监听连接命令
```
# server
## 指定HOST
ncat -l HOST PORT
## 不指定HOST情况下，默认为0。0.0.0
ncat -l PORT
# client
ncat HOST PORT
```


**2、使用JDK Dynamic Proxy来代理客户端向服务端发送消息**

- [x] 添加Cglib Dynamic Proxy方式来代理客户端。

JDK Dynamic Proxy只能代理实现接口的类（相当于代理类和实际类是兄弟类的关系），Cglib Dynamic Proxy可以代理未实现任何接口的类（Cglib通过实现一个实际类的子类拦截对实际类的调用，代理类是实际类的子类），Cglib无法代理声明未final类型的类和方法。

就两者效率而言，大部分情况下都是JDK动态代理更加优秀，随着JDK版本的升级，这个优势更加明显。

**3、使用zookeeper来管理注册和订阅服务**

- [x] client端在zookeeper上开启监听器
- [x] server端关闭服务后取消在zookeeper测的注册
- [ ] server端使用^C能够取消注册，但直接关闭命令窗口无法取消注册，需要解决这一问题

**4、client端开启负载均衡策略**

负载均衡策略包括随机策略法、哈希策略法、一致性哈希策略法、轮询法、最小连接法

- [x] 随机策略法
- [x] 一致性哈希策略法
- [ ] 轮询法
- [ ] 最小连接法

**5、netty NIO替代BIO socket**

- [x] zookeeper管理策略
- [x] JDK CGLIB动态代理
- [x] 使用JDK自带的CompletableFuture异步获取结果，将获取结果的逻辑从NettyRpcClient移动到Proxy中来，如果需要应用到具体业务上可以考虑从Proxy层移动到上层应用中来
- [x] 使用JDK自带的序列化协议进行编码解码(ObjectEncoder, ObjectDecoder)
- [x] 在RpcClientTransport定义close，上层应用可自主决定client是否要关闭

**6、netty和BIO socket中使用自定义序列化**

- [x] Hessian序列化
- [x] Kyro序列化
- [x] Prototuff序列化
- [x] BIO socket实现上述序列化
- [ ] 目前在实现自定义序列化时，先把object序列化成bytes数组，然后再把bytes数组写进到ByteBuf中，考虑将两步变成一步，省略bytes再次写入ByteBuf过程，而是直接将object序列化后直接写进ByteBuf中

**7、netty client重用channel节省资源**
- [x] 在同一个进程中，client端连接同一个server时，发送多次消息时能够利用同一个channel，从而来防止重新建立链接，节省建链时间。

**8、netty引用gzip压缩传输对象大小**

**9、netty开启心跳机制**

TCP短连接和长连接是看是否一次连接发送完消息之后还是否会用到。如果是短连接，socket或者channel发送完一次消息后不会再使用，在client端或者server端（一般client端）
之后会手动close连接，或者说下次发送消息之后不会再使用该连接；如果是长连接，一般会是实现socket或者channel的复用，然后为了检测连接的可用性，会启用保活机制(keep-alive),
保活机制中会用到心跳机制。

我们可以通过两种方式实现心跳机制:使用 TCP 协议层面的 keepalive 机制;在应用层上实现自定义的心跳机制。

虽然在 TCP 协议层面上, 提供了 keepalive 保活机制, 但是使用它有几个缺点:
* 它不是 TCP 的标准协议, 并且是默认关闭的.
* TCP keepalive 机制依赖于操作系统的实现, 默认的 keepalive 心跳时间是 两个小时, 并且对 keepalive 的修改需要系统调用(或者修改系统配置), 灵活性不够.
* TCP keepalive 与 TCP 协议绑定, 因此如果需要更换为 UDP 协议时, keepalive 机制就失效了

netty应用层可以通过配置IdleStateHandler来实现心跳机制，IdleStateHandler开启定时任务，当任务超时会触发相应地读写事件，然后在IdleStateHandler的
userEventTriggered进行相应事件即可。

- [x] server端开启心跳机制
- [x] client端开启心跳机制

**10、添加自定义的通信协议**

    0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
    +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
    |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
    +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
    |                                                                                                       |
    |                                         body                                                          |
    |                                                                                                       |
    |                                        ... ...                                                        |
    +-------------------------------------------------------------------------------------------------------+

magic代表魔数`['t', 'r', 'p', 'c']`，version代表版本，full length代表消息字节长度，messageType代表消息类型（共有四种类型，Requeset、Response、HeartbeatRequest、HeartbeatResponse），
codec代表解编码类型、compress代表解压缩类型，RequestId代表请求Id，body代表传输的实体。

RpcMessage统一了传输过程中的对象，其`data`字段包含了messageType的四种类型

- [x] LengthFieldBasedFrameDecoder替代ByteToMessageDecoder解决TCP粘包、拆包问题

**11、添加注解来注册和消费服务**

- [ ] 在注解来注册和消费服务前提下，提供上层自定义配置host port方式