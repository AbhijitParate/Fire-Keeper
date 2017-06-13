package com.github.abhijitpparate.keeps.screen.note.presenter;


import android.util.Log;

import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.auth.AuthInjector;
import com.github.abhijitpparate.keeps.data.auth.AuthSource;
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector;
import com.github.abhijitpparate.keeps.data.database.DatabaseSource;
import com.github.abhijitpparate.keeps.data.database.Note;
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector;
import com.github.abhijitpparate.keeps.scheduler.SchedulerProvider;
import com.github.abhijitpparate.keeps.utils.Utils;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;

public class NotePresenter implements NoteContract.Presenter {

    public static final String TAG = "NotePresenter";

    private SchedulerProvider schedulerProvider;
    private CompositeDisposable disposable;

    private AuthSource authSource;
    private DatabaseSource databaseSource;

    private FirebaseUser currentUser;

    private NoteContract.View view;

    private boolean isOptionsOpen = false;

    public NotePresenter(NoteContract.View view) {
        this.view = view;

        this.authSource = AuthInjector.getAuthSource();
        this.databaseSource = DatabaseInjector.getDatabaseSource();
        this.schedulerProvider = SchedulerInjector.getScheduler();
        this.disposable = new CompositeDisposable();

        view.setPresenter(this);
    }

    @Override
    public void onSaveClick() {
//        Log.d(TAG, "onSaveClick: " + currentUser.getEmail());
        Note note = generateNote();

        disposable.add(
            databaseSource
                    .createNewNote(currentUser.getUid(), note)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribeWith(
                            new DisposableCompletableObserver() {
                                @Override
                                public void onComplete() {

                                    view.showHomeScreen();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    view.makeToast(e.getMessage());
                                }
                            }
                    )
        );
    }

    private Note generateNote() {
        Note note = new Note();
        note.setTitle(view.getNoteTitle());
        note.setBody(view.getNoteBody());
        note.setChecklist(view.getCheckList());
        return note;
    }

    @Override
    public void loadNote(final String noteId) {
//        Log.d(TAG, "loadNote: ");
        view.makeToast("Loading note...");
        disposable.add(
                databaseSource
                        .getNoteFromId(currentUser.getUid(), noteId)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                new DisposableMaybeObserver<Note>() {

                                    @Override
                                    public void onComplete() {

                                    }

                                    @Override
                                    public void onSuccess(@NonNull Note note) {
                                        view.setNote(note);
                                        view.setNoteTitle(note.getTitle());
                                        if (note.getBody() != null && !note.getBody().isEmpty()){
                                            view.setNoteBody(note.getBody());
                                        } else if (note.getChecklist() != null && !note.getChecklist().isEmpty()){
                                            view.setNoteChecklist(Utils.parseNoteChecklist(note.getChecklist()));
                                            view.switchToChecklist();
                                        }
                                        view.makeToast("Note retrieved successfully");
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        view.makeToast(e.getMessage());
                                    }
                                }
                        )
        );
    }

    @Override
    public void subscribe() {
        getUserData();
    }

    private void getUserData() {
//        Log.d(TAG, "getUserData: ");
        disposable.add(
                authSource
                        .getUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<FirebaseUser>() {
                            @Override
                            public void onSuccess(@NonNull FirebaseUser user) {
                                currentUser = user;
//                                Log.d(TAG, "onSuccess: ");
                                view.loadNoteIfAvailable();
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
//                                Log.d(TAG, "onError: ");
                            }

                            @Override
                            public void onComplete() {
//                                Log.d(TAG, "onComplete: ");
                            }
                        })
        );
    }

    @Override
    public void unsubscribe() {
        disposable.clear();
    }

    @Override
    public void onChecklistClick(boolean isChecked) {
        if (isChecked) view.switchToChecklist();
        else view.switchToText();
    }

    @Override
    public void onDrawingClick() {

    }

    @Override
    public void onAudioClick() {

    }

    @Override
    public void onVideoClick() {

    }

    @Override
    public void onImageClick() {

    }

    @Override
    public void onOptionsClick() {
        toggleOptionsPanel();
    }

    private void toggleOptionsPanel(){
        if (isOptionsOpen){
            view.hideOptionsPanel();
        } else {
            view.showOptionsPanel();
        }
        isOptionsOpen = !isOptionsOpen;
    }

    @Override
    public void onDeleteClick() {
        Log.d(TAG, "onDeleteClick: ");
        toggleOptionsPanel();
    }

    @Override
    public void onSendClick() {
        Log.d(TAG, "onSendClick: ");
        toggleOptionsPanel();
    }

    @Override
    public void onDuplicateClick() {
        Log.d(TAG, "onDuplicateClick: ");
        toggleOptionsPanel();
    }

    @Override
    public void onLabelClick() {
        Log.d(TAG, "onLabelClick: ");
        toggleOptionsPanel();
    }

    @Override
    public void onColorSelected(NoteContract.NoteColor color) {
        Log.d(TAG, "onColorSelected: " + color);
        switch (color){
            case WHITE:
                view.setNoteColor(android.R.color.white);
                break;
            case RED:
                view.setNoteColor(android.R.color.holo_red_light);
                break;
            case GREEN:
                view.setNoteColor(android.R.color.holo_green_light);
                break;
            case YELLOW:
                view.setNoteColor(android.R.color.holo_orange_light);
                break;
            case BLUE:
                view.setNoteColor(android.R.color.holo_blue_bright);
                break;
            case ORANGE:
                view.setNoteColor(android.R.color.holo_orange_dark);
                break;
            default:
                view.setNoteColor(R.color.colorBackground);
                break;
        }
    }
}
