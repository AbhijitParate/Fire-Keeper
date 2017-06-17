package com.github.abhijitpparate.keeper.scheduler;


public class SchedulerInjector {

    public static DevelopmentSchedulerProvider getScheduler() {
        return DevelopmentSchedulerProvider.newInstance();
    }

}