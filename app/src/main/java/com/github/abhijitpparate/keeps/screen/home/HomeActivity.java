package com.github.abhijitpparate.keeps.screen.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.auth.User;
import com.github.abhijitpparate.keeps.screen.home.presenter.HomeContract;
import com.github.abhijitpparate.keeps.screen.home.presenter.HomePresenter;
import com.github.abhijitpparate.keeps.screen.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    @BindView(R.id.tvUserInfo)
    TextView tvUserInfo;

    HomeContract.Presenter presenter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ButterKnife.bind(this);

        if (presenter == null){
            presenter = new HomePresenter(this);
        }

        presenter.subscribe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.actionLogout:
                presenter.onLogoutClick();
                return true;
            case R.id.actionView:
                return true;
            case R.id.actionSearch:
                return true;
        }

        return false;
    }

    @Override
    public void setUserInfo(User userInfo) {
        tvUserInfo.setText(userInfo.getEmail());
    }

    @Override
    public void showLoginScreen() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void setPresenter(HomeContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showProgressBar(boolean bool) {

    }

    @Override
    public void makeToast(@StringRes int strId) {
        Toast.makeText(this, getString(strId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
