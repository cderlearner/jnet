package bootstrap;

import com.xing.handler.ChannelHandlerContext;
import com.xing.handler.InBoundHandlerAdapter;

public class SimpleInBoundHandler extends InBoundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext context) {
        System.out.println("channel active");
    }

    @Override
    public void channelInActive(ChannelHandlerContext context) {
        System.out.println("channel inActive");
    }

    @Override
    public void channelRead(Object message, ChannelHandlerContext context) {
        System.out.println(message.toString());
    }
}
