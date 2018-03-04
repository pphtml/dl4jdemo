package org.superbiz.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.superbiz.fetch.FetchFinViz;
import org.superbiz.guice.BasicModule;
import org.superbiz.service.TradingDayService;
import org.superbiz.util.GlobalInit;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainScheduler {
    static { GlobalInit.init(); }

    private static final Logger LOGGER = Logger.getLogger(MainScheduler.class.getName());

    public static void main(String[] args) throws SchedulerException {
        startScheduler();
        return;
    }

    public static void startScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        JobDetail job = JobBuilder.newJob(FetchFinVizJob.class)
                .withIdentity("myJob", "group1")
                .build();


        scheduler.start();


//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("myTrigger", "group1")
//                .startNow()
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
//                        .withIntervalInSeconds(40)
//                        .repeatForever())
//                .build();

        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger3", "group1")
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 07 16 * * ?"))
                //.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))
                //.forJob("myJob", "group1")
                .build();

        scheduler.scheduleJob(job, cronTrigger);
    }

    public static class FetchFinVizJob implements Job {
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            LOGGER.info("Starting");

            try {
                Injector injector = Guice.createInjector(new BasicModule());
                FetchFinViz fetchData = injector.getInstance(FetchFinViz.class);
                TradingDayService tradingDayService = injector.getInstance(TradingDayService.class);
                if (tradingDayService.isBeforeTradingStart()) {
                    fetchData.fetchAll();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new JobExecutionException(e);
            }

            LOGGER.info("Ending");
        }
    }
}
