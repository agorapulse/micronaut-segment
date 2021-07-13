package com.agorapulse.micronaut.segment.builder;

import java.util.Map;

public interface MessageBuilderWithProperties extends MessageBuilder<MessageBuilderWithProperties> {

    default MessageBuilderWithProperties properties(Map<String, ?> properties) {
        if (properties == null) {
            return self();
        }

        properties.forEach(this::properties);

        return self();
    }

    MessageBuilderWithProperties properties(String key, Object value);

}
