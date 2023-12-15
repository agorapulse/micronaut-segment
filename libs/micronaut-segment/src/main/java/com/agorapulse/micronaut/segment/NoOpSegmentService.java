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
package com.agorapulse.micronaut.segment;

import com.agorapulse.micronaut.segment.builder.MessageBuilderWithProperties;
import com.agorapulse.micronaut.segment.builder.MessageBuilderWithTraits;
import com.agorapulse.micronaut.segment.builder.SimpleMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class NoOpSegmentService implements SegmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpSegmentService.class);

    public NoOpSegmentService() {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Segment API key configuration 'segment.api-key' not found, using no-op service!");
        }
    }

    @Override
    public void flush() {
        // noop
    }

    @Override
    public void alias(String from, String to, Consumer<SimpleMessageBuilder> builder) {
        // noop
    }

    @Override
    public void group(String userId, String groupId, Consumer<MessageBuilderWithTraits> builder) {
        // noop
    }

    @Override
    public void identify(String userId, Consumer<MessageBuilderWithTraits> builder) {
        // noop
    }

    @Override
    public void page(String userId, String name, Consumer<MessageBuilderWithProperties> builder) {
        // noop
    }

    @Override
    public void screen(String userId, String name, Consumer<MessageBuilderWithProperties> builder) {
        // noop
    }

    @Override
    public void track(String userId, String event, Consumer<MessageBuilderWithProperties> builder) {
        // noop
    }
}
