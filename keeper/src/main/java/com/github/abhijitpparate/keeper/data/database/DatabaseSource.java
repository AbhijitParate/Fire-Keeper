package com.github.abhijitpparate.keeper.data.database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface DatabaseSource {

    Completable createProfile(Profile profile);
    Maybe<Profile> getProfile(String uid);
    Completable deleteProfile(String uid);
    Completable updateProfile(Profile profile);

    Completable createOrUpdateNote(String uid, Note note);
    Maybe<Note> getNoteFromId(String uid, String noteId);
    Maybe<List<Note>> getNotesForCurrentUser(String uid);
    Completable deleteNote(String uid, String noteId);

    void setReturnFail(boolean bool);
    void setReturnEmpty(boolean bool);
}
