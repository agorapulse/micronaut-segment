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
import com.segment.analytics.messages.MessageBuilder
import com.segment.analytics.messages.PageMessage
import com.segment.analytics.messages.ScreenMessage
import com.segment.analytics.messages.TrackMessage
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j

@Slf4j
@CompileDynamic
class DefaultSegmentService implements SegmentService {

    private final Analytics analytics
    private final SegmentConfiguration config

    DefaultSegmentService(Analytics analytics, SegmentConfiguration config) {
        this.analytics = analytics
        this.config = config
    }

    /**
     * Flushes the current contents of the queue
     */
    void flush() {
        analytics.flush()
    }

    /**
     * Alias method lets you merge two user profiles, including their actions and traits.
     *
     * @param from
     *            the user's id after they are logged in. It's the same id as
     *            which you would recognize a signed-in user in your system.
     *
     * @param to
     *            new user id
     */
    void alias(String from, String to) {
        log.debug "Alias from=$from to=$to"
        MessageBuilder builder = AliasMessage.builder(from)
                                             .userId(to.toString())
        analytics.enqueue(builder)
    }

    /**
     * Group method lets you associate a user with a group.
     *
     * @param userId
     *            the user's id after they are logged in. It's the same id as
     *            which you would recognize a signed-in user in your system.
     *
     * @param groupId
     *            The ID for this group in your database.
     *
     * @param options
     *            a custom object which allows you to set a timestamp,
     *            an anonymous cookie id, or enable specific integrations.
     *
     */
    void group(String userId, String groupId, Map<String, Object> traits, Map<String, Object> options) {
        log.debug "Group userId=$userId groupId=$groupId traits=$traits"
        MessageBuilder builder = GroupMessage.builder(groupId.toString())
                                             .userId(userId.toString())
                                             .traits(safe(traits))
        addOptions(builder, options ?: config.options)
        analytics.enqueue(builder)
    }

    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId
     *            The ID for this user in your database.
     *
     * @param traits
     *            A dictionary of traits you know about the user. Things like: email,
     *            name or friends.
     *
     * @param timestamp
     *            A DateTime representing when the identify took place.
     *            If the identify just happened, leave it blank and we'll use
     *            the server's time. If you are importing data from the past,
     *            make sure you provide this argument.
     *
     * @param options
     *            A custom object which allows you to set a timestamp, an anonymous cookie id,
     *            or enable specific integrations.
     *
     */
    void identify(String userId, Map<String, Object> traits, Date timestamp, Map<String, Object> options) {
        log.debug "Identify userId=$userId traits=$traits timestamp=$timestamp options=$options"
        MessageBuilder builder = IdentifyMessage.builder()
                                                .userId(userId.toString())
                                                .traits(safe(traits))
        addOptions(builder, options ?: config.options, timestamp)
        analytics.enqueue(builder)
    }

    /**
     * Page method lets you record webpage visits from your web servers.
     *
     * @param userId
     *            The ID for this user in your database.
     *
     * @param name
     *            The webpage name you’re tracking. We recommend human-readable
     *            names like Login or Register.
     *
     * @param category
     *           The webpage category. If you’re making a news app, the category
     *           could be Sports.
     *
     * @param properties
     *            A dictionary of properties for the webpage visit. If the event
     *            was Login, it might have properties like path or title.
     *
     * @param timestamp
     *            a DateTime object representing when the track took
     *            place. If the event just happened, leave it blank and we'll
     *            use the server's time. If you are importing data from the
     *            past, make sure you provide this argument.
     *
     * @param options
     *            A custom object which allows you to set a timestamp, an anonymous
     *            cookie id, or enable specific integrations.
     *
     */
    @SuppressWarnings('ParameterCount')
    void page(String userId, String name, String category, Map<String, Object> properties, Date timestamp, Map<String, Object> options) {
        log.debug "Page userId=$userId name=$name category=$category properties=$properties timestamp=$timestamp options=$options"
        MessageBuilder builder = PageMessage.builder(name)
                                            .userId(userId.toString())
                                            .properties(safe([category: category] + properties))
        addOptions(builder, options ?: config.options, timestamp)
        analytics.enqueue(builder)
    }

