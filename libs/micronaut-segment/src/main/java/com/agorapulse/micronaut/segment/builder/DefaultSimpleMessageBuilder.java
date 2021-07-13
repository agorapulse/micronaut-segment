package com.agorapulse.micronaut.segment.builder;

import com.segment.analytics.messages.AliasMessage;

public class DefaultSimpleMessageBuilder extends DefaultMessageBuilder<SimpleMessageBuilder> implements SimpleMessageBuilder {

    public AliasMessage.Builder buildAliasMessage(String from) {
        AliasMessage.Builder builder = AliasMessage.builder(from);
        buildMessage(builder);
        return builder;
    }
}
