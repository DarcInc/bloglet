package io.darkink.bloglet.processing.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.handler.annotation.Headers;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public class LoggingActivator {
    Log log = LogFactory.getLog(LoggingActivator.class);

    public void handle(Optional<File> payload, @Headers Map<String, Object> headers) {
        StringBuilder sb = new StringBuilder();

        sb.append(payload.get().getAbsolutePath()).append("\n");
        for(String key: headers.keySet()) {
            sb.append(String.format("%s: %s\n", key, headers.get(key).toString()));
        }

        log.info(sb.toString());
    }
}
