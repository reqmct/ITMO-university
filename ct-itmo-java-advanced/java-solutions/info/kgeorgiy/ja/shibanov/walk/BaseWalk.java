package info.kgeorgiy.ja.shibanov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class BaseWalk {
    public static void walk(String[] args, int height) throws WalkException {
        if (args == null || args.length != 2) {
            throw new WalkException("Incorrect number of arguments");
        }
        if (args[0] == null || args[1] == null) {
            throw new WalkException("Incorrect arguments");
        }
        try {
            Path output = Paths.get(args[1]);
            if (output.getParent() != null) {
                Files.createDirectories(output.getParent());
            }
        } catch (InvalidPathException e) {
            throw new WalkException("Incorrect output path: ", e);
        } catch (IOException | SecurityException e) {
            throw new WalkException("Can't create directories: ", e);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(args[0], StandardCharsets.UTF_8))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[1], StandardCharsets.UTF_8))) {
                String currFile;
                CustomFileVisitor visitor = new CustomFileVisitor(writer);
                while ((currFile = reader.readLine()) != null) {
                    try {
                        Path start = Path.of(currFile);
                        if (height == -1) {
                            Files.walkFileTree(start, visitor);
                        } else {
                            Files.walkFileTree(start, Collections.emptySet(), height, visitor);
                        }
                    } catch (InvalidPathException e) {
                        writer.write(String.format("%08x", 0) + " " + currFile);
                        writer.newLine();
                    }
                }
            } catch (SecurityException e) {
                throw new WalkException("Can't open file: ", e);
            } catch (IOException e) {
                throw new WalkException("Incorrect output file: ", e);
            }
        } catch (SecurityException e) {
            throw new WalkException("Can't open input file: ", e);
        } catch (IOException e) {
            throw new WalkException("Incorrect input file: ", e);
        }
    }
}
