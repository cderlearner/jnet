package com.xing.log;

import com.xing.log.api.ILog;

public class LogMain {

    public static void main(String[] args) {

        ILog log = LogManager.getLogger(LogMain.class);

        log.debug("---------------------------------");
    }
}
