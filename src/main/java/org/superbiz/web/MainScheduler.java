package org.superbiz.web;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.superbiz.util.GlobalInit;

import java.util.logging.Logger;

public class MainScheduler {
    static { GlobalInit.init(); }

    private static final Logger LOGGER = Logger.getLogger(MainScheduler.class.getName());

    public static void main(String[] args) throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        JobDetail job = JobBuilder.newJob(SimpleJob.class)
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
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                //.withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))
                //.forJob("myJob", "group1")
                .build();

        scheduler.scheduleJob(job, cronTrigger);
    }

    public static class SimpleJob implements Job {
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            LOGGER.info("This is a quartz job!");
        }
    }
}
