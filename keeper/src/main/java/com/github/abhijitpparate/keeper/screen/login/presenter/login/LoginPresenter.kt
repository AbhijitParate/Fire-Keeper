package com.github.abhijitpparate.keeper.screen.login.presenter.login

import android.support.annotation.VisibleForTesting
import android.util.Log
import com.facebook.login.LoginResult
import com.github.abhijitpparate.keeper.BuildConfig
import com.github.abhijitpparate.keeper.R
import com.github.abhijitpparate.keeper.data.auth.AuthInjector
import com.github.abhijitpparate.keeper.data.auth.AuthSource
import com.github.abhijitpparate.keeper.data.auth.Credentials
import com.github.abhijitpparate.keeper.data.auth.User
import com.github.abhijitpparate.keeper.data.database.DatabaseInjector
import com.github.abhijitpparate.keeper.data.database.DatabaseSource
import com.github.abhijitpparate.keeper.data.database.Profile
import com.github.abhijitpparate.keeper.scheduler.SchedulerInjector
import com.github.abhijitpparate.keeper.scheduler.SchedulerProvider
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.twitter.sdk.android.core.TwitterSession
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver

class LoginPresenter : LoginContract.Presenter {

    var authSource: AuthSource
    var databaseSource: DatabaseSource
    var schedulerProvider: SchedulerProvider
    var disposable: CompositeDisposable

    private var view: LoginContract.View? = null

    constructor(view: LoginContract.View) {
        this.view = view
        this.authSource = AuthInjector.authSource
        this.schedulerProvider = SchedulerInjector.scheduler
        this.databaseSource = DatabaseInjector.databaseSource
        this.disposable = CompositeDisposable()

        view.setPresenter(this)
    }

    @VisibleForTesting
    constructor(view: LoginContract.View, authSource: AuthSource) {
        this.view = view
        this.authSource = authSource
        this.databaseSource = DatabaseInjector.databaseSource
        this.disposable = CompositeDisposable()
        this.schedulerProvider = SchedulerInjector.scheduler

        view.setPresenter(this)
    }

    fun getUser() {
        Log.d(TAG, "getUser")
        view?.showProgressIndicator(true)
        disposable.add(
                authSource
                        .retrieveUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<User>() {
                            override fun onComplete() {
                                view?.showProgressIndicator(false)
                            }

                            override fun onSuccess(@NonNull user: User) {
                                view?.showProgressIndicator(false)
                                view?.showHomeScreen()
                            }

                            override fun onError(e: Throwable) {
                                view?.showProgressIndicator(false)
                            }
                        })
        )
    }

    override fun attemptLogin(credentials: Credentials) {
        view?.showProgressIndicator(true)
        disposable.add(
                authSource
                        .attemptLogin(credentials)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                view?.showProgressIndicator(false)
                                view?.showHomeScreen()
                            }

                            override fun onError(e: Throwable) {
                                view?.showProgressIndicator(false)
                                view?.makeToast(R.string.toast_error_invalid_username_password)
                            }
                        })
        )
    }

    override fun onLoginClick() {
        val email = view?.email
        val password = view?.password
        if (email!!.isEmpty()) {
            view?.makeToast(R.string.toast_error_email_empty)
        } else if (password!!.isEmpty()) {
            view?.makeToast(R.string.toast_error_password_empty)
        } else if (!email.contains("@")) {
            view?.makeToast(R.string.toast_error_invalid_email)
        } else if (password.length > 19) {
            view?.makeToast(R.string.toast_error_long_password)
        } else if (password.length < 6) {
            view?.makeToast(R.string.toast_error_short_password)
        } else
            attemptLogin(Credentials(email, email, password))
    }

    override fun onGoogleLoginClick() {
        view?.showProgressIndicator(true)
        view?.showGoogleSignInScreen()
    }

    override fun onFacebookLoginClick() {
        view?.showProgressIndicator(true)
        view?.showFacebookSignInScreen()
    }

    override fun onTwitterLoginClick() {
        view?.showProgressIndicator(true)
        view?.showTwitterSignInScreen()
    }

    override fun onRegisterClick() {
        view?.showRegistrationScreen()
    }

    override fun subscribe() {
        if (BuildConfig.FLAVOR != "mock") getUser()
    }

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun registerGoogleAccount(account: GoogleSignInAccount) {
        Log.d(TAG, "registerGoogleAccount: ")
        view?.showProgressIndicator(true)
        disposable.add(
                authSource
                        .attemptGoogleLogin(account)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<User>() {
                            override fun onComplete() {
                                Log.d(TAG, "onComplete: ")
                                view?.showProgressIndicator(false)
                                view?.makeToast("Google Login complete")

                            }

                            override fun onSuccess(@NonNull user: User) {
                                Log.d(TAG, "onSuccess: ")
                                view?.makeToast(user.email.toString())
                                view?.showProgressIndicator(false)
                                view?.makeToast("Google Login complete")
                                registerUser(user)
                            }

                            override fun onError(e: Throwable) {
                                Log.d(TAG, "onError: ")
                                view?.showProgressIndicator(false)
                                view?.makeToast(e.message.toString())
                            }
                        })
        )
    }

    override fun registerFacebookAccount(loginResult: LoginResult) {
        Log.d(TAG, "registerFacebookAccount: ")
        view?.showProgressIndicator(true)
        disposable.add(
                authSource
                        .attemptFacebookLogin(loginResult.accessToken)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<User>() {
                            override fun onSuccess(@NonNull firebaseUser: User) {
                                registerUser(firebaseUser)
                                view?.showProgressIndicator(false)
                                Log.d(TAG, "onSuccess: Facebook login")
                            }

                            override fun onError(@NonNull e: Throwable) {
                                view?.showProgressIndicator(false)
                                Log.d(TAG, "onError: Facebook Login failed")
                            }

                            override fun onComplete() {

                            }
                        })
        )
    }

    override fun registerTwitterAccount(twitterSession: TwitterSession) {
        disposable.add(
                authSource
                        .attemptTwitterLogin(twitterSession)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<User>() {
                            override fun onSuccess(@NonNull firebaseUser: User) {
                                registerUser(firebaseUser)
                                Log.d(TAG, "onSuccess: Twitter login")
                            }

                            override fun onError(@NonNull e: Throwable) {
                                view?.showProgressIndicator(false)
                                Log.d(TAG, "onError: Twitter Login failed")
                            }

                            override fun onComplete() {

                            }
                        })
        )
    }

    private fun registerUser(user: User) {

        val profile = Profile()
        profile.uid = user.uid
        profile.name = user.name
        profile.email = user.email

        disposable.add(
                databaseSource
                        .createProfile(profile)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                Log.d(TAG, "onComplete: Login successful")
                                view?.showProgressIndicator(false)
                                view?.showHomeScreen()
                            }

                            override fun onError(@NonNull e: Throwable) {
                                Log.d(TAG, e.message)
                                view?.makeToast("Failed to create profile")
                            }
                        })
        )
    }

    companion object {

        val TAG = "LoginPresenter"
    }

}
