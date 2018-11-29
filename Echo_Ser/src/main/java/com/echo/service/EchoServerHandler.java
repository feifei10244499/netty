package com.echo.service;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 标示一个ChannelHandler   @Sharable
 * 可以被多
 * 个 Channel 安全地
 * 共享
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        /**
         * 将消息
         * 记录到
         * 控制台
         */
        System.out.println(
                "Server received: " + in.toString(CharsetUtil.UTF_8));
        /**
         * 将接收到的消息
         * 写给发送者，而
         * 不冲刷出站消息
         */
        ctx.write(in);
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        /**
         * 将未决消息冲刷到
         * 远程节点，并且关
         * 闭该 Channel
         */
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }


    /**
     * 如果不捕获异常，会发生什么呢
     * 每个 Channel 都拥有一个与之相关联的 ChannelPipeline，其持有一个 ChannelHandler 的
     * 实例链。在默认的情况下，ChannelHandler 会把对它的方法的调用转发给链中的下一个 ChannelHandler。因此，如果
     * exceptionCaught()方法没有被该链中的某处实现，那么所接收的异常将会被
     * 传递到 ChannelPipeline 的尾端并被记录。为此，你的应用程序应该提供至少有一个实现了
     * exceptionCaught()方法的 ChannelHandler。（6.4 节详细地讨论了异常处理）。
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) {
        /**
         * 打印异常
         * 栈跟踪
         */
        cause.printStackTrace();
        /**
         * 关闭该 栈跟踪
         * Channel
         */
        ctx.close();
    }
}
