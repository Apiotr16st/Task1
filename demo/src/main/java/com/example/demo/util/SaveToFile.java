package com.example.demo.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class SaveToFile {
    public static void save(String filename, List<String> lines) throws IOException {
        Path path = Path.of(filename);
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.CREATE);
    }
}
