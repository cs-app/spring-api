package com.neta.spring.api.applications.commons;

public interface ResponseConverter<T, E> {

    T convert(E param);
}
