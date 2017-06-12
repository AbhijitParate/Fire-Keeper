package com.github.abhijitpparate.keeps.data.auth;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import io.reactivex.Completable;
import io.reactivex.Maybe;

import static com.github.abhijitpparate.keeps.data.Constants.EMAIL;
import static com.github.abhijitpparate.keeps.data.Constants.PASSWORD;
import static com.github.abhijitpparate.keeps.data.Constants.USERNAME;

public class MockAuthSource implements AuthSource {

    private boolean returnFailure;
    private boolean autoLogin = false;
    private boolean allowRegistration = true;

    public final Credentials credentials = new Credentials(USERNAME, EMAIL, PASSWORD);
    public final User user = new User(USERNAME, EMAIL);

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
    public Maybe<User> attemptGoogleLogin(GoogleSignInAccount account) {
        return null;
    }
}