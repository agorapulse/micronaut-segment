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
package com.agorapulse.micronaut.segment;

import com.segment.analytics.Analytics;
import com.segment.analytics.MessageInterceptor;
import com.segment.analytics.MessageTransformer;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.util.List;

@Factory
public class SegmentFactory {

    @Bean
    @Singleton
    @Requires(beans = SegmentConfiguration.class)
    public Analytics analytics(
        SegmentConfiguration configuration,
        List<MessageInterceptor> messageInterceptor,
        List<MessageTransformer> messageTransformers
    ) {
        Analytics.Builder builder = Analytics.builder(configuration.getApiKey());
        messageInterceptor.forEach(builder::messageInterceptor);
        messageTransformers.forEach(builder::messageTransformer);
        return builder.build();
    }

    @Bean
    @Singleton
    public SegmentService segmentService(@Nullable Analytics analytics, @Nullable  SegmentConfiguration configuration) {
        if (analytics != null) {
            return new DefaultSegmentService(analytics, configuration);
        }
        return new NoOpSegmentService();
    }

}
