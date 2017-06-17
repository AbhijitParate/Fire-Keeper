package com.github.abhijitpparate.keeper.screen.login.presenter.registration

import android.support.annotation.StringRes

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
