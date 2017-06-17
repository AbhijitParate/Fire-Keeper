package com.github.abhijitpparate.keeps.scheduler;


public class SchedulerInjector {

    public static DevelopmentSchedulerProvider getScheduler() {
        return DevelopmentSchedulerProvider.newInstance();
    }

}