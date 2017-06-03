package com.github.abhijitpparate.keeps.screen.login.presenter.login;

import android.support.annotation.VisibleForTesting;

import com.github.abhijitpparate.keeps.BuildConfig;
import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.auth.AuthInjector;
import com.github.abhijitpparate.keeps.data.auth.AuthSource;
import com.github.abhijitpparate.keeps.data.auth.Credentials;
import com.github.abhijitpparate.keeps.data.auth.User;
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector;
import com.github.abhijitpparate.keeps.scheduler.DevelopmentSchedulerProvider;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;

public class LoginPresenter implements LoginContract.Presenter {

    private AuthSource authSource;
    private DevelopmentSchedulerProvider schedulerProvider;

    private CompositeDisposable disposable;

    private LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.authSource = AuthInjector.getAuthSource();
        this.disposable = new CompositeDisposable();
        this.schedulerProvider = SchedulerInjector.getScheduler();

        view.setPresenter(this);
    }

    @VisibleForTesting
    LoginPresenter(LoginContract.View view, AuthSource authSource) {
        this.view = view;
        this.authSource = authSource;

        this.disposable = new CompositeDisposable();
        this.schedulerProvider = SchedulerInjector.getScheduler();

        view.setPresenter(this);
    }

    public void getUser() {
        view.showProgressIndicator(true);
        disposable.add(
                authSource
                    .getUser()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribeWith(new DisposableMaybeObserver<User>() {
                        @Override
                        public void onComplete() {
                            view.showProgressIndicator(false);
                        }

                        @Override
                        public void onSuccess(@NonNull User user) {
                            view.showProgressIndicator(false);
                            view.showHomeScreen();
                        }

                        @Override
                        public void onError(Throwable e) {
                            view.showProgressIndicator(false);
                            view.makeToast(e.getMessage());
                        }
                    })
        );
    }

    @Override
    public void attemptLogin(Credentials credentials) {
        view.showProgressIndicator(true);
        disposable.add(
                authSource
                    .attemptLogin(credentials)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            view.showProgressIndicator(false);
                            view.showHomeScreen();
                        }

                        @Override
                        public void onError(Throwable e) {
                            view.showProgressIndicator(false);
                            view.makeToast(R.string.toast_error_invalid_username_password);
                        }
                    })
        );
    }

    @Override
    public void onLoginClick() {
        String email = view.getEmail();
        String password = view.getPassword();
        if (email.isEmpty()){
            view.makeToast(R.string.toast_error_email_empty);
        } else if(password.isEmpty()){
            view.makeToast(R.string.toast_error_password_empty);
        } else if(!email.contains("@")){
            view.makeToast(R.string.toast_error_invalid_email);
        } else if(password.length() > 19){
            view.makeToast(R.string.toast_error_long_password);
        } else if(password.length() < 6){
            view.makeToast(R.string.toast_error_short_password);
        } else
            attemptLogin(new Credentials(email, email, password));
    }

    @Override
    public void onRegisterClick() {
        view.showRegistrationScreen();
    }

    @Override
    public void subscribe() {
        if ( !BuildConfig.FLAVOR.equals("mock") ) getUser();
    }

    @Override
    public void unsubscribe() {
        disposable.clear();
    }
}
