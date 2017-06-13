package com.github.abhijitpparate.keeps.data.auth

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.twitter.sdk.android.core.TwitterSession

import org.json.JSONObject

import io.reactivex.Completable
import io.reactivex.Maybe

interface AuthSource {

    fun createNewAccount(credentials: Credentials): Completable

    fun attemptLogin(credentials: Credentials): Completable

    fun deleteUser(): Completable

    fun logoutUser(): Completable

    fun reAuthenticateUser(password: String): Completable

    fun setReturnFail(bool: Boolean)
    fun setAllowRegistration(bool: Boolean)

    val user: Maybe<User>
    fun attemptGoogleLogin(account: GoogleSignInAccount): Maybe<User>
    fun attemptFacebookLogin(token: AccessToken): Maybe<User>
    fun attemptTwitterLogin(session: TwitterSession): Maybe<User>
}
