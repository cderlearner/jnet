package com.xing.handler;

import java.util.ArrayList;
import java.util.List;

public class ChannelHandlerChain {
    private final List<InBoundHandler> inBoundHandlers = new ArrayList<>();
    private final List<OutBoundHandler> outBoundHandlers = new ArrayList<OutBoundHandler>() {{
        add(new DefaultOutBoundHandler());
    }};

    public void addHandler(Handler handler) {
        if (handler instanceof InBoundHandler) {
            inBoundHandlers.add((InBoundHandler) handler);
        } else if (handler instanceof OutBoundHandler) {
            outBoundHandlers.add((OutBoundHandler) handler);
        }
    }

    public List<InBoundHandler> copyInBoundHandlers() {
        return new ArrayList<>(inBoundHandlers);
    }

    public List<OutBoundHandler> copyOutBoundHandlers() {
        return new ArrayList<>(outBoundHandlers);
    }
}
