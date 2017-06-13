package com.github.abhijitpparate.keeps.scheduler

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun computation(): Scheduler
    fun io(): Scheduler
    fun ui(): Scheduler
}
