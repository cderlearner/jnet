package com.xing.log;

import com.xing.log.api.ILog;
import com.xing.log.api.LogResolver;

public class LogManager {
    private static LogResolver RESOLVER = new EasyLogResolver();

    public static void setLogResolver(LogResolver resolver) {
        LogManager.RESOLVER = resolver;
    }

    public static ILog getLogger(Class<?> clazz) {
        if (RESOLVER == null) {
            return NoopLogger.INSTANCE;
        }
        return LogManager.RESOLVER.getLogger(clazz);
    }
}
