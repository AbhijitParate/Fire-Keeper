package com.github.abhijitpparate.keeper.screen.landing.presenter;


public interface LandingContract {
    interface View{

        void showHomeScreen();
        void showLoginScreen();

        void setPresenter(Presenter landingPresenter);
    }

    interface Presenter {
        void subscribe();
        void unsubscribe();
    }
}
