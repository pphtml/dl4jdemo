package org.superbiz.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.superbiz.dao.SecurityDAO;
import org.superbiz.db.ConnAndDSLProvider;
import org.superbiz.db.ConnectionPool;
import org.superbiz.util.HttpUtils;
import org.superbiz.util.LoggingConfig;

import javax.sql.DataSource;

import static org.superbiz.db.ConnectionPool.HIKARI_DATA_SOURCE;

public class BasicModule extends AbstractModule {
    // http://www.baeldung.com/guice

    @Override
    protected void configure() {
        System.setProperty("java.util.logging.config.class", LoggingConfig.class.getName());
        System.setProperty("org.jooq.no-logo", "true");

        bind(SecurityDAO.class);
        bind(SecurityListApp.class);
        bind(DataSource.class).toInstance(HIKARI_DATA_SOURCE);
        bind(ConnAndDSLProvider.class).in(Scopes.SINGLETON); // TODO vyzkouset kolikrat se vola
        bind(DefaultAsyncHttpClientConfig.Builder.class).toInstance(HttpUtils.getHttpAgentBuilder());
    }
}
