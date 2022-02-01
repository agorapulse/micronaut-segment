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
package com.agorapulse.micronaut.segment;

import com.segment.analytics.Analytics;
import com.segment.analytics.messages.AliasMessage;
import com.segment.analytics.messages.GroupMessage;
import com.segment.analytics.messages.IdentifyMessage;
import com.segment.analytics.messages.Message;
import com.segment.analytics.messages.MessageBuilder;
import com.segment.analytics.messages.PageMessage;
import com.segment.analytics.messages.ScreenMessage;
import com.segment.analytics.messages.TrackMessage;
import io.micronaut.context.ApplicationContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("FieldMayBeFinal")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SegmentServiceTest {

    public static final String API_KEY = "some-api-key";

    private static final int INTERCOM = 123456;
    private static final String PREVIOUS_ID = "previous-id";
    private static final String NAME = "name";
    private static final String USER_ID = "user-id";
    private static final String GROUP_ID = "group-id";
    private static final String ANONYMOUS_ID = "anonymous-id";
    private static final String MESSAGE_ID = "message-id";
    private static final String GOOGLE_ANALYTICS_ID = "123.456";
    private static final String IP_ADDRESS = "10.0.0.1";
    private static final String LANGUAGE = "cs";
    private static final String USER_AGENT = "Agorazilla";
    private static final String CATEGORY = "VIP";
    private static final String SECTION = "Header";
    private static final String EVENT = "USER_LOGGED_IN";
    private static final String DEFAULT_LANGUAGE = "sk";
    private static final String DEFAULT_USER_AGENT = "Safari";


    private static ApplicationContext context;
    private static boolean flushed;
    private static List<Message> queue = new ArrayList<>();

    private static SegmentService service;

    @BeforeAll
    static void setupTest() {
        Analytics analytics = mock(Analytics.class);

        doAnswer(invocation -> flushed = true).when(analytics).flush();
        doAnswer(invocation -> {
                Message message = invocation.getArgument(0, MessageBuilder.class).build();
                return queue.add(message);
            }
        ).when(analytics).enqueue(any());

        Map<String, Object> properties = new HashMap<>();
        properties.put("segment.api-key", API_KEY);
        properties.put("segment.options.language", DEFAULT_LANGUAGE);
        properties.put("segment.options.user-agent", DEFAULT_USER_AGENT);

        context = ApplicationContext.build(properties).build();
        context.registerSingleton(Analytics.class, analytics);
        context.start();

        service = context.getBean(SegmentService.class);
    }

    @AfterAll
    static void cleanupTest() {
        context.close();
    }

    @AfterEach
    void cleanup() {
        queue.clear();
        flushed = false;
    }

    @Test
    void client_flushed() {
        service.flush();

        assertTrue(flushed);
    }

    @Test
    void alias_user() {
        // tag::alias[]
        service.alias(PREVIOUS_ID, USER_ID);
        // end::alias[]

        AliasMessage message = assertMessage(AliasMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(PREVIOUS_ID, message.previousId())
        );
    }

    @Test
    void alias_user_with_timestamp() {
        Instant now = Instant.now();

        service.alias(PREVIOUS_ID, USER_ID, b -> b.timestamp(now));

        AliasMessage message = assertMessage(AliasMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(PREVIOUS_ID, message.previousId()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertDefaultContext(message)
        );
    }

    @Test
    void identify_simple() {
        service.identify(USER_ID);

        IdentifyMessage message = assertMessage(IdentifyMessage.class);

        assertEquals(USER_ID, message.userId());
    }

    @Test
    void identify_with_traits() {
        service.identify(USER_ID, b -> b
            .traits("category", CATEGORY)
            .traits("nullable", null)
        );

        IdentifyMessage message = assertMessage(IdentifyMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertCategory(message.traits())
        );
    }

    @Test
    void identify_with_traits_and_timestamp() {
        Instant now = Instant.now();

        service.identify(USER_ID, b -> b
            .traits("category", CATEGORY)
            .traits("nullable", null)
            .timestamp(now)
        );

        IdentifyMessage message = assertMessage(IdentifyMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategory(message.traits()),
            () -> assertDefaultContext(message)
        );
    }

    @Test
    void identify_with_google_analytics_id() {
        Instant now = Instant.now();

        // tag::identify[]
        service.identify(USER_ID, b -> b
            .traits("category", CATEGORY)
            .traits("nullable", null)
            .timestamp(now)
            .anonymousId(ANONYMOUS_ID)
            .messageId(MESSAGE_ID)
            .integrationOptions("Google Analytics", "clientId", GOOGLE_ANALYTICS_ID)
            .enableIntegration("Something Enabled", true)
            .enableIntegration("Something Disabled", false)
            .context("ip", IP_ADDRESS)
            .context("language", LANGUAGE)
            .context("userAgent", USER_AGENT)
            .context("Intercom", INTERCOM)
            .context("nullable", null)
        );
        // end::identify[]

        IdentifyMessage message = assertMessage(IdentifyMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(ANONYMOUS_ID, message.anonymousId()),
            () -> assertEquals(MESSAGE_ID, message.messageId()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertFullIntegrations(message),
            () -> assertFullContext(message),
            () -> assertCategory(message.traits())
        );
    }

    @Test
    void page_no_category() {
        service.page(USER_ID, NAME);

        PageMessage message = assertMessage(PageMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name())
        );
    }

    @Test
    void page_simple() {
        service.page(USER_ID, NAME, CATEGORY);

        PageMessage message = assertMessage(PageMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name()),
            () -> assertCategory(message.properties())
        );
    }

    @Test
    void page_with_properties() {
        service.page(USER_ID, NAME, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
        );

        PageMessage message = assertMessage(PageMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name()),
            () -> assertCategoryAndSection(message.properties())
        );
    }

    @Test
    void page_with_properties_and_timestamp() {
        Instant now = Instant.now();

        service.page(USER_ID, NAME, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
            .timestamp(now)
        );

        PageMessage message = assertMessage(PageMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategoryAndSection(message.properties())
        );
    }

    @Test
    void page_with_google_analytics_id() {
        Instant now = Instant.now();

        // tag::page[]
        service.page(USER_ID, NAME, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
            .timestamp(now)
            .anonymousId(ANONYMOUS_ID)
            .messageId(MESSAGE_ID)
            .integrationOptions("Google Analytics", "clientId", GOOGLE_ANALYTICS_ID)
            .enableIntegration("Something Enabled", true)
            .enableIntegration("Something Disabled", false)
            .context("ip", IP_ADDRESS)
            .context("language", LANGUAGE)
            .context("userAgent", USER_AGENT)
            .context("Intercom", INTERCOM)
            .context("nullable", null)
        );
        // end::page[]

        PageMessage message = assertMessage(PageMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategoryAndSection(message.properties()),
            () -> assertFullIntegrations(message),
            () -> assertFullContext(message)
        );
    }

    @Test
    void screen_no_category() {
        service.screen(USER_ID, NAME);

        ScreenMessage message = assertMessage(ScreenMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name())
        );
    }


    @Test
    void screen_simple() {
        service.screen(USER_ID, NAME, CATEGORY);

        ScreenMessage message = assertMessage(ScreenMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name()),
            () -> assertCategory(message.properties())
        );
    }

    @Test
    void screen_with_properties() {
        service.screen(USER_ID, NAME, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
        );

        ScreenMessage message = assertMessage(ScreenMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name()),
            () -> assertCategoryAndSection(message.properties())
        );
    }

    @Test
    void screen_with_properties_and_timestamp() {
        Instant now = Instant.now();

        service.screen(USER_ID, NAME, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
            .timestamp(now)
        );

        ScreenMessage message = assertMessage(ScreenMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategoryAndSection(message.properties())
        );
    }

    @Test
    void screen_with_google_analytics_id() {
        Instant now = Instant.now();

        // tag::screen[]
        service.screen(USER_ID, NAME, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
            .timestamp(now)
            .anonymousId(ANONYMOUS_ID)
            .messageId(MESSAGE_ID)
            .integrationOptions("Google Analytics", "clientId", GOOGLE_ANALYTICS_ID)
            .enableIntegration("Something Enabled", true)
            .enableIntegration("Something Disabled", false)
            .context("ip", IP_ADDRESS)
            .context("language", LANGUAGE)
            .context("userAgent", USER_AGENT)
            .context("Intercom", INTERCOM)
            .context("nullable", null)
        );
        // end::screen[]

        ScreenMessage message = assertMessage(ScreenMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(NAME, message.name()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategoryAndSection(message.properties()),
            () -> assertFullIntegrations(message),
            () -> assertFullContext(message)
        );
    }

    @Test
    void track_simple() {
        service.track(USER_ID, EVENT);

        TrackMessage message = assertMessage(TrackMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(EVENT, message.event())
        );
    }

    @Test
    void track_with_properties() {
        service.track(USER_ID, EVENT, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
        );

        TrackMessage message = assertMessage(TrackMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(EVENT, message.event()),
            () -> assertCategoryAndSection(message.properties())
        );
    }

    @Test
    void track_with_properties_and_timestamp() {
        Instant now = Instant.now();

        service.track(USER_ID, EVENT, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
            .timestamp(now)
        );

        TrackMessage message = assertMessage(TrackMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(EVENT, message.event()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategoryAndSection(message.properties())
        );
    }

    @Test
    void track_with_google_analytics_id() {
        Instant now = Instant.now();

        // tag::track[]
        service.track(USER_ID, EVENT, b -> b
            .properties("category", CATEGORY)
            .properties("section", SECTION)
            .properties("nullable", null)
            .timestamp(now)
            .anonymousId(ANONYMOUS_ID)
            .messageId(MESSAGE_ID)
            .integrationOptions("Google Analytics", "clientId", GOOGLE_ANALYTICS_ID)
            .enableIntegration("Something Enabled", true)
            .enableIntegration("Something Disabled", false)
            .context("ip", IP_ADDRESS)
            .context("language", LANGUAGE)
            .context("userAgent", USER_AGENT)
            .context("Intercom", INTERCOM)
            .context("nullable", null)
        );
        // end::track[]

        TrackMessage message = assertMessage(TrackMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(EVENT, message.event()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategoryAndSection(message.properties()),
            () -> assertFullIntegrations(message),
            () -> assertFullContext(message)
        );
    }

    @Test
    void group_simple() {
        service.group(USER_ID, GROUP_ID);

        GroupMessage message = assertMessage(GroupMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(GROUP_ID, message.groupId())
        );
    }

    @Test
    void group_with_properties() {
        service.group(USER_ID, GROUP_ID, b -> b
            .traits("category", CATEGORY)
            .traits("section", SECTION)
            .traits("nullable", null)
        );

        GroupMessage message = assertMessage(GroupMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(GROUP_ID, message.groupId()),
            () -> assertCategoryAndSection(message.traits())
        );
    }

    @Test
    void group_with_properties_and_timestamp() {
        Instant now = Instant.now();

        service.group(USER_ID, GROUP_ID, b -> b
            .traits("category", CATEGORY)
            .traits("section", SECTION)
            .traits("nullable", null)
            .timestamp(now)
        );

        GroupMessage message = assertMessage(GroupMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(GROUP_ID, message.groupId()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategoryAndSection(message.traits())
        );
    }

    @Test
    void group_with_google_analytics_id() {
        Instant now = Instant.now();

        // tag::group[]
        service.group(USER_ID, GROUP_ID, b -> b
            .traits("category", CATEGORY)
            .traits("section", SECTION)
            .traits("nullable", null)
            .timestamp(now)
            .anonymousId(ANONYMOUS_ID)
            .messageId(MESSAGE_ID)
            .integrationOptions("Google Analytics", "clientId", GOOGLE_ANALYTICS_ID)
            .enableIntegration("Something Enabled", true)
            .enableIntegration("Something Disabled", false)
            .context("ip", IP_ADDRESS)
            .context("language", LANGUAGE)
            .context("userAgent", USER_AGENT)
            .context("Intercom", INTERCOM)
            .context("nullable", null)
        );
        // end::group[]

        GroupMessage message = assertMessage(GroupMessage.class);

        assertAll(
            "message",
            () -> assertEquals(USER_ID, message.userId()),
            () -> assertEquals(GROUP_ID, message.groupId()),
            () -> assertEquals(Date.from(now), message.timestamp()),
            () -> assertCategoryAndSection(message.traits()),
            () -> assertFullIntegrations(message),
            () -> assertFullContext(message)
        );
    }

    private void assertCategory(Map<String, ?> properties) {
        assertNotNull(properties);
        assertEquals(CATEGORY, properties.get("category"));
    }

    private void assertCategoryAndSection(Map<String, ?> properties) {
        assertNotNull(properties);
        assertAll("properties",
            () -> assertEquals(CATEGORY, properties.get("category")),
            () -> assertEquals(SECTION, properties.get("section"))
        );
    }

    private void assertFullIntegrations(Message message) {
        Map<String, Object> integrations = message.integrations();
        assertNotNull(integrations);
        assertAll("integrations",
            () -> assertEquals(Collections.singletonMap("clientId", GOOGLE_ANALYTICS_ID), integrations.get("Google Analytics")),
            () -> assertEquals(Boolean.TRUE, integrations.get("Something Enabled"))
        );
    }

    private void assertDefaultContext(Message message) {
        Map<String, ?> context = message.context();
        assertNotNull(context);
        assertEquals(DEFAULT_LANGUAGE, context.get("language"));
        assertEquals(DEFAULT_USER_AGENT, context.get("userAgent"));
    }

    private void assertFullContext(Message message) {
        Map<String, ?> context = message.context();
        assertNotNull(context);
        assertAll("context",
            () -> assertEquals(IP_ADDRESS, context.get("ip")),
            () -> assertEquals(LANGUAGE, context.get("language")),
            () -> assertEquals(USER_AGENT, context.get("userAgent")),
            () -> assertEquals(INTERCOM, context.get("Intercom"))
        );
    }

    private <M extends Message> M assertMessage(Class<M> type) {
        Message message = queue.stream().findFirst().orElse(null);
        assertNotNull(message, "Message hasn't been not enqueued");
        assertTrue(type.isInstance(message), "Message is not type of " + type + " but " + message.getClass());
        return type.cast(message);
    }
}
