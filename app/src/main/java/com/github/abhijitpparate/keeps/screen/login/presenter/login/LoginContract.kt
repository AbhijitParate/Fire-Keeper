package com.github.abhijitpparate.keeps.screen.login.presenter.login

import android.support.annotation.StringRes

import com.facebook.login.LoginResult
import com.github.abhijitpparate.keeps.data.auth.Credentials
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.twitter.sdk.android.core.TwitterSession

interface LoginContract {

    interface View {

        val email: String
        val password: String

        fun showHomeScreen()
        fun showRegistrationScreen()
        fun showGoogleSignInScreen()
        fun showFacebookSignInScreen()
        fun showTwitterSignInScreen()
        fun showProgressIndicator(show: Boolean)

        fun setPresenter(presenter: LoginContract.Presenter)
        fun makeToast(@StringRes strId: Int)
        fun makeToast(message: String)
    }

    interface Presenter {
        fun attemptLogin(credentials: Credentials)

        fun onLoginClick()
        fun onGoogleLoginClick()
        fun onFacebookLoginClick()
        fun onTwitterLoginClick()

        fun onRegisterClick()

        fun subscribe()
        fun unsubscribe()

        fun registerGoogleAccount(account: GoogleSignInAccount)
        fun registerFacebookAccount(loginResult: LoginResult)
        fun registerTwitterAccount(twitterSession: TwitterSession)

    }
}
