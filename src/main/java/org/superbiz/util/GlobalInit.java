package org.superbiz.util;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;

public class GlobalInit {
    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.toLevel("info"));

        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
        System.setProperty("org.jooq.no-logo", "true");
    }

    public static void init() {
    }
}
