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
import com.segment.analytics.messages.GroupMessage
import com.segment.analytics.messages.IdentifyMessage
import com.segment.analytics.messages.Message
import com.segment.analytics.messages.MessageBuilder
import com.segment.analytics.messages.PageMessage
import com.segment.analytics.messages.ScreenMessage
import com.segment.analytics.messages.TrackMessage
import groovy.transform.CompileDynamic
import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant

@CompileDynamic
@SuppressWarnings(['Instanceof', 'MethodCount'])
class SegmentServiceSpec extends Specification {

    private static final int INTERCOM = 123456
    private static final String API_KEY = 'some-api-key'
    private static final String PREVIOUS_ID = 'previous-id'
    private static final String NAME = 'name'
    private static final String USER_ID = 'user-id'
    private static final String GROUP_ID = 'group-id'
    private static final String ANONYMOUS_ID = 'anonymous-id'
    private static final String GOOGLE_ANALYTICS_ID = '123.456'
    private static final String IP_ADDRESS = '10.0.0.1'
    private static final String LANGUAGE = 'cs'
    private static final String USER_AGENT = 'Agorazilla'
    private static final String CATEGORY = 'VIP'
    private static final String SECTION = 'Header'
    private static final String EVENT = 'USER_LOGGED_IN'
    private static final Instant INSTANT_NOW = Instant.now()
    private static final Date DATE_NOW = Date.from(INSTANT_NOW)

    @Shared @AutoCleanup ApplicationContext context

    @Shared List<Message> queue = []
    @Shared boolean flushed

    @Shared Analytics analytics = Mock {
        enqueue(_ as  MessageBuilder) >> { MessageBuilder builder ->
            queue << builder.build()
        }

        flush() >> {
            flushed = true
        }
    }

    @Shared SegmentService service

    void setupSpec() {
        context = ApplicationContext
                .builder('segment.api-key': API_KEY)
                .build()

        context.registerSingleton(Analytics, analytics)
        context.start()

        service = context.getBean(SegmentService)
    }

    void cleanup() {
        queue.clear()
        flushed = false
    }

    void "flush"() {
        when:
            service.flush()

        then:
            flushed
    }

    void 'alias user'() {
        when:
            // tag::alias[]
            service.alias(PREVIOUS_ID, USER_ID)
            // end::alias[]
        and:
            AliasMessage message = readMessage(AliasMessage)
        then:
            message.userId() == USER_ID
            message.previousId() == PREVIOUS_ID
    }

    void 'alias user with timestamp'() {
        when:
            service.alias(PREVIOUS_ID, USER_ID) {
                timestamp INSTANT_NOW
            }
        and:
            AliasMessage message = readMessage(AliasMessage)
        then:
            message.userId() == USER_ID
            message.previousId() == PREVIOUS_ID
            message.timestamp() == DATE_NOW
    }

    void 'identify simple'() {
        when:
            service.identify(
                USER_ID
            )
        and:
            IdentifyMessage message = readMessage(IdentifyMessage)
        then:
            message.userId() == USER_ID
    }

    void 'identify with traits'() {
        when:
            service.identify(USER_ID) {
                traits(
                    category: CATEGORY,
                    nullable: null
                )
            }
        and:
            IdentifyMessage message = readMessage(IdentifyMessage)
        then:
            message.userId() == USER_ID

            assertCategory message.traits()
    }

    void 'identify with traits and timestamp'() {
        when:
            service.identify(USER_ID) {
                traits(
                    category:CATEGORY,
                    nullable: null,
                )
                timestamp INSTANT_NOW
            }
        and:
            IdentifyMessage message = readMessage(IdentifyMessage)
        then:
            message.userId() == USER_ID
            message.timestamp() == DATE_NOW

            assertCategory message.traits()
    }

    void 'identify with google analytics id'() {
        when:
            // tag::identify[]
            service.identify(USER_ID) {
                traits(
                    category: CATEGORY,
                    nullable: null
                )

                timestamp INSTANT_NOW

                anonymousId ANONYMOUS_ID

                integrationOptions 'Google Analytics', [clientId: GOOGLE_ANALYTICS_ID]

                enableIntegration 'Something Enabled', true
                enableIntegration 'Something Disabled', false

                context(
                    ip: IP_ADDRESS,
                    language: LANGUAGE,
                    userAgent: USER_AGENT,
                    Intercom: INTERCOM,
                    nullable: null
                )
            }
            // end::identify[]
        and:
            IdentifyMessage message = readMessage(IdentifyMessage)
        then:
            message.userId() == USER_ID
            message.timestamp() == DATE_NOW
            message.anonymousId() == ANONYMOUS_ID

            assertFullIntegrations message
            assertFullContext message
            assertCategory message.traits()
    }

