package com.github.abhijitpparate.keeps.data.database;

import com.github.abhijitpparate.keeps.Constants;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;

public class MockDatabaseSource implements DatabaseSource {

    private boolean returnFailure = false;
    private boolean returnEmpty = false;

    @Override
    public Completable createProfile(Profile profile) {
        if (returnFailure){
            return Completable.error(new Exception("Create profile failed"));
        }
        return Completable.complete();
    }

    @Override
    public Maybe<Profile> getProfile(String uid) {
        if (returnFailure){
            return Maybe.error(new Exception("Get profile failed"));
        } else if (returnEmpty) {
            return Maybe.empty();
        }

        return Maybe.just(Constants.FAKE_PROFILE);
    }

    @Override
    public Maybe<List<Note>> getNotesForCurrentUser(String uid) {
        if (returnFailure){
            return Maybe.error(new Exception("Get notes failed"));
        } else if (returnEmpty) {
            return Maybe.empty();
        }

        return Maybe.just(Constants.FAKE_NOTES);
    }

    @Override
    public Maybe<Note> getNoteFromId(String uid, String noteId) {
        if (returnFailure){
            return Maybe.error(new Exception("Get profile failed"));
        } else if (returnEmpty) {
            return Maybe.empty();
        }

        Note note = new Note();

        for (Note n: Constants.FAKE_NOTES){
            if (n.getNoteId().equals(noteId)){
                note = n;
                break;
            }
        }

        return Maybe.just(note);
    }

    @Override
    public Completable createNewNote(String uid, Note note) {
        Constants.FAKE_NOTES.add(note);
        if (returnFailure){
            return Completable.error(new Exception("Create note failed"));
        }
        return Completable.complete();
    }

    @Override
    public Completable deleteProfile(String uid) {
        if (returnFailure){
            return Completable.error(new Exception("Profile deletion failed"));
        }
        return Completable.complete();
    }

    @Override
    public Completable updateProfile(Profile profile) {
        if (returnFailure){
            return Completable.error(new Exception("Profile update failed"));
        }
        return Completable.complete();
    }

    @Override
    public void setReturnFail(boolean bool) {
        this.returnFailure = bool;
    }

    @Override
    public void setReturnEmpty(boolean bool) {
        this.returnEmpty = bool;
    }
}