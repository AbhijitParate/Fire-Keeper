package com.github.abhijitpparate.keeps.data.database;


import com.github.abhijitpparate.keeps.data.Constants;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class MockDatabaseSource implements DatabaseSource {

    private boolean returnFailure = false;
    private boolean returnEmpty = false;

    @Override
    public Completable createProfile(Profile profile) {
        if (returnFailure){
            return Completable.error(new Exception("Create profile failed"));
        }
        return Completable.complete();
    }

    @Override
    public Maybe<Profile> getProfile(String uid) {
        if (returnFailure){
            return Maybe.error(new Exception("Get profile failed"));
        } else if (returnEmpty) {
            return Maybe.empty();
        }

        return Maybe.just(Constants.FAKE_PROFILE);
    }

    @Override
    public Completable deleteProfile(String uid) {
        if (returnFailure){
            return Completable.error(new Exception("Profile deletion failed"));
        }
        return Completable.complete();
    }

    @Override
    public Completable updateProfile(Profile profile) {
        if (returnFailure){
            return Completable.error(new Exception("Profile update failed"));
        }
        return Completable.complete();
    }

    @Override
    public void setReturnFail(boolean bool) {
        this.returnFailure = bool;
    }

    @Override
    public void setReturnEmpty(boolean bool) {
        this.returnEmpty = bool;
    }
}