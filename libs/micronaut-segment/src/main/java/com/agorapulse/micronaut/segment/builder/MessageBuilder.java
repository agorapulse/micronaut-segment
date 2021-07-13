package com.agorapulse.micronaut.segment.builder;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface MessageBuilder<V extends MessageBuilder<V>> {

    @Deprecated
    default V messageId(UUID messageId) {
        if (messageId == null) {
            return self();
        }
        return messageId(messageId.toString());
    }

    V messageId(String messageId);

    V timestamp(Date timestamp);

    default V timestamp(TemporalAccessor timestamp) {
        return timestamp(Date.from(Instant.from(timestamp)));
    }

    default V context(Map<String, ?> context) {
        if (context == null) {
            return self();
        }

        context.forEach(this::context);

        return self();
    }

    V context(String key, Object value);

    @Deprecated
    default V anonymousId(UUID anonymousId) {
        if (anonymousId == null) {
            return self();
        }
        return anonymousId(anonymousId.toString());
    }

    V anonymousId(String anonymousId);

    V userId(String userId);

    V enableIntegration(String key, boolean enable);

    V integrationOptions(String key, Map<String, ?> options);

    default V integrationOptions(String key, String optionKey, Object optionValue) {
        return integrationOptions(key, Collections.singletonMap(optionKey, optionValue));
    }

    V self();
}
