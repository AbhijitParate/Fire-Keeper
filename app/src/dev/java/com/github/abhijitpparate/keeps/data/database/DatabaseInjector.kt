package com.github.abhijitpparate.keeps.data.database


object DatabaseInjector {

    val databaseSource: DatabaseSource
        get() = FirebaseDatabaseService.instance
}