package com.github.abhijitpparate.keeps.screen.home.presenter


import android.util.Log
import com.facebook.AccessToken
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.github.abhijitpparate.keeps.data.auth.AuthInjector
import com.github.abhijitpparate.keeps.data.auth.AuthSource
import com.github.abhijitpparate.keeps.data.auth.User
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector
import com.github.abhijitpparate.keeps.data.database.DatabaseSource
import com.github.abhijitpparate.keeps.data.database.Note
import com.github.abhijitpparate.keeps.data.database.Profile
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector
import com.github.abhijitpparate.keeps.scheduler.SchedulerProvider
import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver

class HomePresenter(private val view: HomeContract.View) : HomeContract.Presenter {

    lateinit var currentUser: User

    internal var authSource: AuthSource = AuthInjector.authSource
    internal var databaseSource: DatabaseSource = DatabaseInjector.databaseSource
    internal var schedulerProvider: SchedulerProvider = SchedulerInjector.scheduler
    internal var disposable: CompositeDisposable = CompositeDisposable()

    private var isFirstLogin = true

    init {
        view.setPresenter(this)
    }

    override fun onRefresh() {
        getUserNotesFromDatabase()
    }

    override fun onLogoutClick() {
        if (FacebookSdk.isInitialized() && AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut()
        }
        disposable.add(
                authSource.logoutUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                object : DisposableCompletableObserver() {
                                    override fun onComplete() {
                                        view.showLoginScreen()
                                        view.makeToast("Logout")
                                    }

                                    override fun onError(@NonNull e: Throwable) {
                                        view.makeToast(e.message.toString())
                                    }
                                }
                        )
        )
    }

    override fun onNewNoteClick(noteType: HomeContract.NoteType) {
        view.showNewNoteScreen(noteType)
    }

    override fun onNoteClick(note: Note) {
        view.showNoteScreen(note)
    }

    override fun subscribe() {
        getUserData()
    }

    private fun getUserData() {
        //        Log.d(TAG, "getUserData: ");
        view.showProgressBar(true)
        disposable.add(
                authSource
                        .retrieveUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<User>() {
                            override fun onSuccess(@NonNull user: User) {
                                Log.d(TAG, "onSuccess: " + user.email)
                                view.showProgressBar(false)
                                this@HomePresenter.currentUser = user
                                getUserProfileFromDatabase()
                                getUserNotesFromDatabase()
                            }

                            override fun onError(@NonNull e: Throwable) {
                                Log.d(TAG, "onError: ")
                                view.showProgressBar(false)
                            }

                            override fun onComplete() {
                                Log.d(TAG, "onComplete: ")
                                view.showProgressBar(false)
                            }
                        })
        )
    }

    private fun getUserNotesFromDatabase() {
        Log.d(TAG, "getUserNotesFromDatabase: ")
        view.showProgressBar(true)
        disposable.add(
                databaseSource
                        .getNotesForCurrentUser(currentUser.uid!!)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<List<Note>>() {
                            override fun onSuccess(@NonNull notes: List<Note>) {
                                view.setNotes(notes)
                                view.showProgressBar(false)
                            }

                            override fun onError(@NonNull e: Throwable) {
                                view.makeToast(e.message as String)
                                view.showProgressBar(false)
                            }

                            override fun onComplete() {
                                view.showProgressBar(false)
                            }
                        })
        )
    }

    private fun getUserProfileFromDatabase() {
        Log.d(TAG, "getUserProfileFromDatabase: ")
        disposable.add(
                databaseSource
                        .getProfile(currentUser.uid!!)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<Profile>() {
                            override fun onSuccess(@NonNull profile: Profile) {
                                Log.d(TAG, "onSuccess: ")
                                if (isFirstLogin) {
                                    isFirstLogin = false
                                    view.setUserInfo(profile)
                                }
                            }

                            override fun onError(@NonNull e: Throwable) {
                                Log.d(TAG, "onError: ")
                                view.makeToast(e.message as String)
                                //                                view.showLoginScreen();
                            }

                            override fun onComplete() {
                                Log.d(TAG, "onComplete: ")
                                //                                view.showLoginScreen();
                            }
                        })
        )
    }

    override fun unsubscribe() {
        disposable.clear()
    }

    companion object {
        val TAG = "HomePresenter"
    }
}