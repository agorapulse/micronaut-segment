/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2022 Agorapulse.
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
package com.agorapulse.micronaut.segment.builder;

import com.agorapulse.micronaut.segment.util.SafeMap;
import io.micronaut.core.util.StringUtils;

import java.util.*;

public abstract class DefaultMessageBuilder<V extends MessageBuilder<V>> implements MessageBuilder<V> {

    private String messageId;
    private Date timestamp;
    private String anonymousId;
    private String userId;

    private final Map<String, Object> context = new LinkedHashMap<>();
    private final Map<String, Boolean> integrationsEnabled = new LinkedHashMap<>();
    private final Map<String, Map<String, ?>> integrationsOptions = new LinkedHashMap<>();

    @Override
    public V messageId(String messageId) {
        this.messageId = messageId;
        return self();
    }

    @Override
    public V timestamp(Date timestamp) {
        this.timestamp = timestamp;
        return self();
    }

    @Override
    public V context(String key, Object value) {
        if (value == null) {
            return self();
        }
        this.context.put(key, value);
        return self();
    }

    @Override
    public V anonymousId(String anonymousId) {
        this.anonymousId = anonymousId;
        return self();
    }

    @Override
    public V userId(String userId) {
        this.userId = userId;
        return self();
    }

    @Override
    public V enableIntegration(String key, boolean enable) {
        integrationsEnabled.put(key, enable);
        return self();
    }

    @Override
    public V integrationOptions(String key, Map<String, ?> options) {
        integrationsOptions.put(key, SafeMap.safe(options));
        return self();
    }

    @Override
    @SuppressWarnings("unchecked")
    public V self() {
        return (V) this;
    }

    protected void buildMessage(com.segment.analytics.messages.MessageBuilder<?, ?> builder) {
        if (StringUtils.isNotEmpty(messageId)) {
            builder.messageId(messageId);
        }

        if (timestamp != null) {
            builder.timestamp(timestamp);
        }

        if (!context.isEmpty()) {
            builder.context(SafeMap.safe(context));
        }

        if (StringUtils.isNotEmpty(anonymousId)) {
            builder.anonymousId(anonymousId);
        }

        if (StringUtils.isNotEmpty(userId)) {
            builder.userId(userId);
        }

        if (!integrationsEnabled.isEmpty()) {
            integrationsEnabled.forEach(builder::enableIntegration);
        }

        if (!integrationsOptions.isEmpty()) {
            integrationsOptions.forEach((key, value) -> {
                Map<String, Object> safe = SafeMap.safe((Map<String, Object>) value);
                builder.integrationOptions(key, safe);
            });
        }
    }
}
