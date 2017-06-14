package com.github.abhijitpparate.keeps.data.database

import com.github.abhijitpparate.keeps.Constants
import com.github.abhijitpparate.keeps.Constants.FAKE_NOTES

import io.reactivex.Completable
import io.reactivex.Maybe

class MockDatabaseSource : DatabaseSource {

    private var returnFailure = false
    private var returnEmpty = false

    override fun createProfile(profile: Profile): Completable {
        if (returnFailure) {
            return Completable.error(Exception("Create profile failed"))
        }
        return Completable.complete()
    }

    override fun getProfile(uid: String): Maybe<Profile> {
        if (returnFailure) {
            return Maybe.error<Profile>(Exception("Get profile failed"))
        } else if (returnEmpty) {
            return Maybe.empty<Profile>()
        }

        return Maybe.just(Constants.FAKE_PROFILE)
    }

    override fun getNotesForCurrentUser(uid: String): Maybe<List<Note>> {
        if (returnFailure) {
            return Maybe.error<List<Note>>(Exception("Get notes failed"))
        } else if (returnEmpty) {
            return Maybe.empty<List<Note>>()
        }

        return Maybe.just(Constants.FAKE_NOTES)
    }

    override fun createOrUpdateNote(uid: String, note: Note): Completable {
        var index : Int = 0
        (0 until FAKE_NOTES.count()).forEach { i ->
            index++
            if (FAKE_NOTES[i].noteId.equals(note.noteId)) {
                FAKE_NOTES[i].title = note.title
                FAKE_NOTES[i].body = note.body
                FAKE_NOTES[i].checklist = note.checklist
                FAKE_NOTES[i].color = note.color
                return@forEach
            }
        }
        if (index == FAKE_NOTES.count()) Constants.FAKE_NOTES.add(note)

        if (returnFailure) {
            return Completable.error(Exception("Create note failed"))
        }
        return Completable.complete()
    }

    override fun getNoteFromId(uid: String, noteId: String): Maybe<Note> {
        if (returnFailure) {
            return Maybe.error<Note>(Exception("Get profile failed"))
        } else if (returnEmpty) {
            return Maybe.empty<Note>()
        }

        var note = Note()

        for (n in Constants.FAKE_NOTES) {
            if (n.noteId == noteId) {
                note = n
                break
            }
        }

        return Maybe.just(note)
    }

    override fun deleteProfile(uid: String): Completable {
        if (returnFailure) {
            return Completable.error(Exception("Profile deletion failed"))
        }
        return Completable.complete()
    }

    override fun updateProfile(profile: Profile): Completable {
        if (returnFailure) {
            return Completable.error(Exception("Profile update failed"))
        }
        return Completable.complete()
    }

    override fun setReturnFail(bool: Boolean) {
        this.returnFailure = bool
    }

    override fun setReturnEmpty(bool: Boolean) {
        this.returnEmpty = bool
    }
}