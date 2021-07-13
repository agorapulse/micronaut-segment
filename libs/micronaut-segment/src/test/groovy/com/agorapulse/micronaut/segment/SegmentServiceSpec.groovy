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
package com.agorapulse.micronaut.segment

import com.segment.analytics.Analytics
import groovy.transform.CompileDynamic
import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Specification

@CompileDynamic
class SegmentServiceSpec extends Specification {

    private static final String API_KEY = 'some-api-key'
    private static final String USER_ID = 'user-id'
    private static final String GOOGLE_ANALYTICS_ID = '123.456'

    @AutoCleanup
    ApplicationContext context

    Analytics analytics = Mock()

    SegmentService service

    void setup() {
        context = ApplicationContext
                .build('segment.api-key': API_KEY)
                .build()

        context.registerSingleton(Analytics, analytics)
        context.start()

        service = context.getBean(SegmentService)
    }

    void "flush"() {
        when:
            service.flush()

        then:
            1 * analytics.flush()
    }

    void 'send google analytics id'() {
        when:
            service.identify(USER_ID, [:], new Date(), [integrations: ['Google Analytics': GOOGLE_ANALYTICS_ID]])
        then:
            1 * analytics.enqueue(_)
    }

}
