package org.brutality.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugHelper {
    private static final Logger logger = LogManager.getLogger();

    public static void debug(String str) {
        logger.debug("[{}] {}", computeString(), str);
    }

    private static String computeString() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String className = stackTraceElement.getClassName();
        return String.valueOf(new StringBuilder().append(className.substring(className.lastIndexOf(46) + 1)).append(".").append(stackTraceElement.getMethodName()));
    }

    public static void warn(String str) {
        logger.warn("[{}] {}", computeString(), str);
    }

    public static void info(String str) {
        logger.info("[{}] {}", computeString(), str);
    }

    public static void error(String str) {
        logger.error("[{}] {}", computeString(), str);
    }

    public static void error(String str, Throwable t) {
        logger.error("[{}] {}", computeString(), str, t);
    }
}
