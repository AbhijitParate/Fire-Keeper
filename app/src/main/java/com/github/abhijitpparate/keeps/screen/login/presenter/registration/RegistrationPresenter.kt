package com.github.abhijitpparate.keeps.screen.login.presenter.registration

import android.support.annotation.VisibleForTesting

import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.data.auth.AuthInjector
import com.github.abhijitpparate.keeps.data.auth.AuthSource
import com.github.abhijitpparate.keeps.data.auth.Credentials
import com.github.abhijitpparate.keeps.data.auth.User
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector
import com.github.abhijitpparate.keeps.data.database.DatabaseSource
import com.github.abhijitpparate.keeps.data.database.Profile
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector
import com.github.abhijitpparate.keeps.scheduler.SchedulerProvider
import com.google.firebase.auth.FirebaseUser

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver

class RegistrationPresenter : RegistrationContract.Presenter {

    private var schedulerProvider: SchedulerProvider? = null
    private var authSource: AuthSource? = null
    private var databaseSource: DatabaseSource? = null
    private var disposable: CompositeDisposable? = null

    private var view: RegistrationContract.View? = null

    constructor(view: RegistrationContract.View) {
        this.view = view
        this.schedulerProvider = SchedulerInjector.scheduler
        this.authSource = AuthInjector.authSource
        this.databaseSource = DatabaseInjector.databaseSource
        this.disposable = CompositeDisposable()

        view.setPresenter(this)
    }

    @VisibleForTesting
    internal constructor(view: RegistrationContract.View, authSource: AuthSource, databaseSource: DatabaseSource) {
        this.view = view
        this.authSource = authSource
        this.databaseSource = databaseSource
        this.schedulerProvider = SchedulerInjector.scheduler
        this.disposable = CompositeDisposable()

        view.setPresenter(this)
    }

    override fun onLoginClick() {
        view!!.makeToast("Login clicked")
        view!!.showLoginScreen()
    }

    override fun onRegisterClick() {
        if (validateAccountCredentials()) {
            attemptRegistration(Credentials(
                    view!!.name,
                    view!!.email,
                    view!!.password
            ))
        }
    }

    private fun attemptRegistration(credentials: Credentials) {
        view!!.showProgressIndicator(true)
        disposable!!.add(
                authSource!!
                        .createNewAccount(credentials)
                        .subscribeOn(schedulerProvider!!.io())
                        .observeOn(schedulerProvider!!.ui())
                        .subscribeWith(
                                object : DisposableCompletableObserver() {
                                    override fun onComplete() {
                                        getUser()
                                    }

                                    override fun onError(e: Throwable) {
                                        view!!.showProgressIndicator(false)
                                        view!!.makeToast(e.toString())
                                    }
                                })
        )
    }

    private fun getUser() {
        disposable!!.add(
                authSource!!
                        .retrieveUser()
                        .subscribeOn(schedulerProvider!!.io())
                        .observeOn(schedulerProvider!!.ui())
                        .subscribeWith(
                                object : DisposableMaybeObserver<User>() {
                                    override fun onComplete() {
                                        view!!.showProgressIndicator(false)
                                    }

                                    override fun onSuccess(user: User) {
                                        addUserProfileToDatabase(user.uid.toString(), user.email.toString())
                                    }

                                    override fun onError(e: Throwable) {
                                        view!!.showProgressIndicator(false)
                                        view!!.makeToast(e.message.toString())
                                    }
                                })
        )
    }

    private fun addUserProfileToDatabase(uid: String, email: String) {

        val profile = Profile(
                uid,
                view!!.name,
                email
        )

        disposable!!.add(
                databaseSource!!
                        .createProfile(profile)
                        .subscribeOn(schedulerProvider!!.io())
                        .observeOn(schedulerProvider!!.ui())
                        .subscribeWith(
                                object : DisposableCompletableObserver() {
                                    override fun onComplete() {
                                        view!!.showHomeScreen()
                                    }

                                    override fun onError(e: Throwable) {
                                        view!!.makeToast(e.message.toString())
                                    }
                                }
                        )
        )
    }


    private fun validateAccountCredentials(): Boolean {
        if (view!!.name.isEmpty()) {
            view!!.makeToast(R.string.toast_error_name_empty)
            return false
        } else if (view!!.email.isEmpty()) {
            view!!.makeToast(R.string.toast_error_email_empty)
            return false
        } else if (!view!!.email.contains("@")) {
            // TODO: 6/2/2017 Add better email validation
            view!!.makeToast(R.string.toast_error_invalid_email)
            return false
        } else if (view!!.password.isEmpty()) {
            view!!.makeToast(R.string.toast_error_password_empty)
            return false
        } else if (view!!.passwordConfirmation.isEmpty()) {
            view!!.makeToast(R.string.toast_error_password_empty)
            return false
        } else if (view!!.password.length < 6) {
            view!!.makeToast(R.string.toast_error_short_password)
            return false
        } else if (view!!.password.length > 19) {
            view!!.makeToast(R.string.toast_error_long_password)
            return false
        } else if (view!!.password != view!!.passwordConfirmation) {
            view!!.makeToast(R.string.toast_error_password_mismatch)
            return false
        } else {
            return true
        }
    }

    override fun subscribe() {

    }

    override fun unsubscribe() {
        disposable!!.clear()
    }
}
