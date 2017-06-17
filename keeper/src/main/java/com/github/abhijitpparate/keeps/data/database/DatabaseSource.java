package com.github.abhijitpparate.keeps.data.database;

import com.github.abhijitpparate.keeps.data.auth.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public interface DatabaseSource {
    Completable createProfile(Profile profile);

    Maybe<Profile> getProfile(String uid);

    Maybe<List<Note>> getNotesForCurrentUser(String uid);

    Maybe<Note> getNoteFromId(String uid, String noteId);

    Completable createOrUpdateNote(String uid, Note note);

    Completable deleteProfile(String uid);
    Completable updateProfile(Profile profile);

    void setReturnFail(boolean bool);
    void setReturnEmpty(boolean bool);
}
