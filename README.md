# 流程
**1、使用BIO SOCKET接口实现基本的通信，采用JDK自带的序列化协议；**

JDK自带序列化协议方式通过对象实现Serializable接口的方式来实现，对象序列化之后，除了保存对象的信息，也会保存类的相关信息，比如序列化ID、类名。其中，序列化ID可以采用默认生成方式和自定义方式，自定义方式通过声明`private static final long serialVersionUID `的方式来定义序列化ID，自定义方式使用方便，鲁棒性更好，类中即使增加新的属性值也能够将对象正确序列化。

- [ ] 注意研究server端绑定不同的ip(localhost, 私网ip，公网ip)，client能否连接成功。

**2、使用JDK Dynamic Proxy来代理客户端向服务端发送消息**

- [x] 添加Cglib Dynamic Proxy方式来代理客户端。

JDK Dynamic Proxy只能代理实现接口的类（相当于代理类和实际类是兄弟类的关系），Cglib Dynamic Proxy可以代理未实现任何接口的类（Cglib通过射给你吃呢个一个实际类的子类拦截对实际类的调用，代理类是实际类的子类），Cglib无法代理声明未final类型的类和方法。

就两者效率而言，大部分情况下都是JDK动态代理更加优秀，随着JDK版本的升级，这个优势更加明显。

**3、使用zookeeper来管理注册和订阅服务**

- [x] client端在zookeeper上开启监听器
- [x] server端关闭服务后取消在zookeeper测的注册
- [ ] server端使用^C能够取消注册，但直接关闭命令窗口无法取消注册，需要解决这一问题

**4、client端开启负载均衡策略**

负载均衡策略包括随机策略法、哈希策略法、一致性哈希策略法、轮询法、最小连接法

- [x] 随机策略法
- [ ] 一致性哈希策略法
- [ ] 轮询法
- [ ] 最小连接法