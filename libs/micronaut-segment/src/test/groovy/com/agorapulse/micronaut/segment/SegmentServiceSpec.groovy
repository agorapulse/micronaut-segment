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
@SuppressWarnings(['Instanceof'])
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
                .build('segment.api-key': API_KEY)
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
            service.alias(PREVIOUS_ID, USER_ID)
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof AliasMessage
            message.userId() == USER_ID

        when:
            AliasMessage aliasMessage = message as AliasMessage
        then:
            aliasMessage
            aliasMessage.previousId() == PREVIOUS_ID
    }

    void 'alias user with timestamp'() {
        given:
            Instant now = Instant.now()

        when:
            service.alias(PREVIOUS_ID, USER_ID) {
                timestamp now
            }
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof AliasMessage
            message.userId() == USER_ID
            message.timestamp() == Date.from(now)

        when:
            AliasMessage aliasMessage = message as AliasMessage
        then:
            aliasMessage
            aliasMessage.previousId() == PREVIOUS_ID
    }

    void 'identify simple'() {
        when:
            service.identify(
                USER_ID
            )
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof IdentifyMessage
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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof IdentifyMessage
            message.userId() == USER_ID
        when:
            IdentifyMessage identifyMessage = message as IdentifyMessage
        then:
            identifyMessage
            identifyMessage.traits()
            identifyMessage.traits().category == CATEGORY
    }

    void 'identify with traits and timestamp'() {
        given:
            Instant now = Instant.now()

        when:
            service.identify(USER_ID) {
                traits(
                    category:CATEGORY,
                    nullable: null,
                )
                timestamp now
            }
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof IdentifyMessage
            message.userId() == USER_ID
            message.timestamp() == Date.from(now)

        when:
            IdentifyMessage identifyMessage = message as IdentifyMessage
        then:
            identifyMessage
            identifyMessage.traits()
            identifyMessage.traits().category == CATEGORY
    }

    void 'identify with google analytics id'() {
        given:
            Instant now = Instant.now()

        when:
            service.identify(USER_ID) {
                traits(
                    category: CATEGORY,
                    nullable: null
                )

                timestamp now

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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof IdentifyMessage
            message.userId() == USER_ID
            message.anonymousId() == ANONYMOUS_ID
            message.timestamp() == Date.from(now)
            message.integrations()
            message.integrations()['Google Analytics'] == [clientId: GOOGLE_ANALYTICS_ID]
            message.integrations()['Something Enabled'] == true
            message.integrations()['Something Disabled'] == false
            message.context()
            message.context().ip == IP_ADDRESS
            message.context().language == LANGUAGE
            message.context().userAgent == USER_AGENT
            message.context().Intercom == INTERCOM

        when:
            IdentifyMessage identifyMessage = message as IdentifyMessage
        then:
            identifyMessage
            identifyMessage.traits()
            identifyMessage.traits().category == CATEGORY
    }

    void 'page no category'() {
        when:
            service.page(
                USER_ID,
                NAME
            )
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof PageMessage
            message.userId() == USER_ID

        when:
            PageMessage pageMessage = message as PageMessage
        then:
            pageMessage
            pageMessage.name() == NAME
    }

    void 'page simple'() {
        when:
            service.page(
                USER_ID,
                NAME,
                CATEGORY
            )
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof PageMessage
            message.userId() == USER_ID

        when:
            PageMessage pageMessage = message as PageMessage
        then:
            pageMessage
            pageMessage.name() == NAME
            pageMessage.properties()
            pageMessage.properties().category == CATEGORY
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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof PageMessage
            message.userId() == USER_ID

        when:
            PageMessage pageMessage = message as PageMessage
        then:
            pageMessage
            pageMessage.name() == NAME
            pageMessage.properties()
            pageMessage.properties().category == CATEGORY
            pageMessage.properties().section == SECTION
    }

    void 'page with properties and timestamp'() {
        given:
            Instant now = Instant.now()

        when:
            service.page(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )
                timestamp now
            }
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof PageMessage
            message.userId() == USER_ID
            message.timestamp() == Date.from(now)

        when:
            PageMessage pageMessage = message as PageMessage
        then:
            pageMessage
            pageMessage.name() == NAME
            pageMessage.properties()
            pageMessage.properties().category == CATEGORY
            pageMessage.properties().section == SECTION
    }

    void 'page with google analytics id'() {
        given:
            Instant now = Instant.now()

        when:
            service.page(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )

                timestamp now

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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof PageMessage
            message.userId() == USER_ID
            message.anonymousId() == ANONYMOUS_ID
            message.timestamp() == Date.from(now)
            message.integrations()
            message.integrations()['Google Analytics'] == [clientId: GOOGLE_ANALYTICS_ID]
            message.integrations()['Something Enabled'] == true
            message.integrations()['Something Disabled'] == false
            message.context()
            message.context().ip == IP_ADDRESS
            message.context().language == LANGUAGE
            message.context().userAgent == USER_AGENT
            message.context().Intercom == INTERCOM

        when:
            PageMessage pageMessage = message as PageMessage
        then:
            pageMessage
            pageMessage.name() == NAME
            pageMessage.properties()
            pageMessage.properties().category == CATEGORY
            pageMessage.properties().section == SECTION
    }

    void 'screen no category'() {
        when:
            service.screen(
                USER_ID,
                NAME
            )
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof ScreenMessage
            message.userId() == USER_ID

        when:
            ScreenMessage screenMessage = message as ScreenMessage
        then:
            screenMessage
            screenMessage.name() == NAME
    }

    void 'screen simple'() {
        when:
            service.screen(
                USER_ID,
                NAME,
                CATEGORY
            )
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof ScreenMessage
            message.userId() == USER_ID

        when:
            ScreenMessage screenMessage = message as ScreenMessage
        then:
            screenMessage
            screenMessage.name() == NAME
            screenMessage.properties()
            screenMessage.properties().category == CATEGORY
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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof ScreenMessage
            message.userId() == USER_ID

        when:
            ScreenMessage screenMessage = message as ScreenMessage
        then:
            screenMessage
            screenMessage.name() == NAME
            screenMessage.properties()
            screenMessage.properties().category == CATEGORY
            screenMessage.properties().section == SECTION
    }

    void 'screen with properties and timestamp'() {
        given:
            Instant now = Instant.now()

        when:
            service.screen(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )
                timestamp now
            }
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof ScreenMessage
            message.userId() == USER_ID
            message.timestamp() == Date.from(now)

        when:
            ScreenMessage screenMessage = message as ScreenMessage
        then:
            screenMessage
            screenMessage.name() == NAME
            screenMessage.properties()
            screenMessage.properties().category == CATEGORY
            screenMessage.properties().section == SECTION
    }

    void 'screen with google analytics id'() {
        given:
            Instant now = Instant.now()

        when:
            service.screen(USER_ID, NAME) {
                properties(
                    category: CATEGORY,
                    section: SECTION,
                    nullable: null
                )

                timestamp now

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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof ScreenMessage
            message.userId() == USER_ID
            message.anonymousId() == ANONYMOUS_ID
            message.timestamp() == Date.from(now)
            message.integrations()
            message.integrations()['Google Analytics'] == [clientId: GOOGLE_ANALYTICS_ID]
            message.integrations()['Something Enabled'] == true
            message.integrations()['Something Disabled'] == false
            message.context()
            message.context().ip == IP_ADDRESS
            message.context().language == LANGUAGE
            message.context().userAgent == USER_AGENT
            message.context().Intercom == INTERCOM

        when:
            ScreenMessage screenMessage = message as ScreenMessage
        then:
            screenMessage
            screenMessage.name() == NAME
            screenMessage.properties()
            screenMessage.properties().category == CATEGORY
            screenMessage.properties().section == SECTION
    }

    void 'track simple'() {
        when:
            service.track(
                USER_ID,
                EVENT
            )
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof TrackMessage
            message.userId() == USER_ID

        when:
            TrackMessage trackMessage = message as TrackMessage
        then:
            trackMessage
            trackMessage.event() == EVENT
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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof TrackMessage
            message.userId() == USER_ID

        when:
            TrackMessage trackMessage = message as TrackMessage
        then:
            trackMessage
            trackMessage.event() == EVENT
            trackMessage.properties()
            trackMessage.properties().category == CATEGORY
            trackMessage.properties().section == SECTION
    }

    void 'track with properties and timestamp'() {
        given:
            Instant now = Instant.now()

        when:
            service.track(USER_ID, EVENT) {
                properties(
                    category: CATEGORY,
                    section : SECTION,
                    nullable: null
                )

                timestamp now
            }
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof TrackMessage
            message.userId() == USER_ID
            message.timestamp() == Date.from(now)

        when:
            TrackMessage trackMessage = message as TrackMessage
        then:
            trackMessage
            trackMessage.event() == EVENT
            trackMessage.properties()
            trackMessage.properties().category == CATEGORY
            trackMessage.properties().section == SECTION
    }

    void 'track with google analytics id'() {
        given:
            Instant now = Instant.now()

        when:
            service.track(USER_ID, EVENT) {
                properties(
                    category: CATEGORY,
                    section : SECTION,
                    nullable: null
                )

                timestamp now

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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof TrackMessage
            message.userId() == USER_ID
            message.anonymousId() == ANONYMOUS_ID
            message.timestamp() == Date.from(now)
            message.integrations()
            message.integrations()['Google Analytics'] == [clientId: GOOGLE_ANALYTICS_ID]
            message.integrations()['Something Enabled'] == true
            message.integrations()['Something Disabled'] == false
            message.context()
            message.context().ip == IP_ADDRESS
            message.context().language == LANGUAGE
            message.context().userAgent == USER_AGENT
            message.context().Intercom == INTERCOM

        when:
            TrackMessage trackMessage = message as TrackMessage
        then:
            trackMessage
            trackMessage.event() == EVENT
            trackMessage.properties()
            trackMessage.properties().category == CATEGORY
            trackMessage.properties().section == SECTION
    }

    void 'group simple'() {
        when:
            service.group(
                USER_ID,
                GROUP_ID
            )
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof GroupMessage
            message.userId() == USER_ID

        when:
            GroupMessage groupMessage = message as GroupMessage
        then:
            groupMessage.groupId() == GROUP_ID
    }

    void 'group with traits'() {
        when:
            service.group(USER_ID, GROUP_ID) {
                traits(
                    category: CATEGORY,
                    nullable: null
                )
            }
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof GroupMessage
            message.userId() == USER_ID

        when:
            GroupMessage groupMessage = message as GroupMessage
        then:
            groupMessage.groupId() == GROUP_ID
            groupMessage.traits()
            groupMessage.traits().category == CATEGORY
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
        then:
            queue
            queue.size() == 1

        when:
            Message message = queue.first()
        then:
            message
            message instanceof GroupMessage
            message.userId() == USER_ID
            message.context()
            message.context().ip == IP_ADDRESS
            message.context().language == null
            message.context().userAgent == USER_AGENT
            message.context().Intercom == INTERCOM

        when:
            GroupMessage groupMessage = message as GroupMessage
        then:
            groupMessage.groupId() == GROUP_ID
            groupMessage.traits()
            groupMessage.traits().category == CATEGORY
    }

}
