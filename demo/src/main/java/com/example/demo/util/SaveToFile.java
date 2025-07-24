package com.example.demo.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SaveToFile {
    public static void save(String filename, String lines) throws IOException {
        Path path = Path.of(filename);
        Files.write(path, (lines + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
