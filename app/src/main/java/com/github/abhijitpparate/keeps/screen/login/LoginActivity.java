package com.github.abhijitpparate.keeps.screen.login;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.Variant;
import com.github.abhijitpparate.keeps.screen.home.HomeActivity;
import com.github.abhijitpparate.keeps.screen.login.fragments.LoginFragment;
import com.github.abhijitpparate.keeps.screen.login.fragments.RegistrationFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements ScreenSwitcher {

    public static final String TAG = "LoginActivity";
    public static final String LOGIN_FRAGMENT_TAG = "LOGIN_FRAGMENT";
    public static final String REGISTER_FRAGMENT_TAG = "REGISTER_FRAGMENT";

    @BindView(R.id.buildType)
    TextView tv;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        tv.setText(Variant.NAME);

        fragmentManager = getFragmentManager();
        switchToLogin();
    }

    @Override
    public void switchToLogin() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(LOGIN_FRAGMENT_TAG);

        if (loginFragment == null){
            loginFragment = LoginFragment.newInstance();
        }

        ft.setCustomAnimations(R.animator.enter_slide_from_left, R.animator.exit_slide_to_left);
        ft.replace(R.id.fragmentContainer, loginFragment, LOGIN_FRAGMENT_TAG);
        ft.commit();
    }

    @Override
    public void switchToRegistration() {
        FragmentTransaction ft = fragmentManager.beginTransaction();

        RegistrationFragment registrationFragment = (RegistrationFragment) fragmentManager.findFragmentByTag(REGISTER_FRAGMENT_TAG);

        if (registrationFragment == null){
            registrationFragment = RegistrationFragment.newInstance();
        }

        // Custom animations
        // Param 1 - Enter animation
        // Param 2 - Exit animation
        // Param 3 - Enter animation for replaced fragment (from back stack)
        // Param 1 - Exit animation for replaced fragment (from back stack)
        ft.setCustomAnimations(R.animator.enter_slide_from_left, R.animator.exit_slide_to_left, R.animator.enter_slide_from_left, R.animator.exit_slide_to_left);
        ft.replace(R.id.fragmentContainer, registrationFragment, REGISTER_FRAGMENT_TAG);
        ft.addToBackStack(REGISTER_FRAGMENT_TAG);
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        LoginFragment loginFragment = (LoginFragment) fragmentManager.findFragmentByTag(LOGIN_FRAGMENT_TAG);
        if (loginFragment != null) {
            loginFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void startHomeActivity() {
        finish();
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }
}