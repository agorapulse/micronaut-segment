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

import com.agorapulse.micronaut.segment.builder.*;
import com.segment.analytics.Analytics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultSegmentService implements SegmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSegmentService.class);

    private final Analytics analytics;
    private final SegmentConfiguration config;
    private final ExecutorService segmentNetworkExecutor;
    private final boolean blocking;

    public DefaultSegmentService(Analytics analytics, SegmentConfiguration config, ExecutorService segmentNetworkExecutor, boolean blocking) {
        this.analytics = analytics;
        this.config = config;
        this.segmentNetworkExecutor = segmentNetworkExecutor;
        this.blocking = blocking;
    }

    @Override
    public void flush() {
        analytics.flush();
        if (blocking) {
            try {
                LOGGER.debug("Waiting for messages being flushed");
                // give some time to enqueue the batch
                Thread.sleep(10);
                segmentNetworkExecutor.submit(() -> LOGGER.trace("Wating task has been executed")).get(10, TimeUnit.SECONDS);
                LOGGER.debug("Messages should be flushed now!");
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new IllegalStateException("Exception while waiting for flushing to happen");
            }
        }
    }

    @Override
    public void alias(String from, String to, Consumer<SimpleMessageBuilder> builder) {
        analytics.enqueue(builder(to, DefaultSimpleMessageBuilder::new, builder).buildAliasMessage(from));
    }

    @Override
    public void group(String userId, String groupId, Consumer<MessageBuilderWithTraits> builder) {
        analytics.enqueue(builder(userId, DefaultMessageBuilderWithTraits::new, builder).buildGroupMessage(groupId));
    }

    @Override
    public void identify(String userId, Consumer<MessageBuilderWithTraits> builder) {
        analytics.enqueue(builder(userId, DefaultMessageBuilderWithTraits::new, builder).buildIdentifyMessage());
    }

    @Override
    public void page(String userId, String name, Consumer<MessageBuilderWithProperties> builder) {
        analytics.enqueue(builder(userId, DefaultMessageBuilderWithProperties::new, builder).buildPageMessage(name));
    }

    @Override
    public void screen(String userId, String name, Consumer<MessageBuilderWithProperties> builder) {
        analytics.enqueue(builder(userId, DefaultMessageBuilderWithProperties::new, builder).buildScreenMessage(name));
    }

    @Override
    public void track(String userId, String event, Consumer<MessageBuilderWithProperties> builder) {
        analytics.enqueue(builder(userId, DefaultMessageBuilderWithProperties::new, builder).buildTrackMessage(event));
    }

    private <B extends MessageBuilder<?>> B builder(String userId, Supplier<B> creator, Consumer<? super B> builder) {
        B b = creator.get();
        b.userId(userId);
        if (!config.getOptions().isEmpty()) {
            SegmentService.LegacySupport.addOptions(b, config.getOptions(), null);
        }
        builder.accept(b);
        return b;
    }

}
