package com.xiaomi.chen.rpc.client.old.netty;


import com.xiaomi.chen.rpc.client.old.Client;
import com.xiaomi.chen.rpc.common.codec.RpcDecoder;
import com.xiaomi.chen.rpc.common.codec.RpcEncoder;
import com.xiaomi.chen.rpc.common.domain.RpcRequest;
import com.xiaomi.chen.rpc.common.domain.RpcResponse;
import com.xiaomi.chen.rpc.common.util.JsonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author chenwei
 * @version 1.0
 * @date 2018/10/9
 * @description
 */
public class NettyClient implements Client {

    private EventLoopGroup eventLoopGroup;

    private Channel channel;

    private ClientHandler clientHandler;

    private String host;

    private int port;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    @Override
    public RpcResponse send(RpcRequest request) {
        try {
            channel.writeAndFlush(request).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return clientHandler.getRpcResponse(request.getRequestId());
    }

    @Override
    public void connect(final InetSocketAddress socketAddress) {
        clientHandler = new ClientHandler();
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel){
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new LengthFieldBasedFrameDecoder(65535,0,4));
                        pipeline.addLast(new RpcEncoder(RpcRequest.class, new JsonSerializer()));
                        pipeline.addLast(new RpcDecoder(RpcResponse.class, new JsonSerializer()));
                        pipeline.addLast(clientHandler);
                    }
                });

        try {
            channel = bootstrap.connect(socketAddress).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(host, port);
    }

    @Override
    public void close() throws IOException {
        if(!eventLoopGroup.isShutdown()){
            eventLoopGroup.shutdownGracefully();
        }
        channel.closeFuture().syncUninterruptibly();
    }
}
