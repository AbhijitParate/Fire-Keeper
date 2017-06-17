package com.github.abhijitpparate.keeper.data.auth

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.twitter.sdk.android.core.TwitterSession

import io.reactivex.Maybe

interface AuthSource {

    fun createNewAccount(cred: com.github.abhijitpparate.keeper.data.auth.Credentials): io.reactivex.Completable

    fun attemptLogin(credentials: com.github.abhijitpparate.keeper.data.auth.Credentials): io.reactivex.Completable

    fun deleteUser(): io.reactivex.Completable

    fun logoutUser(): io.reactivex.Completable

    fun reAuthenticateUser(password: String): io.reactivex.Completable

    fun setReturnFail(bool: Boolean)
    fun setAllowRegistration(bool: Boolean)

    fun retrieveUser(): io.reactivex.Maybe<User>
    fun attemptGoogleLogin(account: GoogleSignInAccount): Maybe<User>
    fun attemptFacebookLogin(token: AccessToken): Maybe<User>
    fun attemptTwitterLogin(session: TwitterSession): Maybe<User>
}
