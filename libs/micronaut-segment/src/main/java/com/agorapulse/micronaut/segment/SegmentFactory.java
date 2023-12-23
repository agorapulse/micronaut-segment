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

import com.agorapulse.micronaut.segment.util.Slf4jSegmentLog;
import com.segment.analytics.Analytics;
import com.segment.analytics.Callback;
import com.segment.analytics.MessageInterceptor;
import com.segment.analytics.MessageTransformer;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.env.Environment;
import okhttp3.OkHttpClient;


import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.MIN_PRIORITY;

@Factory
public class SegmentFactory {

    private static final String THREAD_NAME = "Analytics";

    @Bean(preDestroy = "shutdown")
    @Singleton
    @Requires(beans = SegmentConfiguration.class)
    public Analytics analytics(
        SegmentConfiguration configuration,
        List<MessageInterceptor> messageInterceptor,
        List<MessageTransformer> messageTransformers,
        List<Callback> callbacks,
        @Named("segment") OkHttpClient client,
        @Named("segment") ThreadFactory threadFactory,
        @Named("segmentNetworkExecutor") ExecutorService segmentNetworkExecutor
    ) {
        Analytics.Builder builder = Analytics.builder(configuration.getApiKey());

        messageInterceptor.forEach(builder::messageInterceptor);

        messageTransformers.forEach(builder::messageTransformer);

        callbacks.forEach(builder::callback);

        builder.log(new Slf4jSegmentLog())
            .threadFactory(threadFactory)
            .networkExecutor(segmentNetworkExecutor)
            .client(client);

        return builder.build();
    }

    @Bean
    @Singleton
    public SegmentService segmentService(
        @Nullable Analytics analytics,
        @Nullable SegmentConfiguration configuration,
        @Nullable @Named("segmentNetworkExecutor") ExecutorService segmentNetworkExecutor,
        Environment environment
    ) {
        if (analytics != null) {
            return new DefaultSegmentService(analytics, configuration, segmentNetworkExecutor, environment.getActiveNames().contains(Environment.FUNCTION));
        }
        return new NoOpSegmentService();
    }

    @Bean
    @Singleton
    @Named("segment")
    @Requires(beans = SegmentConfiguration.class)
    OkHttpClient defaultClient() {
        return new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build();
    }

    @Bean
    @Singleton
    @Named("segmentNetworkExecutor")
    @Requires(beans = SegmentConfiguration.class)
    ExecutorService defaultNetworkExecutor(@Named("segment") ThreadFactory threadFactory) {
        return Executors.newSingleThreadExecutor(threadFactory);
    }

    @Bean
    @Singleton
    @Named("segment")
    @Requires(beans = SegmentConfiguration.class)
    ThreadFactory defaultThreadFactory() {
        return r -> new Thread(() -> {
            Thread.currentThread().setPriority(MIN_PRIORITY);
            r.run();
        }, THREAD_NAME);
    }

}
