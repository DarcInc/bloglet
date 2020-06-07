package io.darkink.bloglet.processing.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.handler.annotation.Header;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class CopyActivator {
    Log log = LogFactory.getLog(CopyActivator.class);

    private Path outputDirectory;

    public CopyActivator(String outputPath) {
        outputDirectory = Paths.get(outputPath);
    }

    public void handle(Optional<File> payload, @Header("file_name") String filename,
                       @Header("file_relativePath") String relativePath) {
        if (payload.isPresent()) {
            log.info(String.format("Filename: %s", filename));
            log.info(String.format("Path: %s", relativePath));
            log.info(payload.get());

            try {
                Path newPath = Paths.get(outputDirectory.toString(), relativePath);
                Path parentDirectory = newPath.getParent();
                if (!Files.exists(parentDirectory)) {
                    Files.createDirectories(parentDirectory);
                }

                Files.copy(Paths.get(payload.get().getAbsolutePath()), newPath, StandardCopyOption.REPLACE_EXISTING);
            } catch(IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }
        } else {
            log.warn("No Payload");
        }
    }
}
