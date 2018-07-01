package com.xing.log.writer;

import com.xing.log.api.IWriter;

import java.io.PrintStream;

public enum SystemOutWriter implements IWriter {
    INSTANCE;
    @Override
    public void write(String message) {
        PrintStream out = System.out;
        out.println(message);
    }
}
