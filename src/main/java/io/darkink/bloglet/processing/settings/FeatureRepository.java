package io.darkink.bloglet.processing.settings;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FeatureRepository {
    @Value("${io.darkink.bloglet.processor.watchDir}")
    private String rootPath;

    private Map<Path, Map<String, Feature>> features = new HashMap<>();

    public void save(Feature feature, Path forPath) {
        if (!features.containsKey(forPath)) {
            features.put(forPath, new HashMap<>());
        }

        features.get(forPath).put(feature.getName(), feature);
    }

    public Optional<Feature> find(String name, Path forPath) {
        Path root = Paths.get(rootPath);
        Optional<Feature> result = Optional.empty();

        Path currentPath = forPath;
        if (features.containsKey(currentPath) && features.get(currentPath).containsKey(name))  {
            result = Optional.of(features.get(currentPath).get(name));
        }

        while(!root.equals(currentPath)) {
            currentPath = currentPath.getParent();
            if (features.containsKey(currentPath) && features.get(currentPath).containsKey(name)) {
                Feature found = features.get(currentPath).get(name);
                if (result.isEmpty()) {
                    result = Optional.of(found);
                } else {
                    result = Optional.of(result.get().overrides(found));
                }
            }
        }

        return result;
    }
}
