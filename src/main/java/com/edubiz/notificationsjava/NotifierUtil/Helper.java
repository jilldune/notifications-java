package com.edubiz.notificationsjava.NotifierUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Helper {
    public static void timeOut(Runnable taskFunc,double durationSeconds) {
        ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
        schedule.schedule(() -> {
            taskFunc.run();
            schedule.shutdown();
        },(long) durationSeconds, TimeUnit.SECONDS);
    }
}
