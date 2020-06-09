package io.darkink.bloglet.processing.settings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class SettingsService {
    Log log = LogFactory.getLog(SettingsService.class);

    @Value("${io.darkink.bloglet.processor.watchDir}")
    private String watchDirectory;

    private final FeatureRepository featureRepository;

    public SettingsService(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    private Feature readFeature(Path filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();

        Map<String, String> data = mapper.readValue(filePath.toFile(), new TypeReference<Map<String, String>>(){});
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
        work.add(Paths.get(watchDirectory));

        log.info("Running " + SettingsService.class.toGenericString() + " postconstruct");

        Map<String, List<File>> configFiles = new HashMap<>();

        while(!work.isEmpty()) {
            Path current = work.get(0);
            work.remove(0);

            try {
                Files.list(current).filter(path -> path.toString().endsWith(".yml") || Files.isDirectory(path)).forEach(path -> {
                    if (Files.isDirectory(path)) {
                        log.info(String.format("Adding directory to search: %s", path.toAbsolutePath()));
                        work.add(path);
                    } else {

                    }
                });
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }
        }

        Path root = Paths.get(watchDirectory);
        for (String key : configFiles.keySet()) {
            Path target = Paths.get(key);
            if (target.getParent().equals(root)) {
                log.info(String.format("%s is in root directory", key));
            } else {
                while (!target.getParent().equals(root)) {

                }
            }
        }
    }
}
