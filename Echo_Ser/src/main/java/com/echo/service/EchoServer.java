package com.echo.service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 这个示例使用了 NIO，因为得益于它的可扩展性和彻底的异步性，它是目前使用最广泛的传
 * 输。但是也可以使用一个不同的传输实现。如果你想要在自己的服务器中使用 OIO 传输，将需
 * 要指定 OioServerSocketChannel 和 OioEventLoopGroup。我们将在第 4 章中对传输进行
 * 更加详细的探讨。
 *
 *
 *      在2.处，你创建了一个 ServerBootstrap 实例。因为你正在使用的是 NIO 传输，所以你指定
 * 了 NioEventLoopGroup  1.来接受和处理新的连接，并且将 Channel 的类型指定为 NioServerSocketChannel  3.
 * 。在此之后，你将本地地址设置为一个具有选定端口的 InetSocketAddress4.
 * 。服务器将绑定到这个地址以监听新的连接请求。
 *      在 5处，你使用了一个特殊的类——ChannelInitializer。这是关键。当一个新的连接
 * 被接受时，一个新的子 Channel 将会被创建，而 ChannelInitializer 将会把一个你的
 * EchoServerHandler 的实例添加到该 Channel 的 ChannelPipeline 中。正如我们之前所
 * 解释的，这个 ChannelHandler 将会收到有关入站消息的通知。
 *      虽然 NIO 是可伸缩的，但是其适当的尤其是关于多线程处理的配置并不简单。Netty 的设计
 * 封装了大部分的复杂性，而且我们将在第 3 章中对相关的抽象（EventLoopGroup、SocketChannel
 * 和 ChannelInitializer）进行详细的讨论。
 *      接下来你绑定了服务器 6，并等待绑定完成。（对 sync()方法的调用将导致当前 Thread
 * 阻塞，一直到绑定操作完成为止）。在 7 .处，该应用程序将会阻塞等待直到服务器的 Channel
 * 关闭（因为你在 Channel 的 CloseFuture 上调用了 sync()方法）。然后，你将可以关闭
 * EventLoopGroup，并释放所有的资源，包括所有被创建的线程 8。
 */
public class EchoServer {

    private final int port;
    public EchoServer(int port) {
        this.port = port;
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println(
                    "Usage: " + EchoServer.class.getSimpleName() +
                            " <port>");
        }
        /**
         * 设置端口值（如果端口参数
         * 的格式不正确，则抛出一个
         * NumberFormatException）
         */
        int port = Integer.parseInt("8888");
        /**
         * 调用服务器
         * 的 start()方法
         */
        new EchoServer(port).start();
    }
    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        /**
         * 创建EventLoopGroup
         */
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            /**
             * 创建ServerBootstrap
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    /**
                     * 指定所使用的 NIO
                     * 传输 Channel
                     */
                    .channel(NioServerSocketChannel.class)
                    /**
                     * 使用指定的
                     * 端口设置套
                     * 接字地址
                     */
                    .localAddress(new InetSocketAddress(port))
                    /**
                     * 5.添加一个EchoServerHandler
                     * 到子Channel
                     * 的 ChannelPipeline
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });

            /**
             * 异步地绑定服务器；
             * 调用 sync()方法阻塞
             * 等待直到绑定完成
             */
            ChannelFuture f = b.bind().sync();
            /**
             * 获取 Channel 的
             * CloseFuture，并
             * 且阻塞当前线
             * 程直到它完成
             */
            f.channel().closeFuture().sync();
        } finally {
            /**
             * 关闭 EventLoopGroup， 程直到它完成
             * 释放所有的资源
             */
            group.shutdownGracefully().sync();
        }
    }
}
