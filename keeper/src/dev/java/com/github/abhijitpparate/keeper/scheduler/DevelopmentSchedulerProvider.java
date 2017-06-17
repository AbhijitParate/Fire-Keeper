package com.github.abhijitpparate.keeper.scheduler;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * This is what is going to be used in production environment
 * Created by abhij on 5/1/2017.
 */

public class DevelopmentSchedulerProvider implements SchedulerProvider {

    private static DevelopmentSchedulerProvider INSTANCE;

    private DevelopmentSchedulerProvider() {}

    public static DevelopmentSchedulerProvider newInstance() {
        if (INSTANCE == null) INSTANCE = new DevelopmentSchedulerProvider();
        return INSTANCE;
    }

    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
