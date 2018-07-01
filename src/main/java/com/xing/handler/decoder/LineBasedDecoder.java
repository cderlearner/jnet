package com.xing.handler.decoder;

public class LineBasedDecoder extends DelimiterBasedDecoder {

    public LineBasedDecoder() {
        //bug?
        //DelimiterBasedDecoder只支持ASCII码，而\r\n不是一个字符，所以暂且这样处理
        this('\n');
    }

    private LineBasedDecoder(char delimiter) {
        super(delimiter);
    }

}