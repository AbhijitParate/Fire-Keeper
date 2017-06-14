package com.github.abhijitpparate.keeps.screen.login.fragments

import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.screen.login.ScreenSwitcher
import com.github.abhijitpparate.keeps.screen.login.presenter.login.LoginContract
import com.github.abhijitpparate.keeps.screen.login.presenter.login.LoginPresenter
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class LoginFragment : Fragment(), LoginContract.View, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.edtEmail)
    lateinit var edtEmail: EditText

    @BindView(R.id.edtPassword)
    lateinit var edtPassword: EditText

    @BindView(R.id.btnLogin)
    lateinit var mButtonLogin: Button

    @BindView(R.id.btnLoginGoogle)
    lateinit var btnLoginGoogle: Button

    @BindView(R.id.btnLoginFacebook)
    lateinit var btnLoginFacebook: Button

    @BindView(R.id.btnLoginTwitter)
    lateinit var btnLoginTwitter: Button

    @BindView(R.id.btnLoginFacebookGone)
    lateinit var btnFacebook: LoginButton

    @BindView(R.id.btnLoginTwitterGone)
    lateinit var btnTwitter: TwitterLoginButton

    @BindView(R.id.btnRegister)
    lateinit var mButtonLRegister: Button

    @BindView(R.id.progressBar)
    lateinit var mProgressBar: ProgressBar

    lateinit var mScreenSwitcher: ScreenSwitcher
    lateinit var mPresenter: LoginContract.Presenter

    lateinit var callbackManager: CallbackManager

    lateinit var mGoogleApiClient: GoogleApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleApiClient = GoogleApiClient.Builder(activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()

        callbackManager = CallbackManager.Factory.create()

        val config = TwitterConfig.Builder(activity)
                .logger(DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret)))
                .debug(true)
                .build()
        Twitter.initialize(config)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mScreenSwitcher = activity as ScreenSwitcher
        mPresenter = LoginPresenter(this)
        mGoogleApiClient.connect()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater!!.inflate(R.layout.fragment_login, container, false)

        ButterKnife.bind(this, root)

        btnFacebook.setReadPermissions("public_profile")
        btnFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                mPresenter.registerFacebookAccount(loginResult)
            }

            override fun onCancel() {
                Log.d(TAG, "onCancel: ")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "onError: ")
            }
        })

        btnTwitter.callback = object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                Log.d(TAG, "Twitter Auth successful: ")
                mPresenter.registerTwitterAccount(result.data)
            }

            override fun failure(exception: TwitterException) {
                Log.d(TAG, "Twitter Auth failure: ")
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        mPresenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        mGoogleApiClient.disconnect()
        mPresenter.unsubscribe()
    }

    @OnClick(R.id.btnLogin)
    internal fun onLoginClick(view: View) {
        mPresenter.onLoginClick()
    }

    @OnClick(R.id.btnLoginGoogle)
    internal fun onGoogleLoginClick(view: View) {
        mPresenter.onGoogleLoginClick()
    }

    @OnClick(R.id.btnLoginFacebook)
    internal fun onFacebookLoginClick(view: View) {
        mPresenter.onFacebookLoginClick()
    }

    @OnClick(R.id.btnLoginTwitter)
    internal fun onTwitterLoginClick(view: View) {
        mPresenter.onTwitterLoginClick()
    }

    @OnClick(R.id.btnRegister)
    internal fun onRegisterClick(view: View) {
        mPresenter.onRegisterClick()
    }

    override val email: String
        get() = edtEmail.text.toString()

    override val password: String
        get() = edtPassword.text.toString()

    override fun showHomeScreen() {
        mScreenSwitcher.startHomeActivity()
    }

    override fun showRegistrationScreen() {
        mScreenSwitcher.switchToRegistration()
    }

    override fun showGoogleSignInScreen() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_GOOGLE_AUTH)
    }

    override fun showFacebookSignInScreen() {
        btnFacebook.performClick()
    }

    override fun showTwitterSignInScreen() {
        btnTwitter.performClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: ")
        callbackManager.onActivityResult(requestCode, resultCode, data)
        btnTwitter.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_AUTH) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                mPresenter.registerGoogleAccount(account!!)
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


    override fun showProgressIndicator(show: Boolean) {
        if (show)
            mProgressBar.visibility = View.VISIBLE
        else
            mProgressBar.visibility = View.GONE
    }

    override fun setPresenter(presenter: LoginContract.Presenter) {
        this.mPresenter = presenter
    }

    override fun makeToast(@StringRes strId: Int) {
        Toast.makeText(activity, getString(strId), Toast.LENGTH_SHORT).show()
    }

    override fun makeToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onConnected(bundle: Bundle?) {

    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    companion object {

        val TAG = "LoginFragment"

        private val RC_GOOGLE_AUTH = 850

        fun newInstance(): LoginFragment {

            val args = Bundle()

            val fragment = LoginFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
