# 流程
* **1、使用BIO SOCKET接口实现基本的通信，采用JDK自带的序列化协议；**

JDK自带序列化协议方式通过对象实现Serializable接口的方式来实现，对象序列化之后，除了保存对象的信息，也会保存类的相关信息，比如序列化ID、类名。其中，序列化ID可以采用默认生成方式和自定义方式，自定义方式通过声明`private static final long serialVersionUID `的方式来定义序列化ID，自定义方式使用方便，鲁棒性更好，类中即使增加新的属性值也能够将对象正确序列化。

- [ ] 注意研究server端绑定不同的ip(localhost, 私网ip，公网ip)，client能否连接成功。

* 2
