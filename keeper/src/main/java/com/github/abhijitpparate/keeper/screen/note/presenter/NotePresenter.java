package com.github.abhijitpparate.keeper.screen.note.presenter;


import android.net.Uri;
import android.util.Log;

import com.github.abhijitpparate.keeper.data.auth.AuthSource;
import com.github.abhijitpparate.keeper.data.auth.User;
import com.github.abhijitpparate.keeper.data.database.DatabaseSource;
import com.github.abhijitpparate.keeper.data.database.Note;
import com.github.abhijitpparate.keeper.data.storage.File;
import com.github.abhijitpparate.keeper.data.storage.StorageInjector;
import com.github.abhijitpparate.keeper.data.storage.StorageSource;
import com.github.abhijitpparate.keeper.scheduler.SchedulerProvider;
import com.github.abhijitpparate.keeper.utils.Utils;
import com.github.abhijitpparate.keeper.R;
import com.github.abhijitpparate.keeper.data.auth.AuthInjector;
import com.github.abhijitpparate.keeper.data.database.DatabaseInjector;
import com.github.abhijitpparate.keeper.scheduler.SchedulerInjector;
import com.google.android.gms.location.places.Place;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;

public class NotePresenter implements NoteContract.Presenter {

    public static final String TAG = "NotePresenter";

    public static Map<String, Integer> colorMap = new HashMap<>();
    static {
        colorMap.put(Note.NoteColor.DEFAULT,   R.color.colorBackground);
        colorMap.put(Note.NoteColor.WHITE,     android.R.color.white);
        colorMap.put(Note.NoteColor.RED,       android.R.color.holo_red_light);
        colorMap.put(Note.NoteColor.GREEN,     android.R.color.holo_green_light);
        colorMap.put(Note.NoteColor.YELLOW,    android.R.color.holo_orange_light);
        colorMap.put(Note.NoteColor.BLUE,      android.R.color.holo_blue_bright);
        colorMap.put(Note.NoteColor.ORANGE,    android.R.color.holo_orange_dark);
    }

    private SchedulerProvider schedulerProvider;
    private CompositeDisposable disposable;

    private AuthSource authSource;
    private DatabaseSource databaseSource;
    private StorageSource storageSource;

    private User currentUser;
    private Note currentNote;
    private Uri tempFileUri;

    private NoteContract.View view;

    private boolean isOptionsOpen = false;

    public NotePresenter(NoteContract.View view) {
        this.view = view;

        this.authSource = AuthInjector.getAuthSource();
        this.databaseSource = DatabaseInjector.getDatabaseSource();
        this.storageSource = StorageInjector.getStorageSource();

        this.schedulerProvider = SchedulerInjector.getScheduler();
        this.disposable = new CompositeDisposable();
        this.currentNote = Note.newNote();

        view.setPresenter(this);
    }

    private Note assembleNote() {
//        if (currentNote == null) currentNote = Note.newNote();

//        if (!currentNote.getNoteId().equals(view.getNoteId())) {
//            currentNote = Note.newNote();
//        }

        currentNote.setTitle(view.getNoteTitle());
        currentNote.setBody(view.getNoteBody());
        currentNote.setChecklist(view.getCheckList());
//        currentNote.setColor(view.getNoteColor());
//        currentNote.setPlace(view.getNotePlace());
//        currentNote.setFile(view.getFile());
        return currentNote;
    }

    @Override
    public void onSaveClick() {
        Note note = assembleNote();
        if (tempFileUri != null){
            saveFile(note);
        } else
            saveNote(note);
    }

    public void saveFile(final Note note){
        disposable.add(
                storageSource
                        .createFile(currentUser.getUid(), note.getFile().getName(), note.getFile().getType(), tempFileUri)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(new DisposableMaybeObserver<File>(){

                            @Override
                            public void onSuccess(File file) {
                                Log.d(TAG, "onSuccess: ");
                                note.setFile(file);
                                saveNote(note);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: ");
                                view.makeToast(e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG, "onComplete: ");
                                tempFileUri = null;
                                view.makeToast("onComplete");
                            }
                        })
        );
    }

    public void saveNote(Note note){
        Log.d(TAG, "saveNote: ");
        disposable.add(
                databaseSource
                        .createOrUpdateNote(currentUser.getUid(), note)
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

    @Override
    public void loadNote(final String noteId) {
//        Log.d(TAG, "loadNote: ");
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
                                        NotePresenter.this.currentNote = note;
//                                        view.setNote(note);

                                        view.setNoteTitle(note.getTitle());

                                        if (note.getBody() != null && !note.getBody().isEmpty()){
                                            view.setNoteBody(note.getBody());
                                        } else if (note.getChecklist() != null && !note.getChecklist().equals("[]")){
                                            view.setNoteChecklist(Utils.parseNoteChecklist(note.getChecklist()));
                                            view.switchToChecklist();
                                        }

                                        view.setNoteColor(Utils.getNoteColor(note.getColor()), note.getColor() != null ? note.getColor() : Note.NoteColor.DEFAULT);

                                        if (note.getPlace() != null && note.getPlace().getLocation() != null) {
                                            view.setNotePlace(note.getPlace());
                                        }

                                        if (note.getFile() != null) {
                                            view.setNoteFile(note.getFile());
                                        }

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
    public void onLocationClick() {
        view.showPlacePicker();
    }

    @Override
    public void onAudioClick() {

    }

    @Override
    public void onVideoClick() {

    }

    @Override
    public void onImageClick() {
        view.showFilePicker();
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
                view.setNoteColor(android.R.color.white, Note.NoteColor.WHITE);
                break;
            case RED:
                view.setNoteColor(android.R.color.holo_red_light, Note.NoteColor.RED);
                break;
            case GREEN:
                view.setNoteColor(android.R.color.holo_green_light, Note.NoteColor.GREEN);
                break;
            case YELLOW:
                view.setNoteColor(android.R.color.holo_orange_light, Note.NoteColor.YELLOW);
                break;
            case BLUE:
                view.setNoteColor(android.R.color.holo_blue_bright, Note.NoteColor.BLUE);
                break;
            case ORANGE:
                view.setNoteColor(android.R.color.holo_orange_dark, Note.NoteColor.ORANGE);
                break;
            default:
                view.setNoteColor(R.color.colorBackground, Note.NoteColor.DEFAULT);
                break;
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        Note.Place p = new Note.Place();
        p.setName(place.getName().toString());
        p.setLocation(new Note.Place.LatLng(place.getLatLng().latitude, place.getLatLng().longitude));
        view.setNotePlace(p);
    }

    @Override
    public void onFileSelected(File file, Uri uri) {
        this.tempFileUri = uri;
        this.currentNote.setFile(file);
    }
}
