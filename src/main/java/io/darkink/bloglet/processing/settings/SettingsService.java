package io.darkink.bloglet.processing.settings;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class SettingsService {
    private final FeatureRepository featureRepository;

    public SettingsService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    public List<String> listFeatures() {
        return new LinkedList<>(featureRepository.allFeatures());
    }
}
