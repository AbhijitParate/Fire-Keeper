package com.github.abhijitpparate.keeper.screen.login.presenter.login;

import com.github.abhijitpparate.keeper.R;
import com.github.abhijitpparate.keeper.data.auth.AuthInjector;
import com.github.abhijitpparate.keeper.data.auth.AuthSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@SuppressWarnings("FieldCanBeLocal")
@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTest{

    private final String EMPTY = "";
    private final String INVALID_EMAIL = "testabc.com";
    private final String VALID_EMAIL = "a@b.com";
    private final String VALID_PASSWORD = "123456";
    private final String LONG_PASSWORD = "123456123456123456123456123456123456";
    private final String SHORT_PASSWORD = "12345";

    // Presenter needs view and auth source
    // and provide a fake one as follows
    @Mock
    private LoginContract.View view;

    private AuthSource authSource = AuthInjector.getAuthSource();

    // Testing the presenter
    private LoginPresenter presenter;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        // Instantiate the presenter using the fake auth source and view
        presenter = new LoginPresenter(view, authSource);
    }

    /**
     * When user clicks register button
     */
    @Test
    public void onRegisterClick(){
        presenter.onRegisterClick();
        verify(view).showRegistrationScreen();
    }

    /**
     * When user is already logged in
     */
    @Test
    public void onActiveUserFound(){
        authSource.setReturnFail(false);
//        when(authSource.getUser()).thenReturn(Maybe.just(new User(VALID_EMAIL, VALID_PASSWORD)));
        presenter.getUser();
        verify(view).showHomeScreen();
    }

    /**
     * When user is not already logged in
     */
    @Test
    public void onActiveUserNotFound(){
        authSource.setReturnFail(true);
//        Mockito.when(authSource.getUser()).thenReturn(Maybe.<User>error(new Exception()));
        presenter.getUser();
        verify(view).showProgressIndicator(false);
    }

    @Test
    public void whenEmailEmpty() {
        Mockito.when(view.getEmail()).thenReturn(EMPTY);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        presenter.onLoginClick();
        verify(view).makeToast(R.string.toast_error_email_empty);
    }

    @Test
    public void whenEmailInvalid(){
        Mockito.when(view.getEmail()).thenReturn(INVALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        presenter.onLoginClick();
        verify(view).makeToast(R.string.toast_error_invalid_email);
    }

    @Test
    public void whenPasswordEmpty(){
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(EMPTY);
        presenter.onLoginClick();
        verify(view).makeToast(R.string.toast_error_password_empty);
    }

    @Test
    public void whenPasswordTooLong(){
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(LONG_PASSWORD);
        presenter.onLoginClick();
        verify(view).makeToast(R.string.toast_error_long_password);
    }

    @Test
    public void whenPasswordTooShort(){
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(SHORT_PASSWORD);
        presenter.onLoginClick();
        verify(view).makeToast(R.string.toast_error_short_password);
    }

    /**
     * When user login fails
     */
    @Test
    public void onLoginFailure(){
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);

        authSource.setReturnFail(true);

        presenter.onLoginClick();

        verify(view).makeToast(anyInt());
    }

    /**
     * When user login fails
     */
    @Test
    public void onLoginFailureWithInvalidUsernameOrPassword(){
        Mockito.when(view.getEmail()).thenReturn(INVALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);

        authSource.setReturnFail(false);

        presenter.onLoginClick();

        verify(view).makeToast(anyInt());
    }

    /**
     * When user login successfully
     */
    @Test
    public void onLoginSuccess(){

        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        authSource.setReturnFail(false);

        presenter.onLoginClick();

        verify(view).showHomeScreen();
    }

}