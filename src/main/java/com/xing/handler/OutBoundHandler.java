package com.xing.handler;


/**
 * 出站事件处理器.
 **/
public interface OutBoundHandler extends Handler {
    /**
     * 向客户端返回数据.
     *
     * @param message 数据(消息)
     * @param context {@link ChannelHandlerContext} 处理器上下文
     */
    void channelWrite(Object message, ChannelHandlerContext context);
}
