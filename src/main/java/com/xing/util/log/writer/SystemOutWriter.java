package com.xing.util.log.writer;

import com.xing.util.log.api.IWriter;

import java.io.PrintStream;

public enum SystemOutWriter implements IWriter {
    INSTANCE;
    @Override
    public void write(String message) {
        PrintStream out = System.out;
        out.println(message);
    }
}
