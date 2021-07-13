package com.agorapulse.micronaut.segment.builder;

import com.agorapulse.micronaut.segment.util.SafeMap;
import com.segment.analytics.messages.PageMessage;
import com.segment.analytics.messages.ScreenMessage;
import com.segment.analytics.messages.TrackMessage;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultMessageBuilderWithProperties extends DefaultMessageBuilder<MessageBuilderWithProperties> implements MessageBuilderWithProperties {

    private final Map<String, Object> properties = new LinkedHashMap<>();

    @Override
    public MessageBuilderWithProperties properties(String key, Object value) {
        properties.put(key, value);
        return self();
    }

    public PageMessage.Builder buildPageMessage(String name) {
        PageMessage.Builder builder = PageMessage.builder(name);
        if (!properties.isEmpty()) {
            builder.properties(SafeMap.safe(properties));
        }
        buildMessage(builder);
        return builder;
    }

    public ScreenMessage.Builder buildScreenMessage(String name) {
        ScreenMessage.Builder builder = ScreenMessage.builder(name);
        if (!properties.isEmpty()) {
            builder.properties(SafeMap.safe(properties));
        }
        buildMessage(builder);
        return builder;
    }

    public TrackMessage.Builder buildTrackMessage(String event) {
        TrackMessage.Builder builder = TrackMessage.builder(event);
        if (!properties.isEmpty()) {
            builder.properties(SafeMap.safe(properties));
        }
        buildMessage(builder);
        return builder;
    }

}
