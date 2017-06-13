package com.github.abhijitpparate.keeps.screen.login.presenter.registration

import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.data.auth.AuthInjector
import com.github.abhijitpparate.keeps.data.auth.AuthSource
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector
import com.github.abhijitpparate.keeps.data.database.DatabaseSource

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegistrationPresenterTest {

    // Mock user input and actions
    @Mock
    private val view: RegistrationContract.View? = null

    // Fake authentication and data source
    private var authSource: AuthSource? = null
    private var databaseSource: DatabaseSource? = null

    private var presenter: RegistrationPresenter? = null

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        authSource = AuthInjector.authSource
        databaseSource = DatabaseInjector.databaseSource
        presenter = RegistrationPresenter(view, authSource, databaseSource)
    }

    @Test
    fun whenRegistrationFailed() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(VALID_PASSWORD)

        authSource!!.setReturnFail(true)
        authSource!!.setAllowRegistration(false)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(Mockito.anyString())
    }

    @Test
    fun whenNameIsEmpty() {
        Mockito.`when`(view!!.name).thenReturn(EMPTY)
        Mockito.`when`(view.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(VALID_PASSWORD)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(R.string.toast_error_name_empty)
    }

    @Test
    fun whenEmailIsEmpty() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(EMPTY)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(VALID_PASSWORD)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(R.string.toast_error_email_empty)
    }

    @Test
    fun whenPasswordIsEmpty() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(EMPTY)
        Mockito.`when`(view.passwordConfirmation).thenReturn(VALID_PASSWORD)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(R.string.toast_error_password_empty)
    }

    @Test
    fun whenConfirmPasswordIsEmpty() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(EMPTY)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(R.string.toast_error_password_empty)
    }

    @Test
    fun whenEmailInvalid() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(INVALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(VALID_PASSWORD)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(R.string.toast_error_invalid_email)
    }

    @Test
    fun whenPasswordTooShort() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(INVALID_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(INVALID_PASSWORD)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(R.string.toast_error_short_password)
    }

    @Test
    fun whenConfirmPasswordTooLong() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(LONG_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(LONG_PASSWORD)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(R.string.toast_error_long_password)
    }

    @Test
    fun whenPasswordMismatch() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(VALID_PASSWORD + "1")

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).makeToast(R.string.toast_error_password_mismatch)
    }

    @Test
    fun whenRegistrationSuccess() {
        Mockito.`when`(view!!.name).thenReturn(VALID_USERNAME)
        Mockito.`when`(view.email).thenReturn(VALID_EMAIL)
        Mockito.`when`(view.password).thenReturn(VALID_PASSWORD)
        Mockito.`when`(view.passwordConfirmation).thenReturn(VALID_PASSWORD)

        authSource!!.setReturnFail(false)
        authSource!!.setAllowRegistration(true)

        presenter!!.onRegisterClick()

        Mockito.verify<View>(view).showHomeScreen()
    }

    companion object {

        private val EMPTY = ""

        private val VALID_PASSWORD = "123456"

        private val INVALID_PASSWORD = "12345"

        private val LONG_PASSWORD = "12345678912345678912"

        private val VALID_USERNAME = "Username"

        private val INVALID_EMAIL = "userexample.com"

        private val VALID_EMAIL = "a@b.com"
    }
}
