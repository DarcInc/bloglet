package io.darkink.bloglet.processing.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureTest {
    @Test
    public void shouldOverrideExistingFeatures() {

    }

    @Test
    public void shouldGetAndSetSettings() {
        Feature feature = new Feature("mayhem");

        feature.put("foo", "bar");
        feature.put("baz", "qux");

        assertEquals("bar", feature.get("foo"));
        assertEquals("baz", feature.get("qux"));
    }
}
