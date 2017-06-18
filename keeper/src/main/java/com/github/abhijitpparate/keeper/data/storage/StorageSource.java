package com.github.abhijitpparate.keeper.data.storage;


import android.net.Uri;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface StorageSource {

    void setReturnFail(boolean bool);

    Maybe<File> getFile(String uid, File file);
    Maybe<File> createFile(String uid, String name, String type, Uri uri);
    Completable deleteFile(String uid, File file);
}
