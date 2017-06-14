package com.github.abhijitpparate.keeps.screen.login.fragments

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.screen.login.ScreenSwitcher
import com.github.abhijitpparate.keeps.screen.login.presenter.registration.RegistrationContract
import com.github.abhijitpparate.keeps.screen.login.presenter.registration.RegistrationPresenter

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class RegistrationFragment : Fragment(), RegistrationContract.View {

    @BindView(R.id.edtName)
    lateinit var edtName: EditText

    @BindView(R.id.edtEmail)
    lateinit var edtEmail: EditText

    @BindView(R.id.edtPassword)
    lateinit var edtPassword: EditText

    @BindView(R.id.edtPasswordConfirm)
    lateinit var edtPasswordConfirmation: EditText

    @BindView(R.id.btnLogin)
    lateinit var mButtonLogin: Button

    @BindView(R.id.btnRegister)
    lateinit var mButtonLRegister: Button

    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar

    lateinit var screenSwitcher: ScreenSwitcher
    lateinit var mPresenter: RegistrationContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        screenSwitcher = activity as ScreenSwitcher

        mPresenter = RegistrationPresenter(this)
        mPresenter.subscribe()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater!!.inflate(R.layout.fragment_registration, container, false)

        ButterKnife.bind(this, root)

        return root
    }

    override fun onDetach() {
        super.onDetach()
        mPresenter.unsubscribe()
    }

    @OnClick(R.id.btnLogin)
    fun onLoginClick(view: View) {
        mPresenter.onLoginClick()
    }

    @OnClick(R.id.btnRegister)
    fun onRegisterClick(view: View) {
        mPresenter.onRegisterClick()
    }

    override val name: String
        get() = edtName.text.toString()

    override val email: String
        get() = edtEmail.text.toString()

    override val password: String
        get() = edtPassword.text.toString()

    override val passwordConfirmation: String
        get() = edtPasswordConfirmation.text.toString()

    override fun showLoginScreen() {
        screenSwitcher.switchToLogin()
    }

    override fun showHomeScreen() {
        screenSwitcher.startHomeActivity()
    }

    override fun showProgressIndicator(show: Boolean) {
        if (show) {
            mProgressBar.visibility = View.VISIBLE
        } else {
            mProgressBar.visibility = View.GONE
        }
    }

    override fun setPresenter(presenter: RegistrationContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun makeToast(@StringRes strId: Int) {
        Toast.makeText(activity, getString(strId), Toast.LENGTH_SHORT).show()
    }

    override fun makeToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {

        val TAG = "RegistrationFragment"

        fun newInstance(): RegistrationFragment {

            val args = Bundle()

            val fragment = RegistrationFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
