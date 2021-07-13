package com.agorapulse.micronaut.segment.builder;

import java.util.Map;

public interface MessageBuilderWithTraits extends MessageBuilder<MessageBuilderWithTraits> {

    default MessageBuilderWithTraits traits(Map<String, ?> traits) {
        if (traits == null) {
            return self();
        }

        traits.forEach(this::traits);

        return self();
    }

    MessageBuilderWithTraits traits(String key, Object value);

}
