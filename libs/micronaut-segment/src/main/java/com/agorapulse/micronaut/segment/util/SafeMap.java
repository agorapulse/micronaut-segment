/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2023 Agorapulse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            .map(e -> {
                if (e.getValue() instanceof Map) {
                    e.setValue((V) safe((Map<?, ?>) e.getValue()));
                }
                return e;
            })
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
