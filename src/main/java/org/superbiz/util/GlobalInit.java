package org.superbiz.util;

public class GlobalInit {
    static {
        // System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
        System.setProperty("org.jooq.no-logo", "true");
        System.setProperty("org.quartz.threadPool.threadCount", "1");
    }

    public static void init() {
    }
}
