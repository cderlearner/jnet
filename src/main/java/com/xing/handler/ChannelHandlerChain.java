package com.xing.handler;

import java.util.ArrayList;
import java.util.List;

public class ChannelHandlerChain {
    private final List<InBoundHandler> inBoundHandlers;
    private final List<OutBoundHandler> outBoundHandlers;

    public ChannelHandlerChain() {
        this.inBoundHandlers = new ArrayList<>();
        this.outBoundHandlers = new ArrayList<>();
        this.outBoundHandlers.add(new DefaultOutBoundHandler());
    }

    /**
     * 添加处理器
     */
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
