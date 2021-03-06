package org.superbiz.scheduler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.superbiz.fetch.FetchData;
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
        JobDetail job = JobBuilder.newJob(MaintenanceJob.class)
                .withIdentity("myJob", "group1")
                .build();


        scheduler.start();

        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger3", "group1")
//                .withSchedule(CronScheduleBuilder.cronSchedule("0 07 16 * * ?"))
                //.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/15 * * * ?"))
                //.forJob("myJob", "group1")
                .build();

        scheduler.scheduleJob(job, cronTrigger);
    }

    public static class MaintenanceJob implements Job {
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            LOGGER.info("Starting");

            try {
                Injector injector = Guice.createInjector(new BasicModule());
                FetchFinViz fetchFinViz = injector.getInstance(FetchFinViz.class);
                FetchData fetchData = injector.getInstance(FetchData.class);
                TradingDayService tradingDayService = injector.getInstance(TradingDayService.class);

                if (tradingDayService.isBeforeTradingStart()) {
                    fetchFinViz.fetchAll();
                }

                if (tradingDayService.isPricesReadyForBackup()) {
                    fetchData.fetchAllIntervals();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new JobExecutionException(e);
            }

            LOGGER.info("Ending");
        }
    }
}
