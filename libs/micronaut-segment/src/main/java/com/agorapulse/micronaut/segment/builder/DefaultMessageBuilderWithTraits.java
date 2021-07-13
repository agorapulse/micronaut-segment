package com.agorapulse.micronaut.segment.builder;

import com.agorapulse.micronaut.segment.util.SafeMap;
import com.segment.analytics.messages.GroupMessage;
import com.segment.analytics.messages.IdentifyMessage;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultMessageBuilderWithTraits extends DefaultMessageBuilder<MessageBuilderWithTraits> implements MessageBuilderWithTraits {

    private final Map<String, Object> traits = new LinkedHashMap<>();

    @Override
    public MessageBuilderWithTraits traits(String key, Object value) {
        traits.put(key, value);
        return self();
    }

    public GroupMessage.Builder buildGroupMessage(String groupId) {
        GroupMessage.Builder builder = GroupMessage.builder(groupId);
        if (!traits.isEmpty()) {
            builder.traits(SafeMap.safe(traits));
        }
        buildMessage(builder);
        return builder;
    }

    public IdentifyMessage.Builder buildIdentifyMessage() {
        IdentifyMessage.Builder builder = IdentifyMessage.builder();
        if (!traits.isEmpty()) {
            builder.traits(SafeMap.safe(traits));
        }
        buildMessage(builder);
        return builder;
    }

}
