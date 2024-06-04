package info.kgeorgiy.ja.shibanov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Implementation of the {@link Impler} and {@link JarImpler} interfaces.
 *
 * <p>This class provides the implementation for generating Java code and compiling it into a JAR file.
 */
public class Implementor implements Impler, JarImpler {
    /**
     * Default constructor.
     */
    public Implementor() {
    }

    /**
     * Implements the given class or interface by generating Java code.
     *
     * <p>Token must be valid {@link Implementor#validateToken(Class)}.
     *
     * @param token the class or interface to implement.
     * @param root  the root directory for the generated code.
     * @throws ImplerException if the class or interface cannot be implemented.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        validateToken(token);
        String javaCode = toUnicode(JavaCodeProducer.produce(token));
        root = root.resolve(getPackageNameByToken(token));
        try {
            Files.createDirectories(root);
        } catch (IOException ignored) {
        }
        var path = root.resolve(getFileNameByToken(token));
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            bufferedWriter.write(javaCode);
        } catch (IOException e) {
            throw new ImplerException("Output file error: " + e);
        }
    }

    /**
     * Converts the given string to Unicode.
     *
     * @param source the string to convert.
     * @return the Unicode representation of the string.
     */
    private static String toUnicode(String source) {
        return source.chars().mapToObj(c -> String.format("\\u%04X", c)).collect(Collectors.joining());
    }

    /**
     * Gets the name of this class or interface.
     *
     * @param token the class or interface to get the name for.
     * @return the name of the class or interface.
     */

    private static String getNameByToken(Class<?> token) {
        return token.getSimpleName() + Tokens.IMPLEMENTATION;
    }

    /**
     * Gets the file name of this class or interface.
     *
     * @param token the class or interface to get the name for.
     * @return the package file name of the class or interface.
     */
    private static String getFileNameByToken(Class<?> token) {
        return getNameByToken(token) + ".java";
    }

    /**
     * Gets the file name of this class or interface.
     *
     * @param token the class or interface to get the name for.
     * @return the package file name of the class or interface.
     */
    private static String getPackageNameByToken(Class<?> token) {
        return getPackageNameByToken(token, File.separatorChar);
    }

    /**
     * Gets the package name for the given class or interface.
     *
     * @param token         the class or interface to get the package name for.
     * @param separatorChar the character to use as the file separator.
     * @return the package name for the class or interface.
     */
    private static String getPackageNameByToken(Class<?> token, char separatorChar) {
        return token.getPackageName().replace('.', separatorChar);
    }

    /**
     * {@link Record} representing a validator.
     *
     * @param predicate {@link Predicate} for validation.
     * @param message   {@link String} to put in exception.
     */
    private record Validator(Predicate<Class<?>> predicate, String message) {
        /**
         * Validates the given class or interface.
         *
         * @param token the class or interface to validate.
         * @throws ImplerException if the class or interface is invalid.
         */
        public void validate(final Class<?> token) throws ImplerException {
            if (predicate.test(token)) {
                throw new ImplerException(String.format("Token is %s: %s", message, token));
            }
        }
    }

    /**
     * The list of validators used to validate the given class or interface.
     * <p>The token is valid if it is not
     *
     * <ul>
     * <li>null</li>
     * <li>primitive</li>
     * <li>record</li>
     * <li>enum</li>
     * <li>array</li>
     * <li>array</li>
     * <li>private</li>
     * </ul>
     */
    private final List<Validator> validators = List.of(
            new Validator(Objects::isNull, "null"),
            new Validator(Class::isPrimitive, "primitive"),
            new Validator(token -> token.equals(Enum.class), "enum"),
            new Validator(Class::isArray, "array"),
            new Validator(token -> Modifier.isFinal(token.getModifiers()), "final"),
            new Validator(token -> Modifier.isPrivate(token.getModifiers()), "private"),
            new Validator(token -> token.equals(Record.class), "record")
    );

    /**
     * Validates the given class or interface.
     * <p>Check the entire list of validators {@link Implementor#validators}.
     *
     * @param token the class or interface to validate.
     * @throws ImplerException if the class or interface is invalid.
     */
    private void validateToken(Class<?> token) throws ImplerException {
        for (var validator : validators) {
            validator.validate(token);
        }
    }


