package com.github.abhijitpparate.keeps.screen.login.presenter.registration;

import android.support.annotation.StringRes;

import com.github.abhijitpparate.keeps.data.auth.Credentials;
import com.github.abhijitpparate.keeps.screen.login.presenter.login.LoginContract;

public interface RegistrationContract {

    interface View {
        String getName();
        String getEmail();
        String getPassword();
        String getPasswordConfirmation();

        void showLoginScreen();
        void showHomeScreen();
        void showProgressIndicator(boolean show);

        void setPresenter(RegistrationContract.Presenter presenter);
        void makeToast(@StringRes int strId);
        void makeToast(String message);
    }

    interface Presenter {

        void onLoginClick();
        void onRegisterClick();

        void subscribe();
        void unsubscribe();
    }

}
