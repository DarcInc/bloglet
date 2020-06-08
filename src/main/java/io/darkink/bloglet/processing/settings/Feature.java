package io.darkink.bloglet.processing.settings;

import java.util.HashMap;
import java.util.Map;

public class Feature {
    private String name;
    private Map<String, String> settings;


    public Feature(String name) {
        this.name = name;
        this.settings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void put(String setting, String value) {
        settings.put(setting, value);
    }

    public String get(String name) {
        return settings.get(name);
    }

    public Feature overrides(Feature parent) {
        if (!name.equals(parent.name)) {
            throw new FeatureRepositoryException(String.format("Feature names must match: %s cannot override %s",
                    name, parent.name));
        }
        Feature result = new Feature(name);

        for(Map.Entry<String, String> item : parent.settings.entrySet()) {
            result.settings.put(item.getKey(), item.getValue());
        }

        for(Map.Entry<String, String> item : settings.entrySet()) {
            result.settings.put(item.getKey(), item.getValue());
        }

        return result;
    }
}
