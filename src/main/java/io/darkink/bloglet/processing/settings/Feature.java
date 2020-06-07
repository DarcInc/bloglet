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

    public void put(String setting, String value) {
        settings.put(setting, value);
    }

    public String get(String name) {
        return settings.get(name);
    }
}
