package com.xing.handler;


import com.xing.context.ChannelHandlerContext;

/**
 * 数据输出事件处理器.
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
