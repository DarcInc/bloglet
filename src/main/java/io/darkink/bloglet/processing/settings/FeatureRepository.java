package io.darkink.bloglet.processing.settings;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FeatureRepository {
    void save(Feature feature, Path forPath);
    Optional<Feature> find(String name, Path forPath);
    Collection<String> allFeatures();
}
