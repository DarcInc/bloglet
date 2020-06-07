package io.darkink.bloglet.processing.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.FileLocker;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.RecursiveDirectoryScanner;
import org.springframework.integration.file.filters.FileListFilter;
import org.springframework.messaging.MessageChannel;

import java.io.File;
import java.util.*;

@Configuration
public class FileCopyConfiguration {
    @Value("${io.darkink.bloglet.processer.watchDir}")
    private String watchDirectory;

    @Value("${io.darkink.bloglet.processor.outputDir}")
    private String copyDirectory;

    @Bean
    public MessageChannel fileInputChannel() {
        return MessageChannels.direct().get();
    }


    public FileListFilter<File> markdownFilter() {
        return new FileListFilter<File>() {
            Map<String, Long> visitedFiles = new HashMap<>();

            private boolean isModified(File file) {
                long lastModified = visitedFiles.get(file.getAbsolutePath());
                if (lastModified < file.lastModified()) {
                    return true;
                }
                return false;
            }

            @Override
            public List<File> filterFiles(File[] files) {
                List<File> result = new LinkedList<>();

                for(File f : files) {
                    if (f.getName().endsWith(".md") || f.getName().endsWith(".markdown")) {
                        if (!visitedFiles.containsKey(f.getAbsolutePath())) {
                            visitedFiles.put(f.getAbsolutePath(), f.lastModified());
                            result.add(f);
                        } else {
                            if (isModified(f)) {
                                visitedFiles.put(f.getAbsolutePath(), f.lastModified());
                                result.add(f);
                            }
                        }
                    }
                }

                return result;
            }
        };
    }

    @Bean
    public RecursiveDirectoryScanner recursiveDirectoryScanner() {
        RecursiveDirectoryScanner scanner = new RecursiveDirectoryScanner();
        scanner.setFilter(markdownFilter());
        return scanner;
    }

    @Bean
    @InboundChannelAdapter(channel = "fileInputChannel", poller = @Poller(fixedDelay = "1000", maxMessagesPerPoll = "100"))
    public MessageSource<File> fileReadingMessageSource(RecursiveDirectoryScanner recursiveDirectoryScanner) {
        FileReadingMessageSource fileReadingMessageSource = new FileReadingMessageSource();
        fileReadingMessageSource.setDirectory(new File(watchDirectory));
        fileReadingMessageSource.setScanner(recursiveDirectoryScanner);
        fileReadingMessageSource.setWatchEvents(FileReadingMessageSource.WatchEventType.MODIFY,
                FileReadingMessageSource.WatchEventType.DELETE, FileReadingMessageSource.WatchEventType.CREATE);

        return fileReadingMessageSource;
    }

    @Bean
    @ServiceActivator(inputChannel = "fileInputChannel")
    public CopyActivator copyActivator() {
        return new CopyActivator(copyDirectory);
    }
}
