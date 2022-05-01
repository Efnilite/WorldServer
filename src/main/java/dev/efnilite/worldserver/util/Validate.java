package dev.efnilite.worldserver.util;

import dev.efnilite.worldserver.WorldServer;

public class Validate {

    public static <T> void notNull(T value, String error) {
        if (value == null) {
            WorldServer.logging().stack("Value is null", error, new IllegalArgumentException());
        }
    }

    public static void isTrue(boolean expression, String error) {
        if (!expression) {
            WorldServer.logging().stack("Invalid expression value", error, new IllegalArgumentException());
        }
    }

}