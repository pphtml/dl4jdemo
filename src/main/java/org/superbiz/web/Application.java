package org.superbiz.web;

import com.zaxxer.hikari.HikariConfig;
import org.superbiz.util.LoggingConfig;
import org.superbiz.web.config.ApplicationModule;
import org.superbiz.web.config.JooqModule;
import ratpack.error.ClientErrorHandler;
import ratpack.guice.Guice;
import ratpack.hikari.HikariModule;
import ratpack.server.RatpackServer;
import ratpack.service.Service;
import ratpack.service.StartEvent;

import java.time.Duration;
import java.util.logging.Logger;

public class Application {
    static {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
        System.setProperty("org.jooq.no-logo", "true");
    }

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) throws Exception {
        // Registry.builder().add(FileRenderer.TYPE, FileRenderer.NON_CACHING);
        RatpackServer.start(server -> server
            .serverConfig(config -> config
                .findBaseDir()
                .sysProps()) // not needed
            .registry(Guice.registry(bindings -> bindings
                .moduleConfig(ApplicationModule.class, bindings.getServerConfig().get("/user", ApplicationModule.Config.class))
                .moduleConfig(HikariModule.class, getHikariConfig())
                .module(JooqModule.class)
//                .module(SessionModule.class, cfg -> cfg.expires(Duration.ofDays(7)).idName("BLE"))
//                .module(ClientSideSessionModule.class, cfg -> {
//                    cfg.setSessionCookieName("rp_session");
//                    cfg.setSecretToken(Double.valueOf(Math.floor(System.currentTimeMillis() / 10000)).toString());
//                    cfg.setSecretKey("fsoiure!#478Fr?$");
//                    cfg.setMacAlgorithm("HmacSHA1");
//                    cfg.setCipherAlgorithm("AES/CBC/PKCS5Padding");
//                    cfg.setMaxSessionCookieSize(1932);
//                    cfg.setMaxInactivityInterval(Duration.ofHours(24));
//                })
//                .bindInstance(ClientErrorHandler.class, new CustomErrorHandler())
                //.bindInstance(FileRenderer.class, (FileRenderer) FileRenderer.NON_CACHING)
//                .bind(EmployeeChainAction.class)
//                .bind(ApiChainAction.class)
//                .bind(SessionChainAction.class)
            ))
            .handlers(chain -> chain
//                .get("foo/:id", ctx -> ctx.render("Foo " + ctx.getPathTokens().get("id")))
//                .prefix("employee", EmployeeChainAction.class) // clean up
//                .prefix("api", ApiChainAction.class)
//                .prefix("session", SessionChainAction.class)
//                .when(false, action -> action
                        .files(f -> f.dir("static"))
                        .all(ctx -> ctx.render(ctx.file("static/index.html")))
//                )
//                .when(true, action -> action // mozna cely vyhodit
//                        .all(ctx -> {
//                            final String fileName = ctx.getRequest().getPath();
//                            final Path path = ctx.file("static/" + fileName);
//                            final Path pathOrIndex = !fileName.isEmpty() && Files.exists(path) ? path : ctx.file("static/index.html");
//                            logger.info(String.format("Rendering: %s", pathOrIndex));
//                            //FileRenderer.NON_CACHING.render(ctx, path);
//                            ctx.render(pathOrIndex);
//                        }))
        ));
    }

    private static HikariConfig getHikariConfig() {
        final String url = System.getenv("JDBC_DATABASE_URL");;
        if (url == null) {
            throw new RuntimeException("Environment variable or property JDBC_DATABASE_URL not set");
        }
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        return config;
    }

    private static boolean getDevMode() {
        String devMode = System.getProperty("devMode", "true");
        return "true".equalsIgnoreCase(devMode);
    }
}