    /**
     * Compiles the given class or interface.
     *
     * @param token the class or interface to compile.
     * @param root  the root directory for the compiled class or interface.
     * @throws ImplerException if the class or interface cannot be compiled.
     */
    private static void compile(final Class<?> token, final Path root) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Could not find java compiler");
        }
        final String classpath = root + File.pathSeparator + getClassPath(token);
        final Path filePath = root.resolve(getPackageNameByToken(token))
                .resolve(getFileNameByToken(token));
        final List<String> args = List.of(filePath.toString(), "-cp", classpath);
        final int exitCode = compiler.run(null, null, null, args.toArray(new String[0]));
        if (exitCode != 0) {
            throw new ImplerException("Compiler exit code " + exitCode);
        }
    }

    /**
     * Gets the classpath for the given class or interface.
     *
     * @param token the class or interface to get the classpath for.
     * @return the classpath for the class or interface.
     */
    private static String getClassPath(Class<?> token) {
        try {
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }


    /**
     * Generate Jar file for the given class or interface.
     *
     * @param token   the class or interface to create a manifest for.
     * @param root    the root directory for the compiled class or interface.
     * @param jarFile the JAR file in which the manifest will be written.
     * @throws ImplerException if the manifest cannot be created.
     */
    private void generateJar(Class<?> token, Path root, Path jarFile) throws ImplerException {
        Manifest manifest = new Manifest();
        String className = String.format("%s/%s.class",
                getPackageNameByToken(token, '/'),
                getNameByToken(token));
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(Attributes.Name.SPECIFICATION_VENDOR, "BOB_ITMO");
        try (JarOutputStream jarOutputStream = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            jarOutputStream.putNextEntry(new ZipEntry(className));
            Files.copy(Path.of(root.toString(), className), jarOutputStream);
        } catch (IOException e) {
            throw new ImplerException("Writing to jar exception: " + e);
        }
    }

    /**
     * Implements class of interface in jar file.
     * <p> Creates a temporary directory where it places the compiled files.
     * After that, it transfers the compiled files to the jar and deletes the temporary directory.
     *
     * @param token   the class or interface to implement.
     * @param jarFile the jar file.
     * @throws ImplerException if there are problems with implementing.
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        validateToken(token);
        if (jarFile.getParent() == null) {
            throw new ImplerException("There are no parent folders: " + jarFile);
        }
        try {
            Files.createDirectories(jarFile.getParent());
        } catch (IOException ignored) {
        }
        try {
            Path tmp = Files.createTempDirectory(jarFile.getParent(), "temp");
            try {
                implement(token, tmp);
                compile(token, tmp);
                generateJar(token, tmp, jarFile);
            } finally {
                Files.walkFileTree(tmp, new DeletePathVisitor());
            }
        } catch (IOException e) {
            throw new ImplerException("Can't create temp directory", e);
        }
    }

    /**
     * A FileVisitor implementation that deletes files and directories recursively.
     */
    private static class DeletePathVisitor extends SimpleFileVisitor<Path> {
        /**
         * Default constructor.
         */
        public DeletePathVisitor() {

        }

        /**
         * Deletes the specified file.
         *
         * @param file  the {@link Path} to the file to be deleted
         * @param attrs the basic file attributes of the file
         * @return {@link FileVisitResult#CONTINUE} indicating that the operation should continue
         * @throws IOException if an I/O error occurs while deleting the file
         */
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * Deletes the specified directory after all its entries have been visited.
         *
         * @param dir the {@link Path} to the directory to be deleted
         * @param exc an IOException that is thrown if the directory could not be visited
         * @return {@link FileVisitResult#CONTINUE} indicating that the operation should continue
         * @throws IOException if an I/O error occurs while deleting the directory
         */
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }

    /**
     * Check if the given arguments point to a JAR implementation request.
     *
     * <p>There should be 3 arguments and the first argument should be "-jar".
     *
     * @param args the arguments to check.
     * @return {@code true} if the arguments indicate a JAR implementation request, {@code false} otherwise.
     */
    private static boolean isJarImplement(String[] args) {
        return args != null && args.length == 3 && "-jar".equals(args[0]);
    }

    /**
     * Check if the given arguments indicate an implementation request.
     *
     * <p>There should be 2 arguments.
     *
     * @param args the arguments to check
     * @return {@code true} if the arguments indicate an implementation request, {@code false} otherwise.
     */
    private static boolean isImplement(String[] args) {
        return args.length == 2;
    }

    /**
     * Main method of the implementor application.
     * <p>Calls the desired method of the {@link Impler} or {@link JarImpler} interfaces.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        if (Objects.isNull(args)) {
            System.err.println("There are no args");
            return;
        }
        try {
            Implementor implementor = new Implementor();
            if (isImplement(args)) {
                implementor.implement(Class.forName(args[0]), Path.of(args[1]));
            } else if (isJarImplement(args)) {
                implementor.implementJar(Class.forName(args[1]), Path.of(args[2]));
            } else {
                System.err.println("Incorrect args");
            }
        } catch (ImplerException e) {
            System.err.printf("Implementor Exception: %s", e);
        } catch (ClassNotFoundException e) {
            System.err.printf("Incorrect class name %s", e);
        }
    }
}
