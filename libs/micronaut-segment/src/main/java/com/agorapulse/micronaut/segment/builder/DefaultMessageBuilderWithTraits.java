/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2021 Agorapulse.
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
import com.segment.analytics.messages.GroupMessage;
import com.segment.analytics.messages.IdentifyMessage;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultMessageBuilderWithTraits extends DefaultMessageBuilder<MessageBuilderWithTraits> implements MessageBuilderWithTraits {

    private final Map<String, Object> traits = new LinkedHashMap<>();

    @Override
    public MessageBuilderWithTraits traits(String key, Object value) {
        if (value == null) {
            return self();
        }
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
