package com.github.abhijitpparate.keeps.data.auth;

import com.facebook.AccessToken;
import com.github.abhijitpparate.keeps.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.twitter.sdk.android.core.TwitterSession;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class MockAuthSource implements AuthSource {

    private boolean returnFailure;
    private boolean autoLogin = false;
    private boolean allowRegistration = true;

    public final Credentials credentials = Constants.CREDENTIALS;
    public final User user = Constants.USER;

    @Override
    public Completable createNewAccount(Credentials credentials) {
        if (returnFailure){
            return Completable.error(new Exception("Registration failed"));
        } else {
            return Completable.complete();
        }
    }

    @Override
    public Completable attemptLogin(Credentials credentials) {
        if (returnFailure){
            return Completable.error(new Exception("Login failed"));
        } else if(this.credentials.getEmail().equals(credentials.getEmail()) &&
                    this.credentials.getPassword().equals(credentials.getPassword())){
            return Completable.complete();
        } else {
            return Completable.error(new Exception("Invalid username or password"));
        }
    }

    @Override
    public Completable deleteUser() {
        if (returnFailure){
            return Completable.error(new Exception("Delete user failed"));
        } else {
            return Completable.complete();
        }
    }

    @Override
    public Completable logoutUser() {
        if (returnFailure) {
            return Completable.error(new Exception("Logout failed"));
        } else {
            return Completable.complete();
        }
    }

    @Override
    public Completable reAuthenticateUser(String password) {
        if (returnFailure){
            return Completable.error(new Exception("Re-authentication failed"));
        } else if(this.credentials.getPassword().equals(password)) {
            return Completable.complete();
        } else {
            return Completable.error(new Exception("Re-authentication failed"));
        }
    }

    @Override
    public void setReturnFail(boolean bool) {
        this.returnFailure = bool;
    }

    @Override
    public void setAllowRegistration(boolean bool) {
        this.allowRegistration = bool;
    }

    @Override
    public Maybe<User> getUser() {
        if (returnFailure){
            return Maybe.error(new Exception("Get user failed"));
        } else {
            if (autoLogin) return Maybe.just(user);
            else if (allowRegistration) return Maybe.just(user);
            else return Maybe.error(new Exception("Auto login failed"));
        }
    }

    @Override
    public Maybe<User> attemptGoogleLogin(GoogleSignInAccount account) {
        if (returnFailure){
            return Maybe.error(new Exception("Get user failed"));
        } else {
            if (autoLogin) return Maybe.just(user);
            else if (allowRegistration) return Maybe.just(user);
            else return Maybe.error(new Exception("Auto login failed"));
        }
    }

    @Override
    public Maybe<User> attemptFacebookLogin(AccessToken token) {
        if (returnFailure){
            return Maybe.error(new Exception("Get user failed"));
        } else {
            if (autoLogin) return Maybe.just(user);
            else if (allowRegistration) return Maybe.just(user);
            else return Maybe.error(new Exception("Auto login failed"));
        }
    }

    @Override
    public Maybe<User> attemptTwitterLogin(TwitterSession session) {
        if (returnFailure){
            return Maybe.error(new Exception("Get user failed"));
        } else {
            if (autoLogin) return Maybe.just(user);
            else if (allowRegistration) return Maybe.just(user);
            else return Maybe.error(new Exception("Auto login failed"));
        }
    }
}