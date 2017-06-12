package com.github.abhijitpparate.keeps.screen.login.presenter.login;

import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.github.abhijitpparate.keeps.BuildConfig;
import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.auth.AuthInjector;
import com.github.abhijitpparate.keeps.data.auth.AuthSource;
import com.github.abhijitpparate.keeps.data.auth.Credentials;
import com.github.abhijitpparate.keeps.data.auth.User;
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector;
import com.github.abhijitpparate.keeps.data.database.DatabaseSource;
import com.github.abhijitpparate.keeps.data.database.Profile;
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector;
import com.github.abhijitpparate.keeps.scheduler.SchedulerProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Maybe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;

public class LoginPresenter implements LoginContract.Presenter {

    public static final String TAG = "LoginPresenter";

    private AuthSource authSource;
    private DatabaseSource databaseSource;
    private SchedulerProvider schedulerProvider;

    private CompositeDisposable disposable;

    private LoginContract.View view;

    public LoginPresenter(LoginContract.View view) {
        this.view = view;
        this.authSource = AuthInjector.getAuthSource();
        this.disposable = new CompositeDisposable();
        this.schedulerProvider = SchedulerInjector.getScheduler();
        this.databaseSource = DatabaseInjector.getDatabaseSource();

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
                    .subscribeWith(new DisposableMaybeObserver<FirebaseUser>() {
                        @Override
                        public void onComplete() {
                            view.showProgressIndicator(false);
                        }

                        @Override
                        public void onSuccess(@NonNull FirebaseUser user) {
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
    public void onGoogleLoginClick() {
        view.showProgressIndicator(true);
        view.showGoogleSignInScreen();
    }

    @Override
    public void onFacebookLoginClick() {
        view.showProgressIndicator(true);
        view.showFacebookSignInScreen();
    }

    @Override
    public void onTwitterLoginClick() {
        view.showProgressIndicator(true);
        view.showTwitterSignInScreen();
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

    @Override
    public void registerGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "registerGoogleAccount: ");
        view.showProgressIndicator(true);
        disposable.add(
                authSource
                        .attemptGoogleLogin(account)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<FirebaseUser>() {
                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: ");
                                view.showProgressIndicator(false);
                                view.makeToast("Google Login complete");

                            }

                            @Override
                            public void onSuccess(@NonNull FirebaseUser user) {
                                Log.d(TAG, "onSuccess: ");
                                view.makeToast(user.getEmail());
                                view.showProgressIndicator(false);
                                view.makeToast("Google Login complete");
                                registerUser(user);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: ");
                                view.showProgressIndicator(false);
                                view.makeToast(e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void registerFacebookAccount(LoginResult loginResult) {
        Log.d(TAG, "registerFacebookAccount: ");
        view.showProgressIndicator(true);
        disposable.add(
                authSource
                        .attemptFacebookLogin(loginResult.getAccessToken())
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<FirebaseUser>() {
                            @Override
                            public void onSuccess(@NonNull FirebaseUser firebaseUser) {
                                registerUser(firebaseUser);
                                view.showProgressIndicator(false);
                                Log.d(TAG, "onSuccess: Facebook login");
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                view.showProgressIndicator(false);
                                Log.d(TAG, "onError: Facebook Login failed");
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    @Override
    public void registerTwitterAccount(TwitterSession twitterSession) {
        disposable.add(
                authSource
                        .attemptTwitterLogin(twitterSession)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<FirebaseUser>() {
                            @Override
                            public void onSuccess(@NonNull FirebaseUser firebaseUser) {
                                registerUser(firebaseUser);
                                Log.d(TAG, "onSuccess: Twitter login");
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                view.showProgressIndicator(false);
                                Log.d(TAG, "onError: Twitter Login failed");
                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );
    }

    private void registerUser(FirebaseUser user){
        Profile profile = new Profile(user.getUid(), user.getDisplayName(), user.getEmail());
        disposable.add(
                databaseSource
                        .createProfile(profile)
                        .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: Login successful");
                        view.showProgressIndicator(false);
                        view.showHomeScreen();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.makeToast("Failed to create profile");
                    }
                })
        );
    }

}
