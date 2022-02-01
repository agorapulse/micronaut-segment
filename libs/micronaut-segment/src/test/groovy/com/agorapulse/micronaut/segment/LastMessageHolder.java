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
import com.segment.analytics.MessageInterceptor;
import com.segment.analytics.messages.Message;

import javax.inject.Singleton;

/**
 * Keeps the reference to the last message.
 */
@Singleton
public class LastMessageHolder implements MessageInterceptor {

    private Message lastMessage;

    public Message getLastMessage() {
        return lastMessage;
    }

    @Override
    public Message intercept(Message message) {
        this.lastMessage = message;
        return message;
    }

}
// end::body[]
