package org.superbiz.util;

public class GlobalInit {
    static {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
        System.setProperty("org.jooq.no-logo", "true");
    }
}
