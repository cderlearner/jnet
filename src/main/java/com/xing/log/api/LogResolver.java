package com.xing.log.api;

public interface LogResolver {

    ILog getLogger(Class<?> clazz);
}
