package com.github.abhijitpparate.keeps.scheduler

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class SchedulerInjector private constructor() : SchedulerProvider {

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }

    private object Holder { val INSTANCE = SchedulerInjector() }

    companion object {

        val INSTANCE: SchedulerInjector by lazy { Holder.INSTANCE }

        val scheduler: SchedulerInjector
            get() {
                return INSTANCE
            }
    }
}
