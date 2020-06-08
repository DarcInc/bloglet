package io.darkink.bloglet.processing.settings;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Component
public class FeatureRepository {
    private Map<Path, Map<String, Feature>> features = new HashMap<>();

    public void save(Feature feature, Path forPath) {
        if (!features.containsKey(forPath)) {
            features.put(forPath, new HashMap<>());
        }

        features.get(forPath).put(feature.getName(), feature);
    }

    public Feature find(String name, Path forPath) {
        return features.get(forPath).get(name);
    }
}
