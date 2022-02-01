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

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public interface MessageBuilder<V extends MessageBuilder<V>> {


    /**
     * @deprecated use {@link #messageId(String)} instead.
     */
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

    /**
     * @deprecated use {@link #anonymousId(String)} instead
     */
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
