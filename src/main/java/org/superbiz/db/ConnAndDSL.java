package org.superbiz.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnAndDSL implements AutoCloseable {
    private DSLContext dsl;
    private Connection conn;

    private ConnAndDSL() {}

    public static ConnAndDSL create() {
        try {
            String url = System.getenv("JDBC_DATABASE_URL");
            Connection conn = DriverManager.getConnection(url);
            ConnAndDSL result = new ConnAndDSL();
            result.dsl = DSL.using(conn, SQLDialect.POSTGRES);
            result.conn = conn;

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public DSLContext getDsl() {
        return dsl;
    }

    @Override
    public void close() {
        try {
            if (this.dsl != null) {
                this.dsl.close();
            }
        } finally {
            if (this.conn != null) {
                try {
                    this.conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
