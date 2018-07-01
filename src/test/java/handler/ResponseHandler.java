package handler;

import com.xing.context.ChannelHandlerContext;
import com.xing.handler.InBoundHandlerAdapter;

public class ResponseHandler extends InBoundHandlerAdapter {

    @Override
    public void channelRead(Object message, ChannelHandlerContext context) {
        context.writeFlush("Hello: " + (String) message + "!\n");
    }

}
