package com.agorapulse.micronaut.segment;

import com.segment.analytics.Analytics;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;

import javax.inject.Singleton;

@Factory
@Requires(classes = Analytics.class)
public class SegmentFactory {

    @Bean
    @Singleton
    @Requires(beans = SegmentConfiguration.class)
    public Analytics analytics(SegmentConfiguration configuration) {
        return Analytics.builder(configuration.getApiKey()).build();
    }

    @Bean
    @Singleton
    @Requires(beans = {Analytics.class})
    public SegmentService segmentService(Analytics analytics, SegmentConfiguration configuration) {
        return new DefaultSegmentService(analytics, configuration);
    }

    @Bean
    @Singleton
    @Requires(missingBeans = {Analytics.class})
    public SegmentService noopSegmentService() {
        return new NoOpSegmentService();
    }

}
