package com.github.abhijitpparate.keeps.scheduler


object SchedulerInjector {

    val scheduler: DevelopmentSchedulerProvider
        get() = DevelopmentSchedulerProvider.newInstance()

}