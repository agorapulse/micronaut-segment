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

import java.util.Date;
import java.util.Map;

public class NoOpSegmentService implements SegmentService {

    @Override
    public void flush() {
        // noop
    }

    @Override
    public void alias(String from, String to) {
        // noop
    }

    @Override
    public void group(String userId, String groupId, Map<String, Object> traits, Map<String, Object> options) {
        // noop
    }

    @Override
    public void identify(String userId, Map<String, Object> traits, Date timestamp, Map<String, Object> options) {
        // noop
    }

    @Override
    public void page(String userId, String name, String category, Map<String, Object> properties, Date timestamp, Map<String, Object> options) {
        // noop
    }

    @Override
    public void screen(String userId, String name, String category, Map<String, Object> properties, Date timestamp, Map<String, Object> options) {
        // noop
    }

    @Override
    public void track(String userId, String event, Map<String, Object> properties, Date timestamp, Map<String, Object> options) {
        // noop
    }
}
