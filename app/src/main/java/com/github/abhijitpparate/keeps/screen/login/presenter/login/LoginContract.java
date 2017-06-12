package com.github.abhijitpparate.keeps.screen.login.presenter.login;

import android.support.annotation.StringRes;

import com.facebook.login.LoginResult;
import com.github.abhijitpparate.keeps.data.auth.Credentials;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.twitter.sdk.android.core.TwitterSession;

public interface LoginContract {

    interface View {

        String getEmail();
        String getPassword();

        void showHomeScreen();
        void showRegistrationScreen();
        void showGoogleSignInScreen();
        void showFacebookSignInScreen();
        void showTwitterSignInScreen();
        void showProgressIndicator(boolean show);

        void setPresenter(LoginContract.Presenter presenter);
        void makeToast(@StringRes int strId);
        void makeToast(String message);
    }

    interface Presenter {
        void attemptLogin(Credentials credentials);

        void onLoginClick();
        void onGoogleLoginClick();
        void onFacebookLoginClick();
        void onTwitterLoginClick();

        void onRegisterClick();

        void subscribe();
        void unsubscribe();

        void registerGoogleAccount(GoogleSignInAccount account);
        void registerFacebookAccount(LoginResult loginResult);
        void registerTwitterAccount(TwitterSession twitterSession);

    }
}
