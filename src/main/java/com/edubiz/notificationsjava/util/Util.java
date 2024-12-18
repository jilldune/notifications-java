package com.edubiz.notificationsjava.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Util {
    public static void timeOut(Runnable taskFunc,int durationInMilliseconds) {
        ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);
        schedule.schedule(() -> {
            taskFunc.run();
            schedule.shutdown();
        },durationInMilliseconds, TimeUnit.MILLISECONDS);
    }
}
