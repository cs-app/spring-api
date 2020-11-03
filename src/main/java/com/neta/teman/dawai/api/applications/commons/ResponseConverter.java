package com.neta.teman.dawai.api.applications.commons;

public interface ResponseConverter<T, E> {

    T convert(E param);
}
