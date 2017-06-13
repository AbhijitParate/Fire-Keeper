package com.github.abhijitpparate.keeps.screen.login.presenter.registration

import android.support.annotation.StringRes

import com.github.abhijitpparate.keeps.data.auth.Credentials
import com.github.abhijitpparate.keeps.screen.login.presenter.login.LoginContract

interface RegistrationContract {

    interface View {
        val name: String
        val email: String
        val password: String
        val passwordConfirmation: String

        fun showLoginScreen()
        fun showHomeScreen()
        fun showProgressIndicator(show: Boolean)

        fun setPresenter(presenter: RegistrationContract.Presenter)
        fun makeToast(@StringRes strId: Int)
        fun makeToast(message: String)
    }

    interface Presenter {

        fun onLoginClick()
        fun onRegisterClick()

        fun subscribe()
        fun unsubscribe()
    }

}
