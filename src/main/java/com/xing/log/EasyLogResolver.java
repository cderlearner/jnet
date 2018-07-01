package com.xing.log;


import com.xing.log.api.ILog;
import com.xing.log.api.LogResolver;

public class EasyLogResolver implements LogResolver {
    @Override
    public ILog getLogger(Class<?> clazz) {
        return new EasyLogger(clazz);
    }
}
