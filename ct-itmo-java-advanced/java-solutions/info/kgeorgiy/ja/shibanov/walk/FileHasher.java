package info.kgeorgiy.ja.shibanov.walk;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHasher {
    private static final int BUFFER_SIZE = 1024;

    public static int getHash(Path filePath, Hasher hasher) {
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                hasher.updateHash(buffer, length);
            }
            return hasher.getHash();
        } catch (IOException e) {
            return 0;
        }
    }
}

