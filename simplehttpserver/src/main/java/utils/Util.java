package utils;

public interface Util {
    static void info(String format, Object... args) {
        System.err.printf(format, args);
    }

    static void debug(String format, Object... args) {
        System.err.printf(format, args);
    }

    static void error(String format, Object... args) {
        System.err.printf(format, args);
    }
}
