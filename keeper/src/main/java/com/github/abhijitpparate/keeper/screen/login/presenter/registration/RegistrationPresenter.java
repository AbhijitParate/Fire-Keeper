package com.github.abhijitpparate.keeper.screen.login.presenter.registration;

import android.support.annotation.VisibleForTesting;

import com.github.abhijitpparate.keeper.data.auth.AuthSource;
import com.github.abhijitpparate.keeper.data.auth.Credentials;
import com.github.abhijitpparate.keeper.data.auth.User;
import com.github.abhijitpparate.keeper.data.database.DatabaseSource;
import com.github.abhijitpparate.keeper.data.database.Profile;
import com.github.abhijitpparate.keeper.scheduler.SchedulerProvider;
import com.github.abhijitpparate.keeper.R;
import com.github.abhijitpparate.keeper.data.auth.AuthInjector;
import com.github.abhijitpparate.keeper.data.database.DatabaseInjector;
import com.github.abhijitpparate.keeper.scheduler.SchedulerInjector;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;

public class RegistrationPresenter implements RegistrationContract.Presenter {

    private SchedulerProvider schedulerProvider;
    private AuthSource authSource;
    private DatabaseSource databaseSource;
    private CompositeDisposable disposable;

    private RegistrationContract.View view;

    public RegistrationPresenter(RegistrationContract.View view) {
        this.view = view;
        this.schedulerProvider = SchedulerInjector.getScheduler();
        this.authSource = AuthInjector.getAuthSource();
        this.databaseSource = DatabaseInjector.getDatabaseSource();
        this.disposable = new CompositeDisposable();

        view.setPresenter(this);
    }

    @VisibleForTesting
    RegistrationPresenter(RegistrationContract.View view, AuthSource authSource, DatabaseSource databaseSource) {
        this.view = view;
        this.authSource = authSource;
        this.databaseSource = databaseSource;
        this.schedulerProvider = SchedulerInjector.getScheduler();
        this.disposable = new CompositeDisposable();

        view.setPresenter(this);
    }

    @Override
    public void onLoginClick() {
        view.makeToast("Login clicked");
        view.showLoginScreen();
    }

    @Override
    public void onRegisterClick() {
        if (validateAccountCredentials()){
            attemptRegistration(new Credentials(
                    view.getName(),
                    view.getEmail(),
                    view.getPassword()
            ));
        }
    }

    private void attemptRegistration(Credentials credentials) {
        view.showProgressIndicator(true);
        disposable.add(
                authSource
                        .createNewAccount(credentials)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        getUser();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        view.showProgressIndicator(false);
                                        view.makeToast(e.toString());
                                    }
                                })
        );
    }

    private void getUser() {
        disposable.add(
                authSource
                    .getUser()
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribeWith(
                            new DisposableMaybeObserver<User>() {
                                @Override
                                public void onComplete() {
                                    view.showProgressIndicator(false);
                                }

                                @Override
                                public void onSuccess(User user) {
                                    addUserProfileToDatabase(user.getUid(), user.getEmail());
                                }

                                @Override
                                public void onError(Throwable e) {
                                    view.showProgressIndicator(false);
                                    view.makeToast(e.getMessage());
                                }
                            })
        );
    }

    private void addUserProfileToDatabase(String uid, String email){

        Profile profile = new Profile(
                uid,
                view.getName(),
                email
        );

        disposable.add(
                databaseSource
                        .createProfile(profile)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        view.showHomeScreen();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        view.makeToast(e.getMessage());
                                    }
                                }
                        )
        );
    }


    private boolean validateAccountCredentials() {
        if (view.getName().isEmpty()) {
            view.makeToast(R.string.toast_error_name_empty);
            return false;
        } else if (view.getEmail().isEmpty()) {
            view.makeToast(R.string.toast_error_email_empty);
            return false;
        } else if (!view.getEmail().contains("@")) {
            // TODO: 6/2/2017 Add better email validation
            view.makeToast(R.string.toast_error_invalid_email);
            return false;
        } else if (view.getPassword().isEmpty()) {
            view.makeToast(R.string.toast_error_password_empty);
            return false;
        } else if (view.getPasswordConfirmation().isEmpty()) {
            view.makeToast(R.string.toast_error_password_empty);
            return false;
        } else if (view.getPassword().length() < 6) {
            view.makeToast(R.string.toast_error_short_password);
            return false;
        } else if (view.getPassword().length() > 19) {
            view.makeToast(R.string.toast_error_long_password);
            return false;
        } else if (!view.getPassword().equals(view.getPasswordConfirmation())) {
            view.makeToast(R.string.toast_error_password_mismatch);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {
        disposable.clear();
    }
}
