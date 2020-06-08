package io.darkink.bloglet.processing.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FeatureTest {
    @Test
    public void shouldOverrideExistingFeatures() {
        Feature feature = new Feature("mayhem");
        Feature override = new Feature("mayhem");

        feature.put("foo", "bar");
        feature.put("darth", "vader");
        override.put("foo", "baz");
        override.put("qux", "quux");

        Feature combined = override.overrides(feature);

        assertEquals("baz", combined.get("foo"));
        assertEquals("vader", combined.get("darth"));
        assertEquals("quux", combined.get("qux"));
    }

    @Test
    public void shouldGetAndSetSettings() {
        Feature feature = new Feature("mayhem");

        feature.put("foo", "bar");
        feature.put("baz", "qux");

        assertEquals("bar", feature.get("foo"));
        assertEquals("qux", feature.get("baz"));
    }

    @Test
    public void shouldReturnNullForSettingNotFound() {
        Feature feature = new Feature("mayhem");

        feature.put("foo", "bar");

        assertNull(feature.get("baz"));
    }

    @Test
    public void shouldThrowExceptionOverridingMismatchedFeatureName() {
        Feature feature = new Feature("mayhem");
        Feature override = new Feature("chaos");

        feature.put("foo", "bar");
        override.put("foo", "bar");

        assertThrows(FeatureRepositoryException.class, () -> {
            override.overrides(feature);
        });
    }
}
