package com.github.abhijitpparate.keeps.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class SchedulerInjector implements SchedulerProvider {

    private static SchedulerInjector INSTANCE;

    public static SchedulerInjector getScheduler() {
        if (INSTANCE == null) INSTANCE = new SchedulerInjector();
        return INSTANCE;
    }

    private SchedulerInjector() {}

    @Override
    public Scheduler computation() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler io() {
        return Schedulers.trampoline();
    }

    @Override
    public Scheduler ui() {
        return Schedulers.trampoline();
    }
}
