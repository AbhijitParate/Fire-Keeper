package com.github.abhijitpparate.keeper.data.storage;


import android.net.Uri;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class MockStorageSource implements StorageSource {

    private boolean returnFailure = false;

    @Override
    public void setReturnFail(boolean bool) {
        this.returnFailure = bool;
    }

    @Override
    public Maybe<File> getFile(String uid, File file) {
        if (returnFailure){
            return Maybe.error(new Exception("Get profile failed"));
        }
        return Maybe.just(new File());
    }

    @Override
    public Maybe<File> createFile(String uid, String name, String type, Uri uri) {
        if (returnFailure){
            return Maybe.error(new Exception("Get profile failed"));
        }
        return Maybe.just(new File());
    }

    @Override
    public Completable deleteFile(String uid, File file) {
        if (returnFailure){
            return Completable.error(new Exception("Get profile failed"));
        }
        return Completable.complete();
    }

    public static StorageSource getInstance() {
        return new MockStorageSource();
    }
}
