package com.github.abhijitpparate.keeps.screen.home.presenter;


import android.util.Log;

import com.facebook.login.LoginManager;
import com.github.abhijitpparate.keeps.data.auth.AuthInjector;
import com.github.abhijitpparate.keeps.data.auth.AuthSource;
import com.github.abhijitpparate.keeps.data.auth.User;
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector;
import com.github.abhijitpparate.keeps.data.database.DatabaseSource;
import com.github.abhijitpparate.keeps.data.database.Note;
import com.github.abhijitpparate.keeps.data.database.Profile;
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector;
import com.github.abhijitpparate.keeps.scheduler.SchedulerProvider;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;

public class HomePresenter implements HomeContract.Presenter {

    public static final String TAG = "HomePresenter";

    private FirebaseUser currentUser;

    private AuthSource authSource;
    private DatabaseSource databaseSource;
    private SchedulerProvider schedulerProvider;
    private CompositeDisposable disposable;

    private HomeContract.View view;

    boolean isFirstLogin = true;

    public HomePresenter(HomeContract.View view) {
        this.view = view;
        this.authSource = AuthInjector.getAuthSource();
        this.databaseSource = DatabaseInjector.getDatabaseSource();
        this.schedulerProvider = SchedulerInjector.getScheduler();
        this.disposable = new CompositeDisposable();

        view.setPresenter(this);
    }

    @Override
    public void onRefresh() {
        getUserNotesFromDatabase();
    }

    @Override
    public void onLogoutClick() {
        LoginManager.getInstance().logOut();
        disposable.add(
                authSource.logoutUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                new DisposableCompletableObserver() {
                                    @Override
                                    public void onComplete() {
                                        view.showLoginScreen();
                                        view.makeToast("Logout");
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        view.makeToast(e.getMessage());
                                    }
                                }
                        )
        );
    }

    @Override
    public void onNewNoteClick(HomeContract.NoteType noteType) {
        view.showNewNoteScreen(noteType);
    }

    @Override
    public void onNoteClick(Note note) {
        view.showNoteScreen(note);
    }

    @Override
    public void subscribe() {
        getUserData();
    }

    private void getUserData() {
//        Log.d(TAG, "getUserData: ");
        view.showProgressBar(true);
        disposable.add(
                authSource
                        .getUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<FirebaseUser>() {
                            @Override
                            public void onSuccess(@NonNull FirebaseUser user) {
                                Log.d(TAG, "onSuccess: " + user.getEmail());
                                view.showProgressBar(false);
                                HomePresenter.this.currentUser = user;
                                getUserProfileFromDatabase();
                                getUserNotesFromDatabase();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d(TAG, "onError: ");
                                view.showProgressBar(false);
                                view.makeToast(e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: ");
                                view.showProgressBar(false);
                            }
                        })
        );
    }

    private void getUserNotesFromDatabase() {
        Log.d(TAG, "getUserNotesFromDatabase: ");
        view.showProgressBar(true);
        disposable.add(
                databaseSource
                        .getNotesForCurrentUser(currentUser.getUid())
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<List<Note>>() {
                            @Override
                            public void onSuccess(@NonNull List<Note> notes) {
                                view.setNotes(notes);
                                view.showProgressBar(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                view.makeToast(e.getMessage());
                                view.showProgressBar(false);
                            }

                            @Override
                            public void onComplete() {
                                view.showProgressBar(false);
                            }
                        })
        );
    }

    private void getUserProfileFromDatabase(){
        Log.d(TAG, "getUserProfileFromDatabase: ");
        disposable.add(
                databaseSource
                        .getProfile(currentUser.getUid())
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<Profile>() {
                            @Override
                            public void onSuccess(@NonNull Profile profile) {
                                Log.d(TAG, "onSuccess: ");
                                if (isFirstLogin){
                                    isFirstLogin = false;
                                    view.setUserInfo(profile);
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.d(TAG, "onError: ");
                                view.makeToast(e.getMessage());
//                                view.showLoginScreen();
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: ");
//                                view.showLoginScreen();
                            }
                        })
        );
    }

    @Override
    public void unsubscribe() {
        disposable.clear();
    }
}