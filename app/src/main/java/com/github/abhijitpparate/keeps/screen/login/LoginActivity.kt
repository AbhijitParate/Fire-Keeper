package com.github.abhijitpparate.keeps.screen.login

import android.app.Fragment
import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.Variant
import com.github.abhijitpparate.keeps.screen.home.HomeActivity
import com.github.abhijitpparate.keeps.screen.login.fragments.LoginFragment
import com.github.abhijitpparate.keeps.screen.login.fragments.RegistrationFragment

class LoginActivity : AppCompatActivity(), ScreenSwitcher {

    @BindView(R.id.buildType)
    lateinit var tv : TextView

    lateinit var fm: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ButterKnife.bind(this)

        tv.text = Variant.NAME

        fm = this.fragmentManager
        switchToLogin()
    }

    override fun switchToLogin() {
        val ft = fm.beginTransaction()
        var loginFragment: Fragment? = fm.findFragmentByTag(LOGIN_FRAGMENT_TAG)

        if (loginFragment == null) {
            loginFragment = LoginFragment.newInstance()
        }

        ft.setCustomAnimations(R.animator.enter_slide_from_left, R.animator.exit_slide_to_left)
        ft.replace(R.id.fragmentContainer, loginFragment, LOGIN_FRAGMENT_TAG)
        ft.commit()
    }

    override fun switchToRegistration() {
        val ft = fm.beginTransaction()

        var registrationFragment: Fragment? = fm.findFragmentByTag(REGISTER_FRAGMENT_TAG)

        if (registrationFragment == null) {
            registrationFragment = RegistrationFragment.newInstance()
        }

        ft.setCustomAnimations(R.animator.enter_slide_from_left, R.animator.exit_slide_to_left, R.animator.enter_slide_from_left, R.animator.exit_slide_to_left)
        ft.replace(R.id.fragmentContainer, registrationFragment, REGISTER_FRAGMENT_TAG)
        ft.addToBackStack(REGISTER_FRAGMENT_TAG)
        ft.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: ")
        val loginFragment = fm.findFragmentByTag(LOGIN_FRAGMENT_TAG) as LoginFragment
        loginFragment.onActivityResult(requestCode, resultCode, data)
    }

    override fun startHomeActivity() {
        finish()
        val homeIntent = Intent(this, HomeActivity::class.java)
        startActivity(homeIntent)
    }

    companion object {
        val TAG = "LoginActivity"
        val LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT"
        val REGISTER_FRAGMENT_TAG = "REGISTER_FRAGMENT"
    }
}