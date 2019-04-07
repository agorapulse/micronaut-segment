package com.agorapulse.micronaut.segment

import com.segment.analytics.Analytics
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value

import javax.inject.Singleton

@Factory
@Requires(classes = Analytics)
class AnalyticsFactory {

    @Bean
    @Singleton
    @Requires(property = "segment.apiKey")
    Analytics analytics(@Value("segment.apiKey") String apiKey) {
        return Analytics.builder(apiKey).build()
    }

}
