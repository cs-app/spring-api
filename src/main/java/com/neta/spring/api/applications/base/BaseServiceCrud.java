package com.neta.spring.api.applications.base;

import org.springframework.data.domain.Page;

import java.util.List;

public interface BaseServiceCrud<T> {

    T createOrUpdate(T o);

    void delete(T o);

    long count();

    T findById(long id);

    List<T> findAll();

    Page<T> page(int page);

    Page<T> page(String search, int page);

}
