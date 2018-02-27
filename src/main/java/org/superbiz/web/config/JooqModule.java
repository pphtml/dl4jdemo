package org.superbiz.web.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.superbiz.web.service.EmployeeRepository;
import org.superbiz.web.service.EmployeeRepositoryImpl;

import javax.inject.Singleton;
import javax.sql.DataSource;

public class JooqModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EmployeeRepository.class).to(EmployeeRepositoryImpl.class).in(Scopes.SINGLETON);
//        bind(EmployeeServicePublisher.class).to(EmployeeServicePublisherImpl.class).in(Scopes.SINGLETON);
//        bind(EmployeeServiceRx.class).to(EmployeeServiceRxImpl.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public DSLContext dslContext(DataSource dataSource) {
        return DSL.using(new DefaultConfiguration().derive(dataSource));
    }
}
