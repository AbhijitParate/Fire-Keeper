package com.github.abhijitpparate.keeps.screen.login.presenter.registration;

import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.auth.AuthInjector;
import com.github.abhijitpparate.keeps.data.auth.AuthSource;
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector;
import com.github.abhijitpparate.keeps.data.database.DatabaseSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("FieldCanBeLocal")
@RunWith(MockitoJUnitRunner.class)
public class RegistrationPresenterTest {

    private static final String EMPTY = "";

    private static final String VALID_PASSWORD = "123456";

    private static final String INVALID_PASSWORD = "12345";

    private static final String LONG_PASSWORD = "12345678912345678912";

    private static final String VALID_USERNAME = "Username";

    private static final String INVALID_EMAIL = "userexample.com";

    private static final String VALID_EMAIL = "a@b.com";

    // Mock user input and actions
    @Mock
    private RegistrationContract.View view;

    // Fake authentication and data source
    private AuthSource authSource;
    private DatabaseSource databaseSource;

    private RegistrationPresenter presenter;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
        authSource = AuthInjector.getAuthSource();
        databaseSource = DatabaseInjector.getDatabaseSource();
        presenter = new RegistrationPresenter(view, authSource, databaseSource);
    }

    @Test
    public void whenRegistrationFailed(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(VALID_PASSWORD);

        authSource.setReturnFail(true);
        authSource.setAllowRegistration(false);

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(Mockito.anyString());
    }

    @Test
    public void whenNameIsEmpty(){
        Mockito.when(view.getName()).thenReturn(EMPTY);
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(VALID_PASSWORD);

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(R.string.toast_error_name_empty);
    }

    @Test
    public void whenEmailIsEmpty(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(EMPTY);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(VALID_PASSWORD);

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(R.string.toast_error_email_empty);
    }

    @Test
    public void whenPasswordIsEmpty(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(EMPTY);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(VALID_PASSWORD);

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(R.string.toast_error_password_empty);
    }

    @Test
    public void whenConfirmPasswordIsEmpty(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(EMPTY);

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(R.string.toast_error_password_empty);
    }

    @Test
    public void whenEmailInvalid(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(INVALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(VALID_PASSWORD);

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(R.string.toast_error_invalid_email);
    }

    @Test
    public void whenPasswordTooShort(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(INVALID_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(INVALID_PASSWORD);

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(R.string.toast_error_short_password);
    }

    @Test
    public void whenConfirmPasswordTooLong(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(LONG_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(LONG_PASSWORD);

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(R.string.toast_error_long_password);
    }

    @Test
    public void whenPasswordMismatch(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(VALID_PASSWORD + "1");

        presenter.onRegisterClick();

        Mockito.verify(view).makeToast(R.string.toast_error_password_mismatch);
    }

    @Test
    public void whenRegistrationSuccess(){
        Mockito.when(view.getName()).thenReturn(VALID_USERNAME);
        Mockito.when(view.getEmail()).thenReturn(VALID_EMAIL);
        Mockito.when(view.getPassword()).thenReturn(VALID_PASSWORD);
        Mockito.when(view.getPasswordConfirmation()).thenReturn(VALID_PASSWORD);

        authSource.setReturnFail(false);
        authSource.setAllowRegistration(true);

        presenter.onRegisterClick();

        Mockito.verify(view).showHomeScreen();
    }
}
