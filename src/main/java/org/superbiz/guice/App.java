package org.superbiz.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.superbiz.util.LoggingConfig;

public class App {
    static {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new BasicModule());
        SecurityListApp securityListApp = injector.getInstance(SecurityListApp.class);
        securityListApp.list();
    }
}
