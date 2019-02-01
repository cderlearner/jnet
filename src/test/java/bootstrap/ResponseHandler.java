package bootstrap;

import com.xing.handler.ChannelHandlerContext;
import com.xing.handler.InBoundHandlerAdapter;

public class ResponseHandler extends InBoundHandlerAdapter {

    @Override
    public void channelRead(Object message, ChannelHandlerContext context) {
        context.writeFlush("Hello: " + (String) message + "!\n");
    }

}
