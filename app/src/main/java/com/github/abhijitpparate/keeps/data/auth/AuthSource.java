package com.github.abhijitpparate.keeps.data.auth;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface AuthSource {

    Completable createNewAccount(Credentials credentials);

    Completable attemptLogin(Credentials credentials);

    Completable deleteUser();

    Maybe<User> getUser();

    Completable logoutUser();

    Completable reAuthenticateUser(String password);

    void setReturnFail(boolean bool);
    void setAllowRegistration(boolean bool);
}
