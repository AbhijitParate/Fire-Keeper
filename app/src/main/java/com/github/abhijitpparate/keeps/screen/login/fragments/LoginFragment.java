package com.github.abhijitpparate.keeps.screen.login.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.screen.login.ScreenSwitcher;
import com.github.abhijitpparate.keeps.screen.login.presenter.login.LoginContract;
import com.github.abhijitpparate.keeps.screen.login.presenter.login.LoginPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements LoginContract.View {

    public static final String TAG = "LoginFragment";

    @BindView(R.id.edtEmail)
    EditText edtEmail;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    @BindView(R.id.btnLogin)
    Button mButtonLogin;

    @BindView(R.id.btnRegister)
    Button mButtonLRegister;

    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private ScreenSwitcher screenSwitcher;

    private LoginContract.Presenter presenter;

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        screenSwitcher = (ScreenSwitcher) getActivity();

        if (presenter == null) {
            presenter = new LoginPresenter(this);
        }

        presenter.subscribe();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.unsubscribe();
    }

    @OnClick(R.id.btnLogin)
    void onLoginClick(View view){
        presenter.onLoginClick();
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
}
