package bootstrap;

import com.xing.handler.HandlerInitializer;
import com.xing.handler.decoder.DelimiterBasedDecoder;
import com.xing.handler.decoder.LengthFieldBasedDecoder;
import com.xing.server.AcceptorServer;
import org.junit.Test;
import com.xing.handler.decoder.StringDecoder;
import com.xing.handler.Handler;
import com.xing.handler.encoder.StringEncoder;

import java.util.concurrent.TimeUnit;

public class Bootstrap {

    @Test
    public void lengthFieldBasedDecoder() {
        new AcceptorServer().bind(8080).withHandlers(new HandlerInitializer() {
            @Override
            public Handler[] init() {
                return new Handler[]{
                        new LengthFieldBasedDecoder(0, 4),
                        new StringDecoder(),
                        new SimpleInBoundHandler()};
            }
        }).start();
    }

    @Test
    public void delimiterBasedDecoder() throws InterruptedException {
        new AcceptorServer().bind(8080).withHandlers(new HandlerInitializer() {
            @Override
            public Handler[] init() {
                return new Handler[]{
                        new DelimiterBasedDecoder('a'),
                        new StringDecoder(),
                        new SimpleInBoundHandler()};
            }
        }).start();
        TimeUnit.MINUTES.sleep(10);
    }

    @Test
    public void response() throws InterruptedException {
        new AcceptorServer().bind(8080).withHandlers(
                new StringDecoder(),
                new ResponseHandler(),
                new StringEncoder()).start();
        TimeUnit.MINUTES.sleep(10);
    }

}
