package com.neta.teman.dawai.api.applications.commons;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegAuth;
import com.neta.teman.dawai.api.plugins.simpeg.models.SimpegResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@SuppressWarnings({"unchecked"})
public class BeanCopy {

    public static <T> T copy(Object source, Class<T> tClass) {
        T o = BeanUtils.instantiateClass(tClass);
        BeanUtils.copyProperties(source, o);
        return o;
    }

    public static void copy(Object destination, Object... sources) {
        copy(destination, new String[]{}, sources);
    }

    public static void copy(Object destination, String[] exclude, Object... sources) {
        for (Object source : sources) {
            BeanUtils.copyProperties(source, destination, exclude);
        }
    }

    // collection
    public static <E, T> List<T> copyCollection(List<E> source, Class<T> tClass) {
        return copyCollection(source, new ArrayList<T>(), tClass);
    }

    // collection
    public static <E, T> List<T> copyCollection(List<E> sources, List<T> destination, Class<T> tClass) {
        for (Object source : sources) {
            T o = BeanUtils.instantiateClass(tClass);
            BeanUtils.copyProperties(source, o);
            destination.add(o);
        }
        return destination;
    }

}
