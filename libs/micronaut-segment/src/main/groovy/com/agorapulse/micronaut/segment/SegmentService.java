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

import java.util.Collections;
import java.util.Date;
import java.util.Map;

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
    void alias(String from, String to);

    /**
     * Group method lets you associate a user with a group.
     *
     * @param userId  the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param groupId The ID for this group in your database.
     */
    default void group(String userId, String groupId) {
        group(userId, groupId, Collections.emptyMap());
    }

    /**
     * Group method lets you associate a user with a group.
     *
     * @param userId  the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param groupId The ID for this group in your database.
     * @param traits  A dictionary of traits you know about the user. Things like: email,
     *                name or friends.
     */
    default void group(String userId, String groupId, Map<String, Object> traits) {
        group(userId, groupId, traits, Collections.emptyMap());
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
     */
    void group(String userId, String groupId, Map<String, Object> traits, Map<String, Object> options);

    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId The ID for this user in your database.
     */
    default void identify(String userId) {
        identify(userId, Collections.emptyMap());
    }


    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId The ID for this user in your database.
     * @param traits A dictionary of traits you know about the user. Things like: email,
     *               name or friends.
     */
    default void identify(String userId, Map<String, Object> traits) {
        identify(userId, traits, null);
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
     */
    default void identify(String userId, Map<String, Object> traits, Date timestamp) {
        identify(userId, traits, timestamp, Collections.emptyMap());
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
     */
    void identify(String userId, Map<String, Object> traits, Date timestamp, Map<String, Object> options);

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
        page(userId, name, category, Collections.emptyMap());
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
     */
    default void page(String userId, String name, String category, Map<String, Object> properties) {
        page(userId, name, category, properties, null);
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
     */
    default void page(String userId, String name, String category, Map<String, Object> properties, Date timestamp) {
        page(userId, name, category, properties, timestamp, Collections.emptyMap());
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
     */
    void page(String userId, String name, String category, Map<String, Object> properties, Date timestamp, Map<String, Object> options);


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
        screen(userId, name, category, Collections.emptyMap());
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
     */
    default void screen(String userId, String name, String category, Map<String, Object> properties) {
        screen(userId, name, category, properties, null);
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
     */
    default void screen(String userId, String name, String category, Map<String, Object> properties, Date timestamp) {
       screen(userId, name, category, properties, timestamp, Collections.emptyMap());
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
     */
    void screen(String userId, String name, String category, Map<String, Object> properties, Date timestamp, Map<String, Object> options);

    /**
     * track lets you record the actions your users perform.
     *
     * @param userId     The ID for this user in your database.
     * @param event      The name of the event you’re tracking. We recommend human-readable
     *                   names like Played Song or Updated Status.
     */
    default void track(String userId, String event) {
        track(userId, event, Collections.emptyMap());
    }

    /**
     * track lets you record the actions your users perform.
     *
     * @param userId     The ID for this user in your database.
     * @param event      The name of the event you’re tracking. We recommend human-readable
     *                   names like Played Song or Updated Status.
     * @param properties A dictionary of properties for the event. If the event was Added to Cart,
     *                   it might have properties like price or product.
     */
    default void track(String userId, String event, Map<String, Object> properties) {
        track(userId, event, properties, null);
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
     */
    default void track(String userId, String event, Map<String, Object> properties, Date timestamp) {
        track(userId, event, properties, timestamp, Collections.emptyMap());
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
     */
    void track(String userId, String event, Map<String, Object> properties, Date timestamp, Map<String, Object> options);

}
