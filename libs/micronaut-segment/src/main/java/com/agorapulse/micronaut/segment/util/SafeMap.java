package com.agorapulse.micronaut.segment.util;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class SafeMap {

    private SafeMap() { }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> safe(Map<K, V> original) {
        if (original == null) {
            return Collections.emptyMap();
        }
        return original.entrySet()
            .stream()
            .filter(e -> e.getValue() != null)
            .peek(e -> {
                if (e.getValue() instanceof Map) {
                    e.setValue((V) safe((Map<?, ?>) e.getValue()));
                }
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
