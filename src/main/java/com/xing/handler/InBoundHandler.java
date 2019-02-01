package com.xing.handler;


/**
 * 进站事件处理器
 */
public interface InBoundHandler extends Handler {

    /**
     * 连接建立.
     *
     * @param context {@link ChannelHandlerContext} 处理器上下文
     */
    void channelActive(ChannelHandlerContext context);

    /**
     * 连接断开.
     *
     * @param context {@link ChannelHandlerContext} 处理器上下文
     */
    void channelInActive(ChannelHandlerContext context);

    /**
     * 数据读取.
     *
     * @param context {@link ChannelHandlerContext} 处理器上下文
     */
    void channelRead(Object message, ChannelHandlerContext context);
}
