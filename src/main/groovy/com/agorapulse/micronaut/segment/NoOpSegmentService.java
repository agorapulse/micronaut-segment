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
