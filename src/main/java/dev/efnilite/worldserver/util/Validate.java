package dev.efnilite.worldserver.util;

import dev.efnilite.vilib.util.Logging;

public class Validate {

    public static <T> void notNull(T value, String error) {
        if (value == null) {
            Logging.stack("Value is null", error, new IllegalArgumentException());
        }
    }

    public static void isTrue(boolean expression, String error) {
        if (!expression) {
            Logging.stack("Invalid expression value", error, new IllegalArgumentException());
        }
    }

}