package com.xiaomi.chen.rpc.server;

import com.xiaomi.chen.rpc.common.annotation.RpcService;
import com.xiaomi.chen.rpc.common.constants.Constants;
import com.xiaomi.chen.rpc.common.constants.RpcException;
import com.xiaomi.chen.rpc.registry.NewServiceRegister;
import com.xiaomi.chen.rpc.registry.base.NodePort;
import com.xiaomi.chen.rpc.server.netty.ServerHandler;
import com.xiaomi.chen.rpc.server.netty.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/10
 * @description
 */
@Slf4j
public class RpcServerConfig implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;
    private Map<String, Object> handlerMap = new ConcurrentHashMap<>();

    private String registerAddress;
    private String serverAddress;

    private NewServiceRegister serviceRegistry;

    public RpcServerConfig(String registerAddress){
        this.registerAddress = registerAddress;
    }

    public RpcServerConfig(String serverAddress, String registerAddress) {
        this.serverAddress = serverAddress;
        this.registerAddress = registerAddress;
    }



    @Override
    public void afterPropertiesSet() {
        ServerHandler handler = new ServerHandler(handlerMap);
        ServerInitializer initializer = new ServerInitializer(handler);

        ServerBootstrap serverBootstrap = null;
        NioEventLoopGroup workerGroup = null, bossGroup = null;

        try {
            workerGroup = new NioEventLoopGroup();
            bossGroup  = new NioEventLoopGroup();
            serverBootstrap = new ServerBootstrap().group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(initializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            initBootStrap(serverBootstrap);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("init serverBootstrap exception",e);
            if(workerGroup != null){
                workerGroup.shutdownGracefully();
            }
            if(bossGroup != null){
                bossGroup.shutdownGracefully();
            }
        }


    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (!serviceBeanMap.isEmpty()) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                log.info("Loading service => {}",interfaceName);
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    private void initBootStrap(ServerBootstrap serverBootstrap) {

        if(StringUtils.isEmpty(serverAddress)){
            serverAddress = getServerAddress();
        }

        if(StringUtils.isEmpty(serverAddress)){
            throw new RpcException("can not get server address");
        }

        String[] array = serverAddress.split(":");
        String host = array[0];
        int port = Integer.parseInt(array[1]);
        try {
            serverBootstrap.bind(host, port).sync().channel().closeFuture();
            log.info("server start on port:{}",port);
            serviceRegistry =  new NewServiceRegister(registerAddress);
            // 注册服务地址
            registerService(host, port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getServerAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            log.debug("server ip is :{}",address.getHostAddress());
            return address.getHostAddress()+":"+ Constants.SERVER_PORT;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            log.error("",e);
            return null;
        }
    }

    private void registerService(String ip, int port){

        for(Map.Entry<String, Object> entry:handlerMap.entrySet()){
            NodePort nodePort = new NodePort(entry.getKey(), ip, port);
            serviceRegistry.register(nodePort);
        }
    }

    @PreDestroy
    public void closeZkConnect(){
        if(this.serviceRegistry != null){
            this.serviceRegistry.releaseConnection();
        }
    }
}
