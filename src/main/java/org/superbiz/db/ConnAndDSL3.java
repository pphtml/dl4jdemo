package org.superbiz.db;

import org.jooq.DSLContext;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnAndDSL3 implements AutoCloseable {
    private final Connection connection;
    private final DSLContext dsl;

    public ConnAndDSL3(Connection connection, DSLContext dsl) {
        this.connection = connection;
        this.dsl = dsl;
    }

    @Override
    public void close() {
        try {
            if (this.dsl != null) {
                this.dsl.close();
            }
        } finally {
            if (this.connection != null) {
                try {
                    this.connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public DSLContext getDsl() {
        return dsl;
    }
}