    void 'page no category'() {
        when:
            service.page(
                USER_ID,
                NAME
            )
        and:
            PageMessage message = readMessage(PageMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME
    }

    void 'page simple'() {
        when:
            service.page(
                USER_ID,
                NAME,
                CATEGORY
            )
        and:
            PageMessage message = readMessage(PageMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME

            assertCategory message.properties()
    }

    void 'page with properties'() {
        when:
            service.page(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )
            }
        and:
            PageMessage message = readMessage(PageMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME

            assertCategoryAndSection message.properties()
    }

    void 'page with properties and timestamp'() {
        when:
            service.page(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )
                timestamp INSTANT_NOW
            }
        and:
            PageMessage message = readMessage(PageMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME
            message.timestamp() == DATE_NOW

            assertCategoryAndSection message.properties()
    }

    void 'page with google analytics id'() {
        when:
            // tag::page[]
            service.page(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )

                timestamp INSTANT_NOW

                anonymousId ANONYMOUS_ID

                integrationOptions 'Google Analytics', [clientId: GOOGLE_ANALYTICS_ID]

                enableIntegration 'Something Enabled', true
                enableIntegration 'Something Disabled', false

                context(
                    ip: IP_ADDRESS,
                    language: LANGUAGE,
                    userAgent: USER_AGENT,
                    Intercom: INTERCOM,
                    nullable: null
                )
            }
            // end::page[]
        and:
            PageMessage message = readMessage(PageMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME
            message.timestamp() == DATE_NOW
            message.anonymousId() == ANONYMOUS_ID

            assertFullIntegrations message
            assertFullContext message
            assertCategoryAndSection message.properties()
    }

    void 'screen no category'() {
        when:
            service.screen(
                USER_ID,
                NAME
            )
        and:
            ScreenMessage message = readMessage(ScreenMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME
    }

    void 'screen simple'() {
        when:
            service.screen(
                USER_ID,
                NAME,
                CATEGORY
            )
        and:
            ScreenMessage message = readMessage(ScreenMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME

            assertCategory message.properties()
    }

    void 'screen with properties'() {
        when:
            service.screen(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section : SECTION,
                    nullable: null
                )
            }
        and:
            ScreenMessage message = readMessage(ScreenMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME

            assertCategoryAndSection message.properties()
    }

    void 'screen with properties and timestamp'() {
        when:
            service.screen(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )
                timestamp INSTANT_NOW
            }
        and:
            ScreenMessage message = readMessage(ScreenMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME
            message.timestamp() == DATE_NOW

            assertCategoryAndSection message.properties()
    }

    void 'screen with google analytics id'() {
        when:
            // tag::screen[]
            service.screen(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )

                timestamp INSTANT_NOW

                anonymousId ANONYMOUS_ID

                integrationOptions 'Google Analytics', [clientId: GOOGLE_ANALYTICS_ID]

                enableIntegration 'Something Enabled', true
                enableIntegration 'Something Disabled', false

                context(
                    ip: IP_ADDRESS,
                    language: LANGUAGE,
                    userAgent: USER_AGENT,
                    Intercom: INTERCOM,
                    nullable: null
                )
            }
            // end::screen[]
        and:
            ScreenMessage message = readMessage(ScreenMessage)
        then:
            message.userId() == USER_ID
            message.name() == NAME
            message.timestamp() == DATE_NOW
            message.anonymousId() == ANONYMOUS_ID

            assertFullIntegrations message
            assertFullContext message
            assertCategoryAndSection message.properties()
    }

    void 'track simple'() {
        when:
            service.track(
                USER_ID,
                EVENT
            )
        and:
            TrackMessage message = readMessage(TrackMessage)
        then:
            message.userId() == USER_ID
            message.event() == EVENT
    }

    void 'track with properties'() {
        when:
            service.track(USER_ID, EVENT) {
                properties(
                    category: CATEGORY,
                    section : SECTION,
                    nullable: null
                )
            }
        and:
            TrackMessage message = readMessage(TrackMessage)
        then:
            message.userId() == USER_ID
            message.event() == EVENT

            assertCategoryAndSection message.properties()
    }

    void 'track with properties and timestamp'() {
        when:
            service.track(USER_ID, EVENT) {
                properties(
                    category: CATEGORY,
                    section : SECTION,
                    nullable: null
                )

                timestamp INSTANT_NOW
            }
        and:
            TrackMessage message = readMessage(TrackMessage)
        then:
            message.userId() == USER_ID
            message.event() == EVENT
            message.timestamp() == DATE_NOW

            assertCategoryAndSection message.properties()
    }

    void 'track with google analytics id'() {
        when:
            // tag::track[]
            service.track(USER_ID, EVENT) {
                properties(
                    category: CATEGORY,
                    section : SECTION,
                    nullable: null
                )

                timestamp INSTANT_NOW

                anonymousId ANONYMOUS_ID

                integrationOptions 'Google Analytics', [clientId: GOOGLE_ANALYTICS_ID]

                enableIntegration 'Something Enabled', true
                enableIntegration 'Something Disabled', false

                context(
                    ip: IP_ADDRESS,
                    language: LANGUAGE,
                    userAgent: USER_AGENT,
                    Intercom: INTERCOM,
                    nullable: null
                )
            }
            // end::track[]
        and:
            TrackMessage message = readMessage(TrackMessage)
        then:
            message.userId() == USER_ID
            message.event() == EVENT
            message.timestamp() == DATE_NOW
            message.anonymousId() == ANONYMOUS_ID

            assertFullIntegrations message
            assertFullContext message
            assertCategoryAndSection message.properties()
    }

    void 'group simple'() {
        when:
            service.group(
                USER_ID,
                GROUP_ID
            )
        and:
            GroupMessage message = readMessage(GroupMessage)
        then:
            message.userId() == USER_ID
            message.groupId() == GROUP_ID
    }

    void 'group with google analytics id'() {
        when:
            // tag::group[]
            service.group(USER_ID, GROUP_ID) {
                traits(
                    category: CATEGORY,
                    section : SECTION,
                    nullable: null
                )

                timestamp INSTANT_NOW

                anonymousId ANONYMOUS_ID

                integrationOptions 'Google Analytics', [clientId: GOOGLE_ANALYTICS_ID]

                enableIntegration 'Something Enabled', true
                enableIntegration 'Something Disabled', false

                context(
                    ip: IP_ADDRESS,
                    language: LANGUAGE,
                    userAgent: USER_AGENT,
                    Intercom: INTERCOM,
                    nullable: null
                )
            }
            // end::group[]
        and:
            GroupMessage message = readMessage(GroupMessage)
        then:
            message.userId() == USER_ID
            message.groupId() == GROUP_ID

            assertCategoryAndSection message.traits()
            assertFullContext message
            assertFullIntegrations message
    }

    void 'group with some null values'() {
        when:
            service.group(USER_ID, GROUP_ID) {
                traits(
                    category: CATEGORY,
                    nullable: null
                )
                context(
                    ip       : IP_ADDRESS,
                    language : null,
                    userAgent: USER_AGENT,
                    Intercom : INTERCOM,
                )
            }
        and:
            GroupMessage message = readMessage(GroupMessage)
        then:
            message.userId() == USER_ID
            message.groupId() == GROUP_ID

            assertCategory message.traits()
    }

    private static boolean assertCategory(Map<String, ?> properties) {
        assert properties
        assert properties.category == CATEGORY
        return true
    }

    private static boolean assertCategoryAndSection(Map<String, ?> properties) {
        assert properties
        assert properties.category == CATEGORY
        assert properties.section == SECTION
        return true
    }

    private boolean assertFullIntegrations(Message message) {
        with message, {
            integrations()
            integrations()['Google Analytics'] == [clientId: GOOGLE_ANALYTICS_ID]
            integrations()['Something Enabled'] == true
            integrations()['Something Disabled'] == false
        }

        return true
    }

    private boolean assertFullContext(Message message) {
        with message, {
            context()
            context().ip == IP_ADDRESS
            context().language == 'cs'
            context().userAgent == USER_AGENT
            context().Intercom == INTERCOM
        }

        return true
    }

    private <M> M readMessage(Class<M> type) {
        assert queue
        assert queue.size() == 1

        Message message = queue.first()

        assert message
        assert type.isInstance(message)

        return message as M
    }

}
