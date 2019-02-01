package com.xing.util.log;


import com.xing.util.log.api.ILog;
import com.xing.util.log.api.LogResolver;

public class EasyLogResolver implements LogResolver {
    @Override
    public ILog getLogger(Class<?> clazz) {
        return new EasyLogger(clazz);
    }
}
