package com.github.abhijitpparate.keeper.data.auth;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.twitter.sdk.android.core.TwitterSession;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface AuthSource {

    Completable createNewAccount(Credentials credentials);

    Completable attemptLogin(Credentials credentials);

    Completable deleteUser();

    Completable logoutUser();

    Completable reAuthenticateUser(String password);

    void setReturnFail(boolean bool);
    void setAllowRegistration(boolean bool);

    Maybe<User> getUser();
    Maybe<User> attemptGoogleLogin(GoogleSignInAccount account);
    Maybe<User> attemptFacebookLogin(AccessToken token);
    Maybe<User> attemptTwitterLogin(TwitterSession session);
}
