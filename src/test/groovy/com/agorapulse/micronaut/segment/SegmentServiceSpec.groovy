package com.agorapulse.micronaut.segment

import com.segment.analytics.Analytics
import groovy.transform.CompileDynamic
import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Specification

@CompileDynamic
class SegmentServiceSpec extends Specification {

    private static final String API_KEY = 'some-api-key'

    @AutoCleanup
    ApplicationContext context

    Analytics analytics = Mock()

    SegmentService service

    void setup() {
        context = ApplicationContext
                .build('segment.apiKey': API_KEY)
                .build()

        context.registerSingleton(Analytics, analytics)
        context.start()

        service = context.getBean(SegmentService)
    }

    void "Flush"() {
        when:
            service.flush()

        then:
            1 * analytics.flush()
    }

}
