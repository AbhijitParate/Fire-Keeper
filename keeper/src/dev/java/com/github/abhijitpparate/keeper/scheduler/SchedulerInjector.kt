package com.github.abhijitpparate.keeper.scheduler


object SchedulerInjector {

    val scheduler: DevelopmentSchedulerProvider
        get() = com.github.abhijitpparate.keeper.scheduler.DevelopmentSchedulerProvider.Companion.newInstance()

}