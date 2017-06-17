package com.github.abhijitpparate.keeper.screen.login.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.abhijitpparate.keeper.screen.login.ScreenSwitcher;
import com.github.abhijitpparate.keeper.R;
import com.github.abhijitpparate.keeper.screen.login.presenter.login.LoginContract;
import com.github.abhijitpparate.keeper.screen.login.presenter.login.LoginPresenter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements
        LoginContract.View,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "LoginFragment";

    @BindView(R.id.edtEmail)
    EditText edtEmail;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    @BindView(R.id.btnLogin)
    Button mButtonLogin;

    @BindView(R.id.btnLoginGoogle)
    Button btnLoginGoogle;

    @BindView(R.id.btnLoginFacebook)
    Button btnLoginFacebook;

    @BindView(R.id.btnLoginTwitter)
    Button btnLoginTwitter;

    @BindView(R.id.btnLoginFacebookGone)
    LoginButton btnFacebook;

    @BindView(R.id.btnLoginTwitterGone)
    TwitterLoginButton btnTwitter;

    @BindView(R.id.btnRegister)
    Button mButtonLRegister;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private ScreenSwitcher screenSwitcher;
    private LoginContract.Presenter presenter;

    CallbackManager callbackManager;

    private static final int RC_GOOGLE_AUTH = 850;

    GoogleApiClient mGoogleApiClient;

    public LoginFragment() {

    }

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        callbackManager = CallbackManager.Factory.create();

        TwitterConfig config = new TwitterConfig.Builder(getActivity())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        screenSwitcher = (ScreenSwitcher) getActivity();

        if (presenter == null) {
            presenter = new LoginPresenter(this);
        }
        mGoogleApiClient.connect();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, root);

        btnFacebook.setReadPermissions("public_profile");
        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                presenter.registerFacebookAccount(loginResult);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: ");
            }
        });

        btnTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "Twitter Auth successful: ");
                presenter.registerTwitterAccount(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "Twitter Auth failure: ");
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
        AppEventsLogger.activateApp(getActivity().getApplication());
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
        presenter.unsubscribe();
        AppEventsLogger.deactivateApp(getActivity().getApplication());
    }

    @OnClick(R.id.btnLogin)
    void onLoginClick(View view){
        presenter.onLoginClick();
    }

    @OnClick(R.id.btnLoginGoogle)
    void onGoogleLoginClick(View view){
        presenter.onGoogleLoginClick();
    }

    @OnClick(R.id.btnLoginFacebook)
    void onFacebookLoginClick(View view){
        presenter.onFacebookLoginClick();
    }

    @OnClick(R.id.btnLoginTwitter)
    void onTwitterLoginClick(View view){
        presenter.onTwitterLoginClick();
    }

    @OnClick(R.id.btnRegister)
    void onRegisterClick(View view){
        presenter.onRegisterClick();
    }

    @Override
    public String getEmail() {
        return edtEmail.getText().toString();
    }

    @Override
    public String getPassword() {
        return edtPassword.getText().toString();
    }

    @Override
    public void showHomeScreen() {
        screenSwitcher.startHomeActivity();
    }

    @Override
    public void showRegistrationScreen() {
        screenSwitcher.switchToRegistration();
    }

    @Override
    public void showGoogleSignInScreen() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_AUTH);
    }

    @Override
    public void showFacebookSignInScreen() {
        btnFacebook.performClick();
    }

    @Override
    public void showTwitterSignInScreen() {
        btnTwitter.performClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        btnTwitter.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_AUTH) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                presenter.registerGoogleAccount(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }


    @Override
    public void showProgressIndicator(boolean show) {
        if (show) mProgressBar.setVisibility(View.VISIBLE);
        else mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void makeToast(@StringRes int strId) {
        Toast.makeText(getActivity(), getString(strId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void makeToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
