package com.github.abhijitpparate.keeps.screen.note.presenter;


import android.util.Log;

import com.github.abhijitpparate.keeps.R;
import com.github.abhijitpparate.keeps.data.auth.AuthInjector;
import com.github.abhijitpparate.keeps.data.auth.AuthSource;
import com.github.abhijitpparate.keeps.data.auth.User;
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector;
import com.github.abhijitpparate.keeps.data.database.DatabaseSource;
import com.github.abhijitpparate.keeps.data.database.Note;
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector;
import com.github.abhijitpparate.keeps.scheduler.SchedulerProvider;
import com.github.abhijitpparate.keeps.utils.Utils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;

import static com.github.abhijitpparate.keeps.data.database.Note.NoteColor.BLUE;
import static com.github.abhijitpparate.keeps.data.database.Note.NoteColor.DEFAULT;
import static com.github.abhijitpparate.keeps.data.database.Note.NoteColor.GREEN;
import static com.github.abhijitpparate.keeps.data.database.Note.NoteColor.ORANGE;
import static com.github.abhijitpparate.keeps.data.database.Note.NoteColor.RED;
import static com.github.abhijitpparate.keeps.data.database.Note.NoteColor.WHITE;
import static com.github.abhijitpparate.keeps.data.database.Note.NoteColor.YELLOW;
import static com.github.abhijitpparate.keeps.utils.Utils.getNoteColor;

public class NotePresenter implements NoteContract.Presenter {

    public static final String TAG = "NotePresenter";

    public static Map<String, Integer> colorMap = new HashMap<>();
    static {
        colorMap.put(DEFAULT,   R.color.colorBackground);
        colorMap.put(WHITE,     android.R.color.white);
        colorMap.put(RED,       android.R.color.holo_red_light);
        colorMap.put(GREEN,     android.R.color.holo_green_light);
        colorMap.put(YELLOW,    android.R.color.holo_orange_light);
        colorMap.put(BLUE,      android.R.color.holo_blue_bright);
        colorMap.put(ORANGE,    android.R.color.holo_orange_dark);
    }

    private SchedulerProvider schedulerProvider;
    private CompositeDisposable disposable;

    private AuthSource authSource;
    private DatabaseSource databaseSource;

    private User currentUser;
    private Note currentNote;

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
        assembleNote();
        disposable.add(
            databaseSource
                    .createOrUpdateNote(currentUser.getUid(), currentNote)
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

    private Note assembleNote() {
        if (currentNote == null) currentNote = new Note();

        if (!currentNote.getNoteId().equals(view.getNoteId())) {
            currentNote = new Note();
        }

        currentNote.setTitle(view.getNoteTitle());
        currentNote.setBody(view.getNoteBody());
        currentNote.setChecklist(view.getCheckList());
        currentNote.setColor(view.getNoteColor());
        return currentNote;
    }

    @Override
    public void loadNote(final String noteId) {
//        Log.d(TAG, "loadNote: ");
        view.makeToast("Loading note...");
        view.showProgressbar(true);
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
                                        currentNote = note;
                                        view.setNote(note);
                                        view.setNoteTitle(note.getTitle());
                                        if (note.getBody() != null && !note.getBody().isEmpty()){
                                            view.setNoteBody(note.getBody());
                                        } else if (note.getChecklist() != null && !note.getChecklist().equals("[]")){
                                            view.setNoteChecklist(Utils.parseNoteChecklist(note.getChecklist()));
                                            view.switchToChecklist();
                                        }
                                        view.setNoteColor(getNoteColor(note.getColor()), note.getColor());
                                        view.makeToast("Note retrieved successfully");
                                        view.showProgressbar(false);
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
                        .subscribeWith(new DisposableMaybeObserver<User>() {
                            @Override
                            public void onSuccess(@NonNull User user) {
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
                view.setNoteColor(android.R.color.white, WHITE);
                break;
            case RED:
                view.setNoteColor(android.R.color.holo_red_light, RED);
                break;
            case GREEN:
                view.setNoteColor(android.R.color.holo_green_light, GREEN);
                break;
            case YELLOW:
                view.setNoteColor(android.R.color.holo_orange_light, YELLOW);
                break;
            case BLUE:
                view.setNoteColor(android.R.color.holo_blue_bright, BLUE);
                break;
            case ORANGE:
                view.setNoteColor(android.R.color.holo_orange_dark, ORANGE);
                break;
            default:
                view.setNoteColor(R.color.colorBackground, DEFAULT);
                break;
        }
    }
}
