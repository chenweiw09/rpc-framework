本项目基于springboot设计了一个简易的rpc框架，底层数据传输采用netty异步方式，服务的注册和发现给予zookeeper。
具体可以分为：
1、rpcclient: rpc客户段实现，其主要作用是对rpc接口做动态代理，调用借口方法实际上调用的是代理请求网络，病组装数据；

2、rpcserver: rpc服务端，主要作用是接受netty传输过来的请求类、方法、参数，根据请求方法的类名查找本地spring的bean，
   找到bean后调用制定的方法，病返回方法执行结果。

3、rpccommon: rpc公共服务，定义了注解，以及netty底层调用的封装和序列化实现。

4、rpcregistry：rpc注册模块，分为服务端注册和服务发现，其基本原理是监控特定的zk节点，病动态刷新服务提供者的地址信息。

5、rpctest:测试模块，用于测试RPC框架能否正常工作。