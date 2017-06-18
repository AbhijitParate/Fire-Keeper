package com.github.abhijitpparate.keeper.data.storage;


public class StorageInjector {

    public static StorageSource getStorageSource() {
        return MockStorageSource.getInstance();
    }
}
