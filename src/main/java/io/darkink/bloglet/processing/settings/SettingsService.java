package io.darkink.bloglet.processing.settings;

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

    public SettingsService() {
        log.info("Constructing Settings Service");
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
                        //configFiles.put(path.toString(), path.toFile());
                        log.info(String.format("Found Config file: %s", path.toString()));
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
