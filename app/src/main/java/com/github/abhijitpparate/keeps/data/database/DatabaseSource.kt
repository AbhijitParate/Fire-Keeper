package com.github.abhijitpparate.keeps.data.database

import com.github.abhijitpparate.keeps.data.auth.User

import io.reactivex.Completable
import io.reactivex.Maybe

interface DatabaseSource {
    fun createProfile(profile: Profile): Completable

    fun getProfile(uid: String): Maybe<Profile>

    fun getNotesForCurrentUser(uid: String): Maybe<List<Note>>

    fun getNoteFromId(uid: String, noteId: String): Maybe<Note>

    fun createOrUpdateNote(uid: String, note: Note): Completable

    fun deleteProfile(uid: String): Completable
    fun updateProfile(profile: Profile): Completable

    fun setReturnFail(bool: Boolean)
    fun setReturnEmpty(bool: Boolean)
}
