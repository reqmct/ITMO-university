package info.kgeorgiy.ja.shibanov.bank.validation;

import java.util.Arrays;

public class Validator {
    public static void validate(String... args) {
        boolean validation = Arrays.stream(args)
                .anyMatch(arg -> arg == null || arg.isBlank());

        if(validation) {
            throw new IllegalArgumentException();
        }
    }
}
