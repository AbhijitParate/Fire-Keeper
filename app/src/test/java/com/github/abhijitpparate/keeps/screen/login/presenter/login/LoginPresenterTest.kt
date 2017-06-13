package com.github.abhijitpparate.keeps.screen.login.presenter.login

import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.data.auth.AuthInjector
import com.github.abhijitpparate.keeps.data.auth.AuthSource

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

import org.mockito.Matchers.anyInt
import org.mockito.Mockito.verify

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */

@RunWith(MockitoJUnitRunner::class)
class LoginPresenterTest {

    private val EMPTY = ""
    private val INVALID_EMAIL = "testabc.com"
    private val VALID_EMAIL = "a@b.com"
    private val VALID_PASSWORD = "123456"
    private val LONG_PASSWORD = "123456123456123456123456123456123456"
    private val SHORT_PASSWORD = "12345"

    // Presenter needs view and auth source
    // and provide a fake one as follows
    @Mock
    private val view: LoginContract.View? = null

    private val authSource = AuthInjector.authSource

    // Testing the presenter
    private var presenter: LoginPresenter? = null

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        // Instantiate the presenter using the fake auth source and view
        presenter = LoginPresenter(view, authSource)
    }

    /**
     * When user clicks register button
     */
    @Test
    fun onRegisterClick() {
        presenter!!.onRegisterClick()
        verify<View>(view).showRegistrationScreen()
    }

    /**
     * When user is already logged in
     */
    @Test
    fun onActiveUserFound() {
        authSource.setReturnFail(false)
        //        when(authSource.getUser()).thenReturn(Maybe.just(new User(VALID_EMAIL, VALID_PASSWORD)));
        presenter!!.getUser()
        verify<View>(view).showHomeScreen()
    }

    /**
     * When user is not already logged in
     */
    @Test
    fun onActiveUserNotFound() {
        authSource.setReturnFail(true)
        //        Mockito.when(authSource.getUser()).thenReturn(Maybe.<User>error(new Exception()));
        presenter!!.getUser()
        verify<View>(view).showProgressIndicator(false)
    }

    @Test
    fun whenEmailEmpty() {
        Mockito.`when`(view!!.email).thenReturn(EMPTY)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        presenter!!.onLoginClick()
        verify<View>(view).makeToast(R.string.toast_error_email_empty)
    }

    @Test
    fun whenEmailInvalid() {
        Mockito.`when`(view!!.email).thenReturn(INVALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        presenter!!.onLoginClick()
        verify<View>(view).makeToast(R.string.toast_error_invalid_email)
    }

    @Test
    fun whenPasswordEmpty() {
        Mockito.`when`(view!!.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(EMPTY)
        presenter!!.onLoginClick()
        verify<View>(view).makeToast(R.string.toast_error_password_empty)
    }

    @Test
    fun whenPasswordTooLong() {
        Mockito.`when`(view!!.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(LONG_PASSWORD)
        presenter!!.onLoginClick()
        verify<View>(view).makeToast(R.string.toast_error_long_password)
    }

    @Test
    fun whenPasswordTooShort() {
        Mockito.`when`(view!!.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(SHORT_PASSWORD)
        presenter!!.onLoginClick()
        verify<View>(view).makeToast(R.string.toast_error_short_password)
    }

    /**
     * When user login fails
     */
    @Test
    fun onLoginFailure() {
        Mockito.`when`(view!!.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)

        authSource.setReturnFail(true)

        presenter!!.onLoginClick()

        verify<View>(view).makeToast(anyInt())
    }

    /**
     * When user login fails
     */
    @Test
    fun onLoginFailureWithInvalidUsernameOrPassword() {
        Mockito.`when`(view!!.email).thenReturn(INVALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)

        authSource.setReturnFail(false)

        presenter!!.onLoginClick()

        verify<View>(view).makeToast(anyInt())
    }

    /**
     * When user login successfully
     */
    @Test
    fun onLoginSuccess() {

        Mockito.`when`(view!!.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        authSource.setReturnFail(false)

        presenter!!.onLoginClick()

        verify<View>(view).showHomeScreen()
    }

}