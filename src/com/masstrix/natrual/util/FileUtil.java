package com.masstrix.natrual.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtil {

    public static void writeData(String path, byte[] data) throws IOException {
        if (!Files.exists(Paths.get(path)))
            Files.createDirectories(Paths.get(path).getParent());
        Files.write(Paths.get(path), data);
    }

    public static byte[] readData(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static void asyncWriteData(final String path, final byte[] data) {
        new Thread(() -> {
            try {
                if (!Files.exists(Paths.get(path)))
                    Files.createDirectories(Paths.get(path).getParent());
                Files.write(Paths.get(path), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).run();
    }
}
