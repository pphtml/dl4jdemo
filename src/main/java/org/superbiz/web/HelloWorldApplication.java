package org.superbiz.web;

//import com.hubspot.dropwizard.guice.GuiceBundle;
//import io.dropwizard.Application;
//import io.dropwizard.setup.Bootstrap;
//import io.dropwizard.setup.Environment;
import org.superbiz.guice.BasicModule;

public class HelloWorldApplication { //extends Application<HelloWorldConfiguration> {
//    // https://github.com/HubSpot/dropwizard-guice
//    private GuiceBundle<HelloWorldConfiguration> guiceBundle;
//
//    public static void main(String[] args) throws Exception {
//        new HelloWorldApplication().run(args);
//    }
//
//    @Override
//    public void initialize(Bootstrap<HelloWorldConfiguration> bootstrap) {
//
//        guiceBundle = GuiceBundle.<HelloWorldConfiguration>newBuilder()
//                .addModule(new BasicModule())
//                .setConfigClass(HelloWorldConfiguration.class)
//                .build();
//
//        bootstrap.addBundle(guiceBundle);
//    }
//
//    @Override
//    public String getName() {
//        return "hello-world";
//    }
//
//    @Override
//    public void run(HelloWorldConfiguration helloWorldConfiguration, Environment environment) throws Exception {
////        environment.jersey().register(HelloWorldResource.class);
////        environment.lifecycle().manage(guiceBundle.getInjector().getInstance(TemplateHealthCheck.class));
//    }
}
