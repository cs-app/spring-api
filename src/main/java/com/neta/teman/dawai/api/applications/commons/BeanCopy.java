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
public class BeanCopy {

    // private static ObjectMapper mapper = new ObjectMapper();

    public static void copy(Object destination, Object... sources) {
        copy(destination, new String[]{}, sources);
    }

    public static void copy(Object destination, String[] exclude, Object... sources) {
        for (Object source : sources) {
            BeanUtils.copyProperties(source, destination, exclude);
        }
    }

    // collection
    @SuppressWarnings({"unchecked"})
    public static <E, T> List<T> copyCollection(List<E> source) {
        return copyCollection(source, new ArrayList<T>());
    }

    public static <E, T> List<T> copyCollection(List<E> sources, List<T> destination) {
        ObjectMapper mapper = new ObjectMapper();
        for (Object source : sources) {
            T o = mapper.convertValue(source, new TypeReference<T>() {});
            destination.add(o);
        }
        return destination;
    }

}
