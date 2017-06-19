package com.github.abhijitpparate.keeper.screen.landing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.abhijitpparate.keeper.R;
import com.github.abhijitpparate.keeper.screen.home.HomeActivity;
import com.github.abhijitpparate.keeper.screen.landing.presenter.LandingContract;
import com.github.abhijitpparate.keeper.screen.landing.presenter.LandingPresenter;
import com.github.abhijitpparate.keeper.screen.login.LoginActivity;

public class LandingActivity extends AppCompatActivity implements LandingContract.View {

    LandingContract.Presenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        if (presenter == null) {
            presenter = new LandingPresenter(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.subscribe();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unsubscribe();
    }

    @Override
    public void showHomeScreen() {
        finish();
        Intent intent = HomeActivity.getLauncherIntent(this);
        startActivity(intent);
    }

    @Override
    public void showLoginScreen() {
        finish();
        Intent intent = LoginActivity.getLauncherIntent(this);
        startActivity(intent);
    }

    @Override
    public void setPresenter(LandingContract.Presenter landingPresenter) {
        this.presenter = landingPresenter;
    }
}
