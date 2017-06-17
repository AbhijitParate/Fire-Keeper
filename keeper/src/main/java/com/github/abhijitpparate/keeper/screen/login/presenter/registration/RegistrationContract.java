package com.github.abhijitpparate.keeper.screen.login.presenter.registration;

import android.support.annotation.StringRes;

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
