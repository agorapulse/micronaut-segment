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
    public V integrationOptions(String key, String optionKey, Object optionValue) {
        return null;
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
            integrationsOptions.forEach((key, value) -> builder.integrationOptions(key, SafeMap.safe(value)));
        }
    }
}
