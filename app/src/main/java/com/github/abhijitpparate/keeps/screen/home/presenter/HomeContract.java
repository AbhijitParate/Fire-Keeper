package com.github.abhijitpparate.keeps.screen.home.presenter;


import android.support.annotation.StringRes;

import com.github.abhijitpparate.keeps.data.auth.User;

public interface HomeContract {

    interface View {
        void setUserInfo(User userInfo);

        void showLoginScreen();

        void setPresenter(Presenter presenter);
        void showProgressBar(boolean bool);
        void makeToast(@StringRes int strId);
        void makeToast(String message);
    }

    interface Presenter {
        void onLogoutClick();

        void subscribe();
        void unsubscribe();
    }
}