    /**
     * screen lets you record mobile screen views from your web servers.
     *
     * @param userId
     *            The ID for this user in your database.
     *
     * @param name
     *            The screen name you’re tracking. We recommend human-readable
     *            names like Login or Register.
     *
     * @param category
     *           The webpage category. If you’re making a news app, the category
     *           could be Sports.
     *
     * @param properties
     *            A dictionary of properties for the screen view. If the screen
     *            is Restaurant Reviews, it might have properties like reviewCount
     *            or restaurantName.
     *
     * @param timestamp
     *            A DateTime object representing when the track took
     *            place. If the event just happened, leave it blank and we'll
     *            use the server's time. If you are importing data from the
     *            past, make sure you provide this argument.
     *
     * @param options
     *            A custom object which allows you to set a timestamp, an anonymous
     *            cookie id, or enable specific integrations.
     *
     */
    @SuppressWarnings('ParameterCount')
    void screen(String userId, String name, String category, Map<String, Object> properties, Date timestamp, Map<String, Object> options) {
        log.debug "Screen userId=$userId name=$name category=$category properties=$properties timestamp=$timestamp options=$options"
        MessageBuilder builder = ScreenMessage.builder(name)
                                              .userId(userId.toString())
                                              .properties(safe([category: category] + properties))
        addOptions(builder, options ?: config.options, timestamp)
        analytics.enqueue(builder)
    }

    /**
     * track lets you record the actions your users perform.
     *
     * @param userId
     *            The ID for this user in your database.
     *
     * @param event
     *            The name of the event you’re tracking. We recommend human-readable
     *            names like Played Song or Updated Status.
     *
     * @param properties
     *            A dictionary of properties for the event. If the event was Added to Cart,
     *            it might have properties like price or product.
     *
     * @param timestamp
     *            a DateTime object representing when the track took
     *            place. If the event just happened, leave it blank and we'll
     *            use the server's time. If you are importing data from the
     *            past, make sure you provide this argument.
     *
     * @param options
     *            A custom object which allows you to set a timestamp, an anonymous cookie id,
     *            or enable specific integrations.
     *
     */
    void track(String userId, String event, Map<String, Object> properties, Date timestamp, Map<String, Object> options) {
        log.debug "Tracking userId=$userId event=$event properties=$properties timestamp=$timestamp options=$options"
        MessageBuilder builder = TrackMessage.builder(event)
                                             .userId(userId.toString())
                                             .properties(safe(properties))
        addOptions(builder, options ?: config.options, timestamp)
        analytics.enqueue(builder)
    }

    // PRIVATE

    private static <K, V> Map<K, V> safe(Map<K, V> original) {
        return original.findAll { it.value }
    }

    private static MessageBuilder addOptions(MessageBuilder builder, Map options, Date timestamp = null) {
        Map safeOptions = safe options
        if (timestamp) {
            builder.timestamp(timestamp)
        }
        if (safeOptions.anonymousId) {
            builder.anonymousId(safeOptions.anonymousId as String)
        }
        if (safeOptions.integrations) {
            safeOptions.integrations.each { String key, Boolean enabled ->
                builder.enableIntegration(key, enabled)
            }
        }
        if (safeOptions.ip || safeOptions.language || safeOptions.userAgent || safeOptions.Intercom) {
            Map<String, Object> context = [:]
            if (safeOptions.ip) {
                context += [ip: safeOptions.ip]
            }
            if (safeOptions.language) {
                context += [language: safeOptions.language]
            }
            if (safeOptions.userAgent) {
                context += [userAgent: safeOptions.userAgent]
            }
            if (safeOptions.Intercom) {
                context += [Intercom: safeOptions.Intercom]
            }
            builder.context(context)
        }
        builder
    }

}
