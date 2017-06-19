package com.github.abhijitpparate.keeper.screen.landing.presenter;


import android.util.Log;

import com.github.abhijitpparate.keeper.data.auth.AuthInjector;
import com.github.abhijitpparate.keeper.data.auth.AuthSource;
import com.github.abhijitpparate.keeper.data.auth.User;
import com.github.abhijitpparate.keeper.scheduler.SchedulerInjector;
import com.github.abhijitpparate.keeper.scheduler.SchedulerProvider;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableMaybeObserver;

public class LandingPresenter implements LandingContract.Presenter {

    public static final String TAG = LandingPresenter.class.getSimpleName();

    private AuthSource authSource;

    private SchedulerProvider schedulerProvider;
    private CompositeDisposable disposable;

    private LandingContract.View view;

    public LandingPresenter(LandingContract.View view) {
        this.authSource = AuthInjector.getAuthSource();

        this.disposable = new CompositeDisposable();
        this.schedulerProvider = SchedulerInjector.getScheduler();

        this.view = view;

        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        Log.d(TAG, "subscribe: ");
        disposable.add(
                authSource.getUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<User>() {
                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: ");
                            }

                            @Override
                            public void onSuccess(@NonNull User user) {
                                Log.d(TAG, "onSuccess: ");
                                view.showHomeScreen();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: ");
                                view.showLoginScreen();
                            }
                        })
        );
    }

    @Override
    public void unsubscribe() {
        Log.d(TAG, "unsubscribe: ");
        disposable.clear();
    }
}
