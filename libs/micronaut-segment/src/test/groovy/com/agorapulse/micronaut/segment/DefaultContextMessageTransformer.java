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
package com.agorapulse.micronaut.segment;

// tag::body[]
import com.segment.analytics.MessageTransformer;
import com.segment.analytics.messages.Message;
import com.segment.analytics.messages.MessageBuilder;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sets the default context value if missing.
 */
@Singleton
public class DefaultContextMessageTransformer implements MessageTransformer {

    @Override
    @SuppressWarnings("unchecked")
    public boolean transform(MessageBuilder builder) {
        Message message = builder.build();

        Map<String, ?> context = message.context();

        if (context == null) {
            builder.context(Collections.singletonMap("FromTransformer", "Value"));
            return true;
        }

        if (!context.containsKey("FromTransformer")) {
            Map<String, Object> newContext = new LinkedHashMap<>(context);
            newContext.put("FromTransformer", "Value");
            builder.context(newContext);
        }

        return true;


    }

}
// end::body[]
