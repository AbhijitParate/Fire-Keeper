package com.github.abhijitpparate.keeps.data.auth;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;

public class FirebaseAuthService implements AuthSource {

    public static final String TAG = "FirebaseAuthService";

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;

    public FirebaseAuthService() {
        this.auth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth(){
        if (auth ==  null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    @Override
    public Completable createNewAccount(final Credentials credentials) {
        return Completable.create(
                new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                        getAuth()
                                .createUserWithEmailAndPassword(
                                        credentials.getEmail(),
                                        credentials.getPassword()
                                ).addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()){
                                            e.onComplete();
                                        } else {
                                            e.onError(task.getException());
                                        }
                                    }
                                });
                    }
                }
        );
    }

    @Override
    public Completable attemptLogin(final Credentials credentials) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                getAuth()
                    .signInWithEmailAndPassword(
                            credentials.getEmail(),
                            credentials.getPassword()
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@android.support.annotation.NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        e.onComplete();
                    } else {
                        e.onError(task.getException());
                    }
                    }
                });
            }
        });
    }

    @Override
    public Completable deleteUser() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                FirebaseUser user = getAuth().getCurrentUser();
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        e.onComplete();
                                    } else {
                                        e.onError(task.getException());
                                    }
                                }
                            });
                } else {
                    e.onError(new Exception("User is null"));
                }
            }
        });
    }

    @Override
    public Maybe<User> getUser() {
        Log.d(TAG, "getUser: ");
        return Maybe.create(new MaybeOnSubscribe<User>() {
            @Override
            public void subscribe(@NonNull final MaybeEmitter<User> e) throws Exception {
                Log.d(TAG, "subscribe: ");
                getAuth().removeAuthStateListener(listener);
                listener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@android.support.annotation.NonNull FirebaseAuth firebaseAuth) {
                        Log.d(TAG, "onAuthStateChanged: ");
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        auth.removeAuthStateListener(listener);
                        if (firebaseUser != null){
                            User user = new User(
                                    firebaseUser.getUid(),
                                    firebaseUser.getEmail()
                            );
                            Maybe.just(user);
                            e.onSuccess(user);
                        } else {
                            e.onComplete();
                        }
                    }
                };

                getAuth().addAuthStateListener(listener);
            }
        });
    }

    @Override
    public Completable logoutUser() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                listener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@android.support.annotation.NonNull FirebaseAuth firebaseAuth) {
                        auth.removeAuthStateListener(listener);
                        if (firebaseAuth.getCurrentUser() == null){
                            e.onComplete();
                        } else {
                            e.onError(new Exception());
                        }
                    }
                };

                getAuth().addAuthStateListener(listener);
                auth.signOut();
            }
        });
    }

    @Override
    public Completable reAuthenticateUser(final String password) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull final CompletableEmitter e) throws Exception {
                FirebaseUser user = getAuth().getCurrentUser();
                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@android.support.annotation.NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        e.onComplete();
                                    } else {
                                        e.onError(task.getException());
                                    }
                                }
                            });
                } else {
                    e.onError(new Exception());
                }
            }
        });
    }

    @Override
    public void setReturnFail(boolean bool) {

    }

    @Override
    public void setAllowRegistration(boolean bool) {

    }

    public static AuthSource getAuthSource() {
        return new FirebaseAuthService();
    }
}