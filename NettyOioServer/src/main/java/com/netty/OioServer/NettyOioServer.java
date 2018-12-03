package com.netty.OioServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

public class NettyOioServer {

    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
        EventLoopGroup group = new OioEventLoopGroup();
        try {
            /**
             * 创建Server- Bootstrap
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(group).
                    /**
                     * 使用 OioEventLoopGroup 以允许阻塞模式（旧的 I/O
                     */
                            channel(OioServerSocketChannel.class).
                    localAddress(new InetSocketAddress(port)).
                    /**
                     * 指定 Channel- Initializer，对于 每个已接受的 连接都调用它
                     */
                            childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            /**
                             * 添加一个Channel- InboundHandler- Adapter 以拦截和 处理事件
                             */
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(buf.duplicate()).
                                            addListener(
                                                    /**
                                                     * 将消息写到客户端，并添 加ChannelFutureListener， 以便消息一被写完就关闭
                                                     */
                                                    ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            /**
             * 绑定服务器 以接受连接
             */
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
