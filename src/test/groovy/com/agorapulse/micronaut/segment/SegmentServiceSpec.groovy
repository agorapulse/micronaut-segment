package com.agorapulse.micronaut.segment

import com.segment.analytics.Analytics
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import spock.lang.Ignore
import spock.lang.Specification

import javax.inject.Inject

@Ignore
@MicronautTest
class SegmentServiceSpec extends Specification {

    private static final String API_KEY = 'some-api-key'

    //@AutoCleanup ApplicationContext context

    @MockBean(Analytics)
    Analytics analytics() {
        Mock(Analytics)
    }

    @Inject
    SegmentService service

    void setup() {
        /*context = ApplicationContext
                .build('segment.apiKey': API_KEY)                                          // <5>
                .build()

        Analytics analytics = AnalyticsFactory.analytics()
        context.registerSingleton(Analytics, analytics)                                   // <6>
        context.start()

        service = context.getBean(SegmentService)*/
    }

    void "Flush"() {
        when:
        service.flush()

        then:
        1 * service.analytics.flush()
    }

}
