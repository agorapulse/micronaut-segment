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

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties("segment")
@Requires(property = "segment.api-key")
public class SegmentConfiguration {

    @NotBlank @NotNull
    private String apiKey;

    private Map<String, Object> options = new LinkedHashMap<>();

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @return the default options for the messages
     * @deprecated declare {@link com.segment.analytics.MessageTransformer} bean instead
     */
    @Deprecated
    public Map<String, Object> getOptions() {
        return options;
    }

    /**
     * Sets the default options for the messages.
     * @deprecated declare {@link com.segment.analytics.MessageTransformer} bean instead
     */
    @Deprecated
    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

}
