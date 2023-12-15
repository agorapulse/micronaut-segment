/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2023 Agorapulse.
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
package com.agorapulse.micronaut.segment.groovy;

import com.agorapulse.micronaut.segment.SegmentService;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FromString;
import space.jasan.support.groovy.closure.ConsumerWithDelegate;

public class SegmentServiceExtensions {

    private SegmentServiceExtensions() { }

    /**
     * Alias method lets you merge two user profiles, including their actions and traits.
     *
     * @param from    the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param builder Consumer of the builder for the additional configuration
     */
    public static void alias(
        SegmentService self,
        String from,
        String to,
        @DelegatesTo(type = "com.agorapulse.micronaut.segment.builder.SimpleMessageBuilder", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.micronaut.segment.builder.SimpleMessageBuilder")
            Closure<?> builder
    ) {
        self.alias(from, to, ConsumerWithDelegate.create(builder));
    }

    /**
     * Group method lets you associate a user with a group.
     *
     * @param userId  the user's id after they are logged in. It's the same id as
     *                which you would recognize a signed-in user in your system.
     * @param groupId The ID for this group in your database.
     * @param builder Consumer of the builder for the additional configuration
     */
    public static void group(
        SegmentService self,
        String userId,
        String groupId,
        @DelegatesTo(type = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithTraits", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithTraits")
            Closure<?> builder
    ) {
        self.group(userId, groupId, ConsumerWithDelegate.create(builder));
    }


    /**
     * identify lets you tie a user to their actions and record traits about them.
     *
     * @param userId  The ID for this user in your database.
     * @param builder Consumer of the builder for the additional configuration
     */
    public static void identify(
        SegmentService self,
        String userId,
        @DelegatesTo(type = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithTraits", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithTraits")
            Closure<?> builder
    ) {
        self.identify(userId, ConsumerWithDelegate.create(builder));
    }

    /**
     * Page method lets you record webpage visits from your web servers.
     *
     * @param userId  The ID for this user in your database.
     * @param name    The webpage name you’re tracking. We recommend human-readable
     *                names like Login or Register.
     * @param builder Consumer of the builder for the additional configuration
     */
    public static void page(
        SegmentService self,
        String userId,
        String name,
        @DelegatesTo(type = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithProperties", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithProperties")
            Closure<?> builder
    ) {
        self.page(userId, name, ConsumerWithDelegate.create(builder));
    }

    /**
     * screen lets you record mobile screen views from your web servers.
     *
     * @param userId  The ID for this user in your database.
     * @param name    The screen name you’re tracking. We recommend human-readable
     *                names like Login or Register.
     * @param builder Consumer of the builder for the additional configuration
     */
    public static void screen(
        SegmentService self,
        String userId,
        String name,
        @DelegatesTo(type = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithProperties", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithProperties")
            Closure<?> builder
    ) {
        self.screen(userId, name, ConsumerWithDelegate.create(builder));
    }

    /**
     * track lets you record the actions your users perform.
     *
     * @param userId  The ID for this user in your database.
     * @param event   The name of the event you’re tracking. We recommend human-readable
     *                names like Played Song or Updated Status.
     * @param builder Consumer of the builder for the additional configuration
     */
    public static void track(
        SegmentService self,
        String userId,
        String event,
        @DelegatesTo(type = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithProperties", strategy = Closure.DELEGATE_FIRST)
        @ClosureParams(value = FromString.class, options = "com.agorapulse.micronaut.segment.builder.MessageBuilderWithProperties")
            Closure<?> builder
    ) {
        self.track(userId, event, ConsumerWithDelegate.create(builder));
    }

}
