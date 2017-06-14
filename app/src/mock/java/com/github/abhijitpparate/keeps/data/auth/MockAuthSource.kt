package com.github.abhijitpparate.keeps.data.auth

import com.facebook.AccessToken
import com.github.abhijitpparate.keeps.Constants
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.twitter.sdk.android.core.TwitterSession

import io.reactivex.Completable
import io.reactivex.Maybe

class MockAuthSource : AuthSource {

    private var returnFailure: Boolean = false
    private val autoLogin = false
    private var allowRegistration = true

    val credentials = Constants.CREDENTIALS
    val user = Constants.USER

    override fun createNewAccount(credentials: Credentials): Completable {
        if (returnFailure) {
            return Completable.error(Exception("Registration failed"))
        } else {
            return Completable.complete()
        }
    }

    override fun attemptLogin(credentials: Credentials): Completable {
        if (returnFailure) {
            return Completable.error(Exception("Login failed"))
        } else if (this.credentials.email == credentials.email && this.credentials.password == credentials.password) {
            return Completable.complete()
        } else {
            return Completable.error(Exception("Invalid username or password"))
        }
    }

    override fun deleteUser(): Completable {
        if (returnFailure) {
            return Completable.error(Exception("Delete user failed"))
        } else {
            return Completable.complete()
        }
    }

    override fun logoutUser(): Completable {
        if (returnFailure) {
            return Completable.error(Exception("Logout failed"))
        } else {
            return Completable.complete()
        }
    }

    override fun reAuthenticateUser(password: String): Completable {
        if (returnFailure) {
            return Completable.error(Exception("Re-authentication failed"))
        } else if (this.credentials.password == password) {
            return Completable.complete()
        } else {
            return Completable.error(Exception("Re-authentication failed"))
        }
    }

    override fun setReturnFail(bool: Boolean) {
        this.returnFailure = bool
    }

    override fun setAllowRegistration(bool: Boolean) {
        this.allowRegistration = bool
    }

    override fun retrieveUser(): Maybe<User> {
        if (returnFailure) {
            return Maybe.error<User>(Exception("Get user failed"))
        } else {
            if (autoLogin)
                return Maybe.just(user)
            else if (allowRegistration)
                return Maybe.just(user)
            else
                return Maybe.error<User>(Exception("Auto login failed"))
        }
    }

    override fun attemptGoogleLogin(account: GoogleSignInAccount): Maybe<User> {
        if (returnFailure) {
            return Maybe.error<User>(Exception("Get user failed"))
        } else {
            if (autoLogin)
                return Maybe.just(user)
            else if (allowRegistration)
                return Maybe.just(user)
            else
                return Maybe.error<User>(Exception("Auto login failed"))
        }
    }

    override fun attemptFacebookLogin(token: AccessToken): Maybe<User> {
        if (returnFailure) {
            return Maybe.error<User>(Exception("Get user failed"))
        } else {
            if (autoLogin)
                return Maybe.just(user)
            else if (allowRegistration)
                return Maybe.just(user)
            else
                return Maybe.error<User>(Exception("Auto login failed"))
        }
    }

    override fun attemptTwitterLogin(session: TwitterSession): Maybe<User> {
        if (returnFailure) {
            return Maybe.error<User>(Exception("Get user failed"))
        } else {
            if (autoLogin)
                return Maybe.just(user)
            else if (allowRegistration)
                return Maybe.just(user)
            else
                return Maybe.error<User>(Exception("Auto login failed"))
        }
    }
}