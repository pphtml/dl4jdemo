package org.superbiz.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {
    private ConnectionPool() {
    }
    /*
     * Expects a config in the following format
     *
     * poolName = "test pool"
     * jdbcUrl = ""
     * maximumPoolSize = 10
     * minimumIdle = 2
     * username = ""
     * password = ""
     * cachePrepStmts = true
     * prepStmtCacheSize = 256
     * prepStmtCacheSqlLimit = 2048
     * useServerPrepStmts = true
     *
     * Let HikariCP bleed out here on purpose
     */
    public static HikariDataSource getDataSource() {
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName("MyHikariCP");
        jdbcConfig.setMaximumPoolSize(4);
        jdbcConfig.setMinimumIdle(2);
        //jdbcConfig.setMinimumIdle(conf.getInt("minimumIdle"));
        String url = System.getenv("JDBC_DATABASE_URL");
        if (url == null || url.length() == 0) {
            throw new RuntimeException("JDBC_DATABASE_URL is supposed to be provided.");
        }
        jdbcConfig.setJdbcUrl(url);
        jdbcConfig.addDataSourceProperty("cachePrepStmts", true);
        jdbcConfig.addDataSourceProperty("prepStmtCacheSize", true);
//        jdbcConfig.addDataSourceProperty("prepStmtCacheSqlLimit", conf.getInt("prepStmtCacheSqlLimit"));
//        jdbcConfig.addDataSourceProperty("useServerPrepStmts", conf.getBoolean("useServerPrepStmts"));
        return new HikariDataSource(jdbcConfig);
    }
}
