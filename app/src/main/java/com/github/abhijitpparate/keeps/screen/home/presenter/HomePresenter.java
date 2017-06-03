package com.github.abhijitpparate.keeps.screen.home.presenter;


import com.github.abhijitpparate.keeps.data.auth.AuthInjector;
import com.github.abhijitpparate.keeps.data.auth.AuthSource;
import com.github.abhijitpparate.keeps.data.auth.User;
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector;
import com.github.abhijitpparate.keeps.data.database.DatabaseSource;
import com.github.abhijitpparate.keeps.data.database.Profile;
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector;
import com.github.abhijitpparate.keeps.scheduler.DevelopmentSchedulerProvider;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;

public class HomePresenter implements HomeContract.Presenter {

    private User currentUser;

    AuthSource authSource;
    DatabaseSource databaseSource;

    DevelopmentSchedulerProvider schedulerProvider;

    CompositeDisposable disposable;

    HomeContract.View view;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
        this.authSource = AuthInjector.getAuthSource();
        this.databaseSource = DatabaseInjector.getDatabaseSource();
        this.schedulerProvider = SchedulerInjector.getScheduler();
        this.disposable = new CompositeDisposable();

        view.setPresenter(this);
    }

    @Override
    public void onLogoutClick() {
        disposable.add(
                authSource.logoutUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        view.showLoginScreen();
                                        view.makeToast("Logout");
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        view.makeToast(e.getMessage());
                                    }
                                }
                        )
        );
    }

    @Override
    public void subscribe() {
        getUserData();
    }

    private void getUserData() {
        view.showProgressBar(true);
        disposable.add(
                authSource
                        .getUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<User>() {
                            @Override
                            public void onSuccess(@NonNull User user) {
                                HomePresenter.this.currentUser = user;
                                getUserProfileFromDatabase();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    private void getUserProfileFromDatabase(){
        disposable.add(
                databaseSource
                        .getProfile(currentUser.getUid())
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<Profile>() {
                            @Override
                            public void onSuccess(@NonNull Profile profile) {
                                view.setUserInfo(currentUser);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                view.makeToast(e.getMessage());
                                view.showLoginScreen();
                            }

                            @Override
                            public void onComplete() {
                                view.showLoginScreen();
                            }
                        })
        );
    }

    @Override
    public void unsubscribe() {
        disposable.clear();
    }
}