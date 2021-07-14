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
package com.agorapulse.micronaut.segment;

import com.agorapulse.micronaut.segment.builder.MessageBuilder;
import com.agorapulse.micronaut.segment.builder.MessageBuilderWithProperties;
import com.agorapulse.micronaut.segment.builder.MessageBuilderWithTraits;
import com.agorapulse.micronaut.segment.builder.SimpleMessageBuilder;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface SegmentService {


    /**
     * Flushes the current contents of the queue
     */
    void flush();

    /**
     * Alias method lets you merge two user profiles, including their actions and traits.
     *
     * @param from the user's id after they are logged in. It's the same id as
     *             which you would recognize a signed-in user in your system.
     * @param to   new user id
     */
    default void alias(String from, String to) {
        alias(from, to, b -> { });
    }

    /**
     * Alias method lets you merge two user profiles, including their actions and traits.
     *
     * @param from    the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param to      new user id
     * @param builder Consumer of the builder for the additional configuration
     */
    void alias(String from, String to, Consumer<SimpleMessageBuilder> builder);

    /**
     * Group method lets you associate a user with a group.
     *
     * @param userId  the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param groupId The ID for this group in your database.
     */
    default void group(String userId, String groupId) {
        group(userId, groupId, b -> { });
    }

    /**
     * Group method lets you associate a user with a group.
     *
     * @param userId  the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param groupId The ID for this group in your database.
     * @param builder Consumer of the builder for the additional configuration
     */
    void group(String userId, String groupId, Consumer<MessageBuilderWithTraits> builder);

    /**
     * Group method lets you associate a user with a group.
     *
     * @param userId  the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param groupId The ID for this group in your database.
     * @param traits  A dictionary of traits you know about the user. Things like: email,
     *                name or friends.
     * @deprecated use {@link #group(String, String, Consumer)} instead
     */
    @Deprecated
    default void group(String userId, String groupId, Map<String, ?> traits) {
        group(userId, groupId, b -> b.traits(traits));
    }

    /**
     * Group method lets you associate a user with a group.
     *
     * @param userId  the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param groupId The ID for this group in your database.
     * @param traits  A dictionary of traits you know about the user. Things like: email,
     *                name or friends.
     * @param options a custom object which allows you to set a timestamp,
     *                an anonymous cookie id, or enable specific integrations.
     * @deprecated use {@link #group(String, String, Consumer)} instead
     */
    @Deprecated
    default void group(String userId, String groupId, Map<String, ?> traits, Map<String, ?> options) {
        group(userId, groupId, b -> {
            b.traits(traits);
            LegacySupport.addOptions(b, options, null);
        });
    }

    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId The ID for this user in your database.
     */
    default void identify(String userId) {
        identify(userId, b -> { });
    }

    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId  The ID for this user in your database.
     * @param builder Consumer of the builder for the additional configuration
     */
    void identify(String userId, Consumer<MessageBuilderWithTraits> builder);


    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId The ID for this user in your database.
     * @param traits A dictionary of traits you know about the user. Things like: email,
     *               name or friends.
     * @deprecated user {@link #identify(String, Consumer)} instead
     */
    @Deprecated
    default void identify(String userId, Map<String, ?> traits) {
        identify(userId, b -> b.traits(traits));
    }


    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId    The ID for this user in your database.
     * @param traits    A dictionary of traits you know about the user. Things like: email,
     *                  name or friends.
     * @param timestamp A DateTime representing when the identify took place.
     *                  If the identify just happened, leave it blank and we'll use
     *                  the server's time. If you are importing data from the past,
     *                  make sure you provide this argument.
     * @deprecated user {@link #identify(String, Consumer)} instead
     */
    @Deprecated
    default void identify(String userId, Map<String, ?> traits, Date timestamp) {
        identify(userId, b -> b.traits(traits).timestamp(timestamp));
    }

    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId    The ID for this user in your database.
     * @param traits    A dictionary of traits you know about the user. Things like: email,
     *                  name or friends.
     * @param timestamp A DateTime representing when the identify took place.
     *                  If the identify just happened, leave it blank and we'll use
     *                  the server's time. If you are importing data from the past,
     *                  make sure you provide this argument.
     * @param options   A custom object which allows you to set a timestamp, an anonymous cookie id,
     *                  or enable specific integrations.
     * @deprecated user {@link #identify(String, Consumer)} instead
     */
    @Deprecated
    default void identify(String userId, Map<String, ?> traits, Date timestamp, Map<String, ?> options) {
        identify(userId, b -> {
            b.traits(traits);
            LegacySupport.addOptions(b, options, timestamp);
        });
    }

    /**
     * Page method lets you record webpage visits from your web servers.
     *
     * @param userId The ID for this user in your database.
     * @param name   The webpage name you’re tracking. We recommend human-readable
     *               names like Login or Register.
     */
    default void page(String userId, String name) {
        page(userId, name, b -> { });
    }

    /**
     * Page method lets you record webpage visits from your web servers.
     *
     * @param userId  The ID for this user in your database.
     * @param name    The webpage name you’re tracking. We recommend human-readable
     *                names like Login or Register.
     * @param builder Consumer of the builder for the additional configuration
     */
    void page(String userId, String name, Consumer<MessageBuilderWithProperties> builder);

    /**
     * Page method lets you record webpage visits from your web servers.
     *
     * @param userId   The ID for this user in your database.
     * @param name     The webpage name you’re tracking. We recommend human-readable
     *                 names like Login or Register.
     * @param category The webpage category. If you’re making a news app, the category
     *                 could be Sports.
     */
    default void page(String userId, String name, String category) {
        page(userId, name, b -> b.properties("category", category));
    }

    /**
     * Page method lets you record webpage visits from your web servers.
     *
     * @param userId     The ID for this user in your database.
     * @param name       The webpage name you’re tracking. We recommend human-readable
     *                   names like Login or Register.
     * @param category   The webpage category. If you’re making a news app, the category
     *                   could be Sports.
     * @param properties A dictionary of properties for the webpage visit. If the event
     *                   was Login, it might have properties like path or title.
     * @deprecated use {@link #page(String, String, Consumer)} instead
     */
    @Deprecated
    default void page(String userId, String name, String category, Map<String, ?> properties) {
        page(userId, name, b -> b.properties("category", category).properties(properties));
    }

    /**
     * Page method lets you record webpage visits from your web servers.
     *
     * @param userId     The ID for this user in your database.
     * @param name       The webpage name you’re tracking. We recommend human-readable
     *                   names like Login or Register.
     * @param category   The webpage category. If you’re making a news app, the category
     *                   could be Sports.
     * @param properties A dictionary of properties for the webpage visit. If the event
     *                   was Login, it might have properties like path or title.
     * @param timestamp  a DateTime object representing when the track took
     *                   place. If the event just happened, leave it blank and we'll
     *                   use the server's time. If you are importing data from the
     *                   past, make sure you provide this argument.
     * @deprecated use {@link #page(String, String, Consumer)} instead
     */
    @Deprecated
    default void page(String userId, String name, String category, Map<String, ?> properties, Date timestamp) {
        page(userId, name, b -> b.properties("category", category).properties(properties).timestamp(timestamp));
    }


    /**
     * Page method lets you record webpage visits from your web servers.
     *
     * @param userId     The ID for this user in your database.
     * @param name       The webpage name you’re tracking. We recommend human-readable
     *                   names like Login or Register.
     * @param category   The webpage category. If you’re making a news app, the category
     *                   could be Sports.
     * @param properties A dictionary of properties for the webpage visit. If the event
     *                   was Login, it might have properties like path or title.
     * @param timestamp  a DateTime object representing when the track took
     *                   place. If the event just happened, leave it blank and we'll
     *                   use the server's time. If you are importing data from the
     *                   past, make sure you provide this argument.
     * @param options    A custom object which allows you to set a timestamp, an anonymous
     *                   cookie id, or enable specific integrations.
     * @deprecated use {@link #page(String, String, Consumer)} instead
     */
    @Deprecated
    default void page(String userId, String name, String category, Map<String, ?> properties, Date timestamp, Map<String, ?> options) {
        page(userId, name, b -> {
            b.properties("category", category).properties(properties);
            LegacySupport.addOptions(b, options, timestamp);
        });
    }

    /**
     * screen lets you record mobile screen views from your web servers.
     *
     * @param userId The ID for this user in your database.
     * @param name   The screen name you’re tracking. We recommend human-readable
     *               names like Login or Register.
     */
    default void screen(String userId, String name) {
        screen(userId, name, b -> { });
    }

    /**
     * screen lets you record mobile screen views from your web servers.
     *
     * @param userId   The ID for this user in your database.
     * @param name     The screen name you’re tracking. We recommend human-readable
     *                 names like Login or Register.
     * @param category The webpage category. If you’re making a news app, the category
     *                 could be Sports.
     */
    default void screen(String userId, String name, String category) {
        screen(userId, name, b -> b.properties("category", category));
    }

    /**
     * screen lets you record mobile screen views from your web servers.
     *
     * @param userId  The ID for this user in your database.
     * @param name    The screen name you’re tracking. We recommend human-readable
     *                names like Login or Register.
     * @param builder Consumer of the builder for the additional configuration
     */
    void screen(String userId, String name, Consumer<MessageBuilderWithProperties> builder);

    /**
     * screen lets you record mobile screen views from your web servers.
     *
     * @param userId     The ID for this user in your database.
     * @param name       The screen name you’re tracking. We recommend human-readable
     *                   names like Login or Register.
     * @param category   The webpage category. If you’re making a news app, the category
     *                   could be Sports.
     * @param properties A dictionary of properties for the screen view. If the screen
     *                   is Restaurant Reviews, it might have properties like reviewCount
     *                   or restaurantName.
     * @deprecated use {@link #screen(String, String, Consumer)} instead
     */
    @Deprecated
    default void screen(String userId, String name, String category, Map<String, ?> properties) {
        screen(userId, name, b -> b.properties("category", category).properties(properties));
    }


    /**
     * screen lets you record mobile screen views from your web servers.
     *
     * @param userId     The ID for this user in your database.
     * @param name       The screen name you’re tracking. We recommend human-readable
     *                   names like Login or Register.
     * @param category   The webpage category. If you’re making a news app, the category
     *                   could be Sports.
     * @param properties A dictionary of properties for the screen view. If the screen
     *                   is Restaurant Reviews, it might have properties like reviewCount
     *                   or restaurantName.
     * @param timestamp  A DateTime object representing when the track took
     *                   place. If the event just happened, leave it blank and we'll
     *                   use the server's time. If you are importing data from the
     *                   past, make sure you provide this argument.
     * @deprecated use {@link #screen(String, String, Consumer)} instead
     */
    @Deprecated
    default void screen(String userId, String name, String category, Map<String, ?> properties, Date timestamp) {
        screen(userId, name, b -> b.properties("category", category).properties(properties).timestamp(timestamp));
    }


    /**
     * screen lets you record mobile screen views from your web servers.
     *
     * @param userId     The ID for this user in your database.
     * @param name       The screen name you’re tracking. We recommend human-readable
     *                   names like Login or Register.
     * @param category   The webpage category. If you’re making a news app, the category
     *                   could be Sports.
     * @param properties A dictionary of properties for the screen view. If the screen
     *                   is Restaurant Reviews, it might have properties like reviewCount
     *                   or restaurantName.
     * @param timestamp  A DateTime object representing when the track took
     *                   place. If the event just happened, leave it blank and we'll
     *                   use the server's time. If you are importing data from the
     *                   past, make sure you provide this argument.
     * @param options    A custom object which allows you to set a timestamp, an anonymous
     *                   cookie id, or enable specific integrations.
     * @deprecated use {@link #screen(String, String, Consumer)} instead
     */
    @Deprecated
    default void screen(String userId, String name, String category, Map<String, ?> properties, Date timestamp, Map<String, ?> options) {
        screen(userId, name, b -> {
            b.properties("category", category).properties(properties);
            LegacySupport.addOptions(b, options, timestamp);
        });
    }

    /**
     * track lets you record the actions your users perform.
     *
     * @param userId The ID for this user in your database.
     * @param event  The name of the event you’re tracking. We recommend human-readable
     *               names like Played Song or Updated Status.
     */
    default void track(String userId, String event) {
        track(userId, event, b -> { });
    }

    /**
     * track lets you record the actions your users perform.
     *
     * @param userId  The ID for this user in your database.
     * @param event   The name of the event you’re tracking. We recommend human-readable
     *                names like Played Song or Updated Status.
     * @param builder Consumer of the builder for the additional configuration
     */
    void track(String userId, String event, Consumer<MessageBuilderWithProperties> builder);

    /**
     * track lets you record the actions your users perform.
     *
     * @param userId     The ID for this user in your database.
     * @param event      The name of the event you’re tracking. We recommend human-readable
     *                   names like Played Song or Updated Status.
     * @param properties A dictionary of properties for the event. If the event was Added to Cart,
     *                   it might have properties like price or product.
     * @deprecated use {@link #screen(String, String, Consumer)} instead
     */
    @Deprecated
    default void track(String userId, String event, Map<String, ?> properties) {
        track(userId, event, b -> b.properties(properties));
    }

    /**
     * track lets you record the actions your users perform.
     *
     * @param userId     The ID for this user in your database.
     * @param event      The name of the event you’re tracking. We recommend human-readable
     *                   names like Played Song or Updated Status.
     * @param properties A dictionary of properties for the event. If the event was Added to Cart,
     *                   it might have properties like price or product.
     * @param timestamp  a DateTime object representing when the track took
     *                   place. If the event just happened, leave it blank and we'll
     *                   use the server's time. If you are importing data from the
     *                   past, make sure you provide this argument.
     * @deprecated use {@link #track(String, String, Consumer)} instead
     */
    @Deprecated
    default void track(String userId, String event, Map<String, ?> properties, Date timestamp) {
        track(userId, event, b -> b.properties(properties).timestamp(timestamp));
    }


    /**
     * track lets you record the actions your users perform.
     *
     * @param userId     The ID for this user in your database.
     * @param event      The name of the event you’re tracking. We recommend human-readable
     *                   names like Played Song or Updated Status.
     * @param properties A dictionary of properties for the event. If the event was Added to Cart,
     *                   it might have properties like price or product.
     * @param timestamp  a DateTime object representing when the track took
     *                   place. If the event just happened, leave it blank and we'll
     *                   use the server's time. If you are importing data from the
     *                   past, make sure you provide this argument.
     * @param options    A custom object which allows you to set a timestamp, an anonymous cookie id,
     *                   or enable specific integrations.
     * @deprecated use {@link #track(String, String, Consumer)} instead
     */
    @Deprecated
    default void track(String userId, String event, Map<String, ?> properties, Date timestamp, Map<String, ?> options) {
        track(userId, event, b -> {
            b.properties(properties);
            LegacySupport.addOptions(b, options, timestamp);
        });
    }

    class LegacySupport {

        private static final List<String> SUPPORTED_CONTEXT_OPTIONS = Arrays.asList("ip", "language", "userAgent", "Intercom");

        private LegacySupport() { }

        /**
         * Method to support old-style options settings.
         *
         * @deprecated for internal use only
         */
        @Deprecated
        @SuppressWarnings({"unchecked", "rawtypes"})
        static <B extends MessageBuilder> B addOptions(B builder, Map<String, ?> options, Date timestamp) {
            if (timestamp != null) {
                builder.timestamp(timestamp);
            }

            if (options.containsKey("anonymousId")) {
                Object anonymousId = options.get("anonymousId");
                if (anonymousId != null) {
                    builder.anonymousId(anonymousId.toString());
                }
            }

            if (options.containsKey("integrations")) {
                Object integrations = options.get("integrations");
                if (integrations instanceof Map) {
                    Map<String, Object> integrationsMap = (Map<String, Object>) integrations;
                    integrationsMap.forEach((key, payload) -> {
                        if (payload instanceof Boolean) {
                            builder.enableIntegration(key, (Boolean) payload);
                        } else if (payload instanceof Map) {
                            builder.integrationOptions(key, (Map<String, ?>) payload);
                        }
                    });
                }
            }

            SUPPORTED_CONTEXT_OPTIONS.forEach(option -> {
                if (options.containsKey(option)) {
                    builder.context(option, options.get(option));
                }
            });

            return builder;
        }
    }
}
