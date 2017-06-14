package com.github.abhijitpparate.keeps.data.auth


import android.util.Log
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import com.twitter.sdk.android.core.TwitterSession
import io.reactivex.Completable
import io.reactivex.Maybe

class FirebaseAuthService : AuthSource {

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var listener: FirebaseAuth.AuthStateListener? = null

    fun getAuth(): FirebaseAuth {
        return auth
    }

    override fun createNewAccount(cred: Credentials): Completable {
        return Completable.create { e ->
            getAuth().createUserWithEmailAndPassword(
                    cred.email!!,
                    cred.password!!
                    ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    e.onComplete()
                } else {
                    e.onError(task.exception)
                }
            }
        }
    }

    override fun attemptLogin(credentials: Credentials): Completable {
        return Completable.create { e ->
            getAuth().signInWithEmailAndPassword(
                    credentials.email!!,
                    credentials.password!!
                    ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    e.onComplete()
                } else {
                    e.onError(task.exception)
                }
            }
        }
    }

    override fun deleteUser(): Completable {
        return Completable.create { e ->
            val user = getAuth().currentUser
            user?.delete()
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            e.onComplete()
                        } else {
                            e.onError(task.exception)
                        }
                    } ?: e.onError(Exception("User is null"))
        }
    }

    override fun retrieveUser(): Maybe<User> {
            Log.d(TAG, "getUser: ")
            return Maybe.create { e ->
                Log.d(TAG, "subscribe: ")
                getAuth().removeAuthStateListener(listener!!)
                listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    Log.d(TAG, "onAuthStateChanged: ")
                    val firebaseUser = firebaseAuth.currentUser
                    auth.removeAuthStateListener(listener!!)

                    firebaseUser.let {
                        val user = User(
                                firebaseUser?.uid,
                                firebaseUser?.displayName,
                                firebaseUser?.email
                        )
                        e.onSuccess(user)
                    }
                }

                getAuth().addAuthStateListener(listener!!)
            }
        }

    override fun logoutUser(): Completable {
        return Completable.create { e ->
            listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                auth.removeAuthStateListener(listener!!)
                if (firebaseAuth.currentUser == null) {
                    e.onComplete()
                } else {
                    e.onError(Exception())
                }
            }

            getAuth().addAuthStateListener(listener!!)
            auth.signOut()
        }
    }

    override fun reAuthenticateUser(password: String): Completable {
        return Completable.create { e ->
            val user = getAuth().currentUser
            if (user != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                e.onComplete()
                            } else {
                                e.onError(task.exception)
                            }
                        }
            } else {
                e.onError(Exception())
            }
        }
    }

    override fun setReturnFail(bool: Boolean) {

    }

    override fun setAllowRegistration(bool: Boolean) {

    }

    override fun attemptGoogleLogin(account: GoogleSignInAccount): Maybe<User> {
        Log.d(TAG, "attemptGoogleLogin: ")
        return Maybe.create { e ->
            Log.d(TAG, "subscribe: ")
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            getAuth().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        Log.d(TAG, "onComplete: ")
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val firebaseUser = getAuth().currentUser
                            if (firebaseUser != null) {
                                val user = User(
                                        firebaseUser.uid,
                                        firebaseUser.displayName,
                                        firebaseUser.email
                                )
                                e.onSuccess(user)
                                e.onComplete()
                                Log.d(TAG, "signInWithCredential:success")
                            } else {
                                e.onError(Exception("signInWithCredential : failed"))
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential : failure", task.exception)
                            e.onError(Exception("Authentication failed"))
                        }
                    }
        }
    }

    override fun attemptFacebookLogin(token: AccessToken): Maybe<User> {
        Log.d(TAG, "attemptGoogleLogin: ")
        return Maybe.create { e ->
            Log.d(TAG, "subscribe: ")
            val credential = FacebookAuthProvider.getCredential(token.token)
            getAuth().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        Log.d(TAG, "onComplete: ")
                        if (task.isSuccessful) {
                            val firebaseUser = getAuth().currentUser
                            if (firebaseUser != null) {
                                val user = User(
                                        firebaseUser.uid,
                                        firebaseUser.displayName,
                                        firebaseUser.email
                                )
                                e.onSuccess(user)
                                e.onComplete()
                                Log.d(TAG, "signInWithCredential : success")
                            } else {
                                e.onError(Exception("signInWithCredential : failed"))
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential : failure", task.exception)
                            e.onError(Exception("Authentication failed"))
                        }
                    }
        }
    }

    override fun attemptTwitterLogin(session: TwitterSession): Maybe<User> {
        Log.d(TAG, "attemptTwitterLogin: ")
        return Maybe.create { e ->
            Log.d(TAG, "subscribe: ")
            val credential = TwitterAuthProvider.getCredential(
                    session.authToken.token,
                    session.authToken.secret)

            getAuth().signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        Log.d(TAG, "onComplete: ")
                        if (task.isSuccessful) {
                            val firebaseUser = getAuth().currentUser
                            if (firebaseUser != null) {
                                val user = User(
                                        firebaseUser.uid,
                                        firebaseUser.displayName,
                                        firebaseUser.email
                                )
                                e.onSuccess(user)
                                e.onComplete()
                                Log.d(TAG, "signInWithCredential : success")
                            } else {
                                e.onError(Exception("signInWithCredential : failed"))
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential : failure", task.exception)
                            e.onError(Exception("Authentication failed"))
                        }
                    }
        }
    }

    companion object {

        val TAG = "FirebaseAuthService"

        val authSource: AuthSource
            get() = FirebaseAuthService()
    }
}