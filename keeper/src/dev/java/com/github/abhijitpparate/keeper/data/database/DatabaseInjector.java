package com.github.abhijitpparate.keeper.data.database;


public class DatabaseInjector {

    public static DatabaseSource getDatabaseSource() {
        return FirebaseDatabaseService.getInstance();
    }
}