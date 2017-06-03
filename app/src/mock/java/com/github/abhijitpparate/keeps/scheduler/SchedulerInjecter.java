package com.github.abhijitpparate.keeps.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class SchedulerInjecter implements SchedulerProvider {

    private static SchedulerInjecter INSTANCE;

    public static SchedulerInjecter getScheduler() {
        if (INSTANCE == null) INSTANCE = new SchedulerInjecter();
        return INSTANCE;
    }

    private SchedulerInjecter() {}

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
