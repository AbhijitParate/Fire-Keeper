package com.github.abhijitpparate.keeper.data.database


object DatabaseInjector {

    val databaseSource: DatabaseSource
        get() = FirebaseDatabaseService.instance
}