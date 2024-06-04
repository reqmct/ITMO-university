package info.kgeorgiy.ja.shibanov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Java code generator based on the class description
 */
public class JavaCodeProducer {
    /**
     * Default constructor.
     */
    public JavaCodeProducer() {
    }

    /**
     * Returns the simple name of the token class with an implementation suffix.
     *
     * @param token the class token {@link Class}.
     * @return the class name with the implementation suffix as a {@link String}.
     */
    private static String getClassName(Class<?> token) {
        return token.getSimpleName() + Tokens.IMPLEMENTATION;
    }

    /**
     * Returns the method name as a {@link String} combining the return type and the method name.
     *
     * @param method the method to get the name from.
     * @return the method name as a {@link String}.
     */
    private static String getMethodName(Method method) {
        return String.join(Tokens.SPACE, method.getReturnType().getCanonicalName(), method.getName());
    }

    /**
     * Returns the constructor name as the simple name of the declaring class.
     *
     * @param constructor the {@link Constructor} to get the name from.
     * @return the constructor name as a {@link String}.
     */
    private static String getConstructorName(Constructor<?> constructor) {
        return getClassName(constructor.getDeclaringClass());
    }

    /**
     * Returns the arguments of the {@link Executable} as a {@link String}, optionally including the types.
     *
     * @param executable the {@link Executable} to get the arguments from.
     * @param needTypes  {@code true} if the types should be included in the string, {@code false} otherwise.
     * @return the arguments of the executable as a {@link String}.
     */
    private static String getArguments(Executable executable, boolean needTypes) {
        return Arrays.stream(executable.getParameters())
                .map(p -> (needTypes ? p.getType().getCanonicalName() + Tokens.SPACE : Tokens.EMPTY)
                        + p.getName())
                .collect(Collectors.joining(Tokens.PARAMETER_SEPARATOR));
    }

    /**
     * Returns the throws clause of the {@link Executable} as a {@link String}, or an {@link Tokens#EMPTY} if there are no exceptions thrown.
     *
     * @param executable the {@link Executable} to get the throws clause from.
     * @return the throws clause of the executable as a {@link String}.
     */
    private static String getThrows(Executable executable) {
        var exps = executable.getExceptionTypes();
        if (exps.length == 0) {
            return Tokens.EMPTY;
        }
        return String.format("throws %s", Arrays.stream(exps)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(Tokens.PARAMETER_SEPARATOR)));
    }

    /**
     * Returns the executable declaration as a {@link String}.
     * <p>
     * First it outputs the keyword public, then the declaration of the executable.
     *
     * @param executable the {@link Executable} to get the declaration from.
     * @param name       the name of the executable.
     * @return the executable declaration as a {@link String}.
     */
    private static String getExecutableDeclaration(Executable executable, String name) {
        return String.format("public %s(%s) %s {",
                name,
                getArguments(executable, true),
                getThrows(executable));
    }

    /**
     * Adds margins to a string.
     *
     * @param source the {@link String} to add margins to.
     * @param level  the level of margins to add.
     * @return the string with margins added.
     */
    private static String addMargins(String source, int level) {
        return Tokens.TABULATION.repeat(level) + source;
    }

    /**
     * Gets the implementation of {@link Executable} as a {@link String}.
     * <p>Combines the declaration and the body of the {@link Executable}.
     * Also adds margins {@link JavaCodeProducer#addMargins(String, int)}.
     *
     * @param <T>        the type of the {@link Executable}s.
     * @param executable the executable to get the string from.
     * @param getName    a {@link Function} that gets the name of the executable.
     * @param body       a {@link Function} that gets the body of the executable.
     * @return the executable as a {@link String}.
     */
    private static <T extends Executable> String getExecutable(T executable,
                                                               Function<T, String> getName,
                                                               Function<T, String> body) {
        return String.join(
                Tokens.LINE_SEPARATOR,
                addMargins(getExecutableDeclaration(executable, getName.apply(executable)), 1),
                addMargins(body.apply(executable), 2),
                addMargins(Tokens.RIGHT_CURLY_BRACE, 1)
        );
    }

    /**
     * Validates the parameters of the specified executables.
     * <p>It checks if any of the parameters are private, and if so, throws an {@link ImplerException}.
     *
     * @param parameters the parameters to validate.
     * @throws ImplerException if any of the parameters are private.
     */
    private static void validateParameters(Parameter[] parameters) throws ImplerException {
        for (Parameter p : parameters) {
            if (Modifier.isPrivate(p.getModifiers())) {
                throw new ImplerException("Incorrect parameter: " + p);
            }
        }
    }

    /**
     * Validates the return type of the specified token.
     * <p>It checks if the return type is private, and if so, throws an {@link ImplerException}.
     *
     * @param token the token to validate.
     * @throws ImplerException if the return type is private.
     */
    private static void validateReturnType(Class<?> token) throws ImplerException {
        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Incorrect parameter: " + token);
        }
    }

    /**
     * Validates the executables in the specified list.
     * <p>It calls {@link JavaCodeProducer#validateParameters(Parameter[])} and {@link JavaCodeProducer#validateReturnType(Class)}
     * on each {@link Executable}, and if any of the executables have invalid parameters or return types,
     * it throws an {@link ImplerException}.
     *
     * @param <T>             the type of the {@link Executable}s.
     * @param listExecutables the {@link List} of executables to validate.
     * @throws ImplerException if any of the executables have invalid parameters or return types.
     */
    private static <T extends Executable> void validateExecutables(List<T> listExecutables) throws ImplerException {
        for (T executable : listExecutables) {
            validateParameters(executable.getParameters());
            if (executable instanceof Method method) {
                validateReturnType(method.getReturnType());
            }
        }
    }

    /**
     * Gets the super constructor call for the specified constructor.
     * <p>It returns a {@link String} that represents the super constructor call, including the arguments.
     *
     * @param constructor the {@link Constructor} to get the super call for.
     * @return the super constructor call as a {@link String}.
     */
    private static String getSuper(Constructor<?> constructor) {
        return String.format("super(%s);", getArguments(constructor, false));
    }

    /**
     * Gets the constructor implementation for the specified constructor.
     * <p>It calls {@link JavaCodeProducer#getExecutable(Executable, Function, Function)}
     * and {@link JavaCodeProducer#getSuper(Constructor)} to generate the constructor implementation,
     * and returns it as a {@link String}.
     *
     * @param constructor the constructor to get the implementation for.
     * @return the constructor implementation as a {@link String}
     */
    private static String getConstructor(Constructor<?> constructor) {
        return getExecutable(constructor, JavaCodeProducer::getConstructorName, JavaCodeProducer::getSuper);
    }

    /**
     * Gets the constructors implementations for the specified token.
     * <p>It filters out any private constructors, and then calls {@link JavaCodeProducer#getExecutables(List, Function)}
     * on each non-private constructor.
     * It returns the constructor implementations as a {@link String}.
     *
     * @param token the token to get the constructor implementations for.
     * @return the constructor implementations as a {@link String}.
     * @throws ImplerException if there are no non-private constructors for the token.
     */
    private static String getConstructors(Class<?> token) throws ImplerException {
        List<Constructor<?>> constructors = Arrays.stream(token.getDeclaredConstructors())
                .filter(constructor -> !Modifier.isPrivate(constructor.getModifiers()))
                .toList();
        if (token.getDeclaredConstructors().length != 0 && constructors.isEmpty()) {
            throw new ImplerException("There are no constructors for class: " + token);
        }
        return getExecutables(constructors, JavaCodeProducer::getConstructor);
    }

    /**
     * Gets the executable implementations for the specified list of executables.
     *
     * @param <T>                the type of the {@link Executable}s.
     * @param executables        the {@link List} of {@link Executable} to get the implementations for.
     * @param executableToString a {@link Function} that converts an executable to a string.
     * @return the executable implementations as a {@link String}.
     * @throws ImplerException if any of the executables have invalid parameters or return types.
     */
    private static <T extends Executable> String getExecutables(List<T> executables,
                                                                Function<T, String> executableToString) throws ImplerException {
        validateExecutables(executables);
        return executables.stream()
                .map(executableToString)
                .collect(Collectors.joining(Tokens.LINE_SEPARATOR));
    }

    /**
     * Gets the default value for the specified token.
     * <p>If token is not primitive type, it returns {@code null}.
     *
     * @param token the token to get the default value for.
     * @return the default value as a {@link String}.
     */
    private static String getDefaultValue(Class<?> token) {
        if (!token.isPrimitive()) {
            return Tokens.NULL;
        } else if (token.equals(boolean.class)) {
            return Tokens.FALSE;
        } else if (token.equals(void.class)) {
            return Tokens.EMPTY;
        } else {
            return Tokens.ZERO;
        }
    }

    /**
     * Gets the return statement for the specified method.
     *
     * @param method the {@link Method} to get the return statement for.
     * @return the return statement as a {@link String}.
     */
    private static String getReturn(Method method) {
        return String.format("return %s;",
                getDefaultValue(method.getReturnType())
        );
    }

    /**
     * Gets the method implementation for the specified method.
     * <p>It calls {@link JavaCodeProducer#getExecutable(Executable, Function, Function)} to generate the method implementation,
     * and returns it as a {@link String}.
     *
     * @param method the {@link Method} to get the implementation for.
     * @return the method implementation as a {@link String}.
     */
    private static String getMethod(Method method) {
        return getExecutable(method, JavaCodeProducer::getMethodName, JavaCodeProducer::getReturn);
    }

    /**
     * Gets the method implementations for the specified token.
     * <p>It filters out any non-abstract methods, and then calls {@link JavaCodeProducer#getExecutables(List, Function)} on each abstract method.
     *
     * @param token the token to get the method implementations for.
     * @return the method implementations as a {@link String}.
     * @throws ImplerException if any of the methods have invalid parameters or return types.
     */
    private static String getMethods(Class<?> token) throws ImplerException {
        List<Method> methods = getAllAbstractMethods(token);
        return getExecutables(methods, JavaCodeProducer::getMethod);
    }

    /**
     * Gets all abstract methods for the specified token.
     * <p>It recursively traverses the token's superclasses and interfaces, and collects all abstract methods.
     *
     * @param token the token to get the abstract methods for.
     * @return {@link List} of abstract methods.
     */
    private static List<Method> getAllAbstractMethods(Class<?> token) {
        if (token == null) {
            return Collections.emptyList();
        }
        Set<MethodWrapper> methods = Arrays.stream(token.getMethods())
                .map(MethodWrapper::new)
                .collect(Collectors.toCollection(HashSet::new));
        while (token != null) {
            methods.addAll(
                    Arrays.stream(token.getDeclaredMethods())
                            .map(MethodWrapper::new)
                            .collect(Collectors.toCollection(HashSet::new))
            );
            token = token.getSuperclass();
        }
        return methods.stream()
                .map(MethodWrapper::method)
                .filter(method -> Modifier.isAbstract(method.getModifiers()))
                .collect(Collectors.toList());
    }

    /**
     * Gets the keyword for the specified token.
     *
     * @param token the token to get the keyword for.
     * @return "implements" if the token is an interface, and "extends" if the token is a class.
     */
    private static String getImplementsOrExtends(Class<?> token) {


        return token.isInterface() ? "implements" : "extends";
    }

    /**
     * Gets the class declaration for the specified token.
     * <p>
     * First it outputs the public class, then the declaration of the class.
     *
     * @param token the token to get the class declaration for.
     * @return the class declaration as a {@link String}.
     */
    private static String getClassDeclaration(Class<?> token) {
        return String.format("public class %s %s %s {",
                getClassName(token),
                getImplementsOrExtends(token),
                token.getCanonicalName());
    }

    /**
     * Gets the package for the specified token.
     * <p>It returns {@link Tokens#EMPTY} if there are no packages.
     *
     * @param tokenPackage the token's {@link Package}.
     * @return {@link String} that represents the package declaration, or an empty string if the token does not have a package.
     */
    private static String getPackage(Package tokenPackage) {
        return Objects.isNull(tokenPackage) ?
                Tokens.EMPTY :
                String.format("%s;", tokenPackage) + Tokens.LINE_SEPARATOR;
    }

    /**
     * Produces the Java code for the specified token.
     *
     * @param token the token to produce the Java code for.
     * @return the Java code as a {@link String}.
     * @throws ImplerException if there are any errors generating the Java code.
     */
    public static String produce(Class<?> token) throws ImplerException {
        return String.join(Tokens.LINE_SEPARATOR,
                getPackage(token.getPackage()),
                getClassDeclaration(token),
                getConstructors(token),
                getMethods(token),
                Tokens.RIGHT_CURLY_BRACE
        );
    }

    /**
     * Wrapper for {@link Method} with custom comparison and hashcode.
     *
     * @param method {@link Method} for the wrapper.
     */
    private record MethodWrapper(Method method) {
        /**
         * Custom implementation of the equals {@link Method} to compare methods based on their name, return type, and parameter types.
         *
         * @param obj the {@link Objects} to compare to.
         * @return {@code true} if the methods are equal, {@code false} otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MethodWrapper other) {
                if (method.getName().equals(other.method.getName())) {
                    if (!method.getReturnType().equals(other.method.getReturnType())) {
                        return false;
                    }
                    return Arrays.equals(method.getParameterTypes(), other.method.getParameterTypes());
                }
            }
            return false;
        }

        /**
         * Custom implementation of the hashCode {@link Method} to hash methods based on their name, return type, and parameter types.
         *
         * @return the hash code of the method.
         */
        @Override
        public int hashCode() {
            return Objects.hash(method.getName(), method.getReturnType(), Arrays.hashCode(method.getParameterTypes()));
        }
    }
}
