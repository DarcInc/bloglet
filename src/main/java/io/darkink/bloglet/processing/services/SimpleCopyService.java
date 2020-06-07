package io.darkink.bloglet.processing.services;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class SimpleCopyService {
    public String handleString(String input) {
        System.out.println("Copying text: " + input);
        return input.toUpperCase();
    }

    public File handleFile(File input) {
        System.out.println("Copying file: " + input.getAbsolutePath());
        return input;
    }

    public byte[] handleBytes(byte[] input) {
        System.out.println("Copying " + input.length + " bytes ...");
        return new String(input).toUpperCase().getBytes();
    }
}
