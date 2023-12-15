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
        if (value == null) {
            return self();
        }
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
