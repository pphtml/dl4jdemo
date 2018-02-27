package org.superbiz.web.config;

import com.google.inject.Provides;
import org.superbiz.web.service.EmployeeRepository;
import org.superbiz.web.service.EmployeeService;
import org.superbiz.web.service.EmployeeServiceImpl;
import ratpack.guice.ConfigurableModule;

import javax.inject.Singleton;

public class ApplicationModule extends ConfigurableModule<ApplicationModule.Config> {
    public static class Config {
        private String nodeName;

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Config{");
            sb.append("nodeName='").append(nodeName).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    protected void configure() {
//        bind(RegexService.class).to(RegexServiceImpl.class).in(Scopes.SINGLETON);
//        bind(TaskExecutor.class).to(TaskExecutorImpl.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public EmployeeService employeeService(EmployeeRepository employeeRepository) {
        return new EmployeeServiceImpl(employeeRepository);
    }
}
