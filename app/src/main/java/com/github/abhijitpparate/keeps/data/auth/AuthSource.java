package com.github.abhijitpparate.keeps.data.auth;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONObject;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface AuthSource {

    Completable createNewAccount(Credentials credentials);

    Completable attemptLogin(Credentials credentials);

    Completable deleteUser();

    Maybe<FirebaseUser> getUser();

    Completable logoutUser();

    Completable reAuthenticateUser(String password);

    void setReturnFail(boolean bool);
    void setAllowRegistration(boolean bool);

    Maybe<FirebaseUser> attemptGoogleLogin(GoogleSignInAccount account);
    Maybe<FirebaseUser> attemptFacebookLogin(AccessToken token);
    Maybe<FirebaseUser> attemptTwitterLogin(TwitterSession session);
}
