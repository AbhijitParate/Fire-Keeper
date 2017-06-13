package com.github.abhijitpparate.keeps.scheduler

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * This is what is going to be used in production environment
 * Created by abhij on 5/1/2017.
 */

class DevelopmentSchedulerProvider private constructor() : SchedulerProvider {

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    companion object {

        private var INSTANCE: DevelopmentSchedulerProvider? = null

        fun newInstance(): DevelopmentSchedulerProvider {
            if (INSTANCE == null) INSTANCE = DevelopmentSchedulerProvider()
            return INSTANCE as DevelopmentSchedulerProvider
        }
    }
}
