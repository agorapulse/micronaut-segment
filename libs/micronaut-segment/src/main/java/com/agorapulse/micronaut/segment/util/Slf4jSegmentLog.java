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
package com.agorapulse.micronaut.segment.util;

import com.segment.analytics.Analytics;
import com.segment.analytics.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jSegmentLog implements Log {

    private static final Logger LOGGER = LoggerFactory.getLogger(Analytics.class);

    @Override
    public void print(Level level, String format, Object... args) {
        switch (level) {
            case VERBOSE:
                LOGGER.trace(String.format(format, args));
                break;
            case DEBUG:
                LOGGER.debug(String.format(format, args));
                break;
            case ERROR:
                LOGGER.error(String.format(format, args));
                break;
        }
    }

    @Override
    public void print(Level level, Throwable error, String format, Object... args) {
        switch (level) {
            case VERBOSE:
                LOGGER.trace(String.format(format, args), error);
                break;
            case DEBUG:
                LOGGER.debug(String.format(format, args), error);
                break;
            case ERROR:
                LOGGER.error(String.format(format, args), error);
                break;
        }
    }
}
