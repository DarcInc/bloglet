package io.darkink.bloglet.processing.settings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class FilesystemFeatureRepository implements FeatureRepository {
    @Value("${io.darkink.bloglet.processor.watchDir}")
    private String rootPath;

    private final Map<Path, Map<String, Feature>> features = new HashMap<>();
    private final Log log = LogFactory.getLog(FilesystemFeatureRepository.class);

    @Override
    public void save(Feature feature, Path forPath) {
        synchronized (features) {
            if (!features.containsKey(forPath)) {
                features.put(forPath, new HashMap<>());
            }

            features.get(forPath).put(feature.getName(), feature);
        }
    }

    @Override
    public Optional<Feature> find(String name, Path forPath) {
        Path root = Paths.get(rootPath);
        Optional<Feature> result = Optional.empty();

        Path currentPath = forPath;

        synchronized (features) {
            if (features.containsKey(currentPath) && features.get(currentPath).containsKey(name)) {
                result = Optional.of(features.get(currentPath).get(name));
            }

            while (!root.equals(currentPath)) {
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
        }

        return result;
    }

    @Override
    public Collection<String> allFeatures() {
        Set<String> result = new HashSet<>();

        synchronized (features) {
            for(Map.Entry<Path, Map<String, Feature>> entry : features.entrySet()) {
                for(Map.Entry<String, Feature> f : entry.getValue().entrySet()) {
                    result.add(f.getKey());
                }
            }
        }

        return result;
    }

    private Feature readFeature(Path filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        Map<String, String> data = mapper.readValue(filePath.toFile(), new TypeReference<>() {
        });
        String filename = filePath.getFileName().toString().replace(".yml", "");

        Feature result = new Feature(filename);
        for(Map.Entry<String, String> line : data.entrySet()) {
            result.put(line.getKey(), line.getValue());
        }

        return result;
    }

    @PostConstruct
    public void postConstruct() {
        List<Path> work = new LinkedList<>();
        work.add(Paths.get(rootPath));

        log.info("Running " + SettingsService.class.toGenericString() + " postconstruct");

        while(!work.isEmpty()) {
            Path current = work.get(0);
            work.remove(0);

            try {
                Files.list(current).filter(path -> path.toString().endsWith(".yml") || Files.isDirectory(path)).forEach(path -> {
                    if (Files.isDirectory(path)) {
                        work.add(path);
                    } else {
                        try {
                            Feature newFeature = readFeature(path);
                            save(newFeature, path.getParent());
                        } catch (IOException ioe) {
                            throw new FeatureRepositoryException(
                                    String.format("FAiled to read feature at path: %s", path.toString()),
                                    ioe);
                        }
                    }
                });
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }
        }
    }
}
