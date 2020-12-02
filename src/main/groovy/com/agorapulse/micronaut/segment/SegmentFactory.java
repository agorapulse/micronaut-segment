package com.agorapulse.micronaut.segment;

import com.segment.analytics.Analytics;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

@Factory
public class SegmentFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentFactory.class);

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
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("Segment API key configuration 'segment.api-key' not found, using no-op service!");
        }
        return new NoOpSegmentService();
    }

}
