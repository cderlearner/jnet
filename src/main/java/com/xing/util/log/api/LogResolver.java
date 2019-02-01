package com.xing.util.log.api;

public interface LogResolver {

    ILog getLogger(Class<?> clazz);
}
