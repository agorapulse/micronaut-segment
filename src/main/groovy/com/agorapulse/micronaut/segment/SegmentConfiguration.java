package com.agorapulse.micronaut.segment;

import io.micronaut.context.annotation.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties("segment")
public class SegmentConfiguration {

    @NotBlank
    private String apiKey;

    private Map<String, Object> options = new LinkedHashMap<>();

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }


}
