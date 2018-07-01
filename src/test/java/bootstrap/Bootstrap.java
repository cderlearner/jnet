package bootstrap;


import com.xing.handler.HandlerInitializer;
import com.xing.handler.decoder.DelimiterBasedDecoder;
import com.xing.handler.decoder.LengthFieldBasedDecoder;
import handler.SimpleInBoundHandler;
import org.junit.Test;
import com.xing.server.Server;
import com.xing.handler.decoder.StringDecoder;
import com.xing.handler.Handler;
import handler.ResponseHandler;
import com.xing.handler.encoder.StringEncoder;

import java.util.concurrent.TimeUnit;


public class Bootstrap {

    @Test
    public void lengthFieldBasedDecoder() {
        Server server = new Server();
        server.bind(8080).setHandlers(new HandlerInitializer() {
            @Override
            public Handler[] init() {
                return new Handler[] {new LengthFieldBasedDecoder(0, 4), new StringDecoder(), new SimpleInBoundHandler()};
            }
        }).start();
    }

    @Test
    public void delimiterBasedDecoder() throws InterruptedException {
        Server server = new Server();
        server.bind(8080).setHandlers(new HandlerInitializer() {
            @Override
            public Handler[] init() {
                return new Handler[] {new DelimiterBasedDecoder('a'), new StringDecoder(), new SimpleInBoundHandler()};
            }
        }).start();
        TimeUnit.MINUTES.sleep(10);
    }

    @Test
    public void response() throws InterruptedException {
        Server server = new Server();
        server.bind(8080).setHandlers(new StringDecoder(), new ResponseHandler(), new StringEncoder()).start();
        TimeUnit.MINUTES.sleep(10);
    }

}
