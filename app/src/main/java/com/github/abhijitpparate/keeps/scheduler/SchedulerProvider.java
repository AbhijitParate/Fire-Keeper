package com.github.abhijitpparate.keeps.scheduler;

import io.reactivex.Scheduler;

public interface SchedulerProvider {
    Scheduler computation();
    Scheduler io();
    Scheduler ui();
}
