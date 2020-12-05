package com.neta.teman.dawai.api.applications.commons;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class FamilyCommons {

    private Map<String, String> value() {
        Map<String, String> map = new HashMap<>();
        map.put("SAUDARAKANDUNG", "SAUDARA KANDUNG");
        map.put("IBUMERTUA", "IBU MERTUA");
        map.put("BAPAKMERTUA", "BAPAK MERTUA");
        map.put("IBUKANDUNG", "IBU KANDUNG");
        map.put("BAPAKKANDUNG", "BAPAK KANDUNG");
        return map;
    }

    public String alias(String key) {
        String value = value().get(key);
        if (Objects.isNull(value)) return key;
        return value;
    }


}
