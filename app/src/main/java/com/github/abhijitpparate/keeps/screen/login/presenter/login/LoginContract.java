package com.github.abhijitpparate.keeps.screen.login.presenter.login;

import android.support.annotation.StringRes;

import com.github.abhijitpparate.keeps.data.auth.Credentials;

public interface LoginContract {

    interface View {

        String getEmail();
        String getPassword();

        void showHomeScreen();
        void showRegistrationScreen();
        void showProgressIndicator(boolean show);

        void setPresenter(LoginContract.Presenter presenter);
        void makeToast(@StringRes int strId);
        void makeToast(String message);
    }

    interface Presenter {
        void attemptLogin(Credentials credentials);

        void onLoginClick();
        void onRegisterClick();

        void subscribe();
        void unsubscribe();
    }
}
