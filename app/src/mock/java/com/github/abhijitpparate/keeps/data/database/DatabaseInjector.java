package com.github.abhijitpparate.keeps.data.database;

public class DatabaseInjector {

    public static DatabaseSource getDatabaseSource(){
        return new MockDatabaseSource();
    }
}