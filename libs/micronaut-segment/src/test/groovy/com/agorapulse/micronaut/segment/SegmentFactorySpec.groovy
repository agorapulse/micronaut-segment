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
package com.agorapulse.micronaut.segment

import com.segment.analytics.Analytics
import com.segment.analytics.messages.AliasMessage
import groovy.transform.CompileDynamic
import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Specification

@CompileDynamic
@SuppressWarnings('Instanceof')
class SegmentFactorySpec extends Specification {

    @AutoCleanup ApplicationContext context

    void 'no-op service is present by default'() {
        when:
            context = ApplicationContext.run()
        then:
            context.getBean(SegmentService) instanceof NoOpSegmentService
    }

    @SuppressWarnings('GroovyAccessibility')
    void 'real service is present if api key is provided'() {
        when:
            context = ApplicationContext.run('example')
            Analytics analytics = context.getBean(Analytics)
            SegmentService service = context.getBean(SegmentService)
            LastMessageHolder holder = context.getBean(LastMessageHolder)
        then:
            noExceptionThrown()

            service instanceof DefaultSegmentService
            !holder.lastMessage

        when:
            analytics.enqueue(AliasMessage.builder('previous').userId('new'))

        then:
            holder.lastMessage
            holder.lastMessage.context().FromTransformer == 'Value'
    }

}
