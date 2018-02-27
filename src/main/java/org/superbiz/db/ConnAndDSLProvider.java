package org.superbiz.db;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnAndDSLProvider {
    @Inject
    DataSource dataSource;

    public ConnAndDSLProvider() {}

    public ConnAndDSL3 create() {
        try {
            //ConnAndDSLProvider result = new ConnAndDSLProvider();
            Connection connection = dataSource.getConnection();
            DSLContext dsl = DSL.using(connection, SQLDialect.POSTGRES);
            return new ConnAndDSL3(connection, dsl);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
