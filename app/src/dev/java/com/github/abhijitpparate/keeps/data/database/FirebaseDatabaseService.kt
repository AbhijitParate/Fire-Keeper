package com.github.abhijitpparate.keeps.data.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Completable
import io.reactivex.Maybe
import java.lang.Error
import java.util.*

class FirebaseDatabaseService private constructor() : DatabaseSource {

    override fun createProfile(profile: Profile): Completable {
        return Completable.create { e ->
            val rootRef = FirebaseDatabase.getInstance().reference
            val idRef = rootRef.child(USERS).child(profile.uid)
            idRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        idRef.setValue(profile)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        e.onComplete()
                                    } else {
                                        e.onError(task.exception)
                                    }
                                }
                    } else {
                        e.onComplete()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FIREBASE", databaseError.toString())
                    e.onError(Error("Database error"))
                }
            })
        }
    }

    override fun getProfile(uid: String): Maybe<Profile> {
        return Maybe.create { e ->
            val rootRef = FirebaseDatabase.getInstance().reference
            val idRef = rootRef.child(USERS).child(uid)
            idRef.addListenerForSingleValueEvent(object : ValueEventListener {
                //does this check node for activeUser exists?
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val profile = snapshot.getValue(Profile::class.java)
                        e.onSuccess(profile)
                    } else {
                        e.onError(Error("Profile not found"))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FIREBASE", databaseError.toString())
                    e.onError(Error("Database error"))
                }
            })
        }
    }

    override fun getNoteFromId(uid: String, noteId: String): Maybe<Note> {
        return Maybe.create { e ->
            val rootRef = FirebaseDatabase.getInstance().reference
            val idRef = rootRef.child(USERS).child(uid).child(NOTES).child(noteId)
            idRef.addListenerForSingleValueEvent(object : ValueEventListener {
                //does this check node for activeUser exists?
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val note = snapshot.getValue(Note::class.java)
                        e.onSuccess(note)
                    } else {
                        e.onError(Error("Note not found"))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FIREBASE", databaseError.toString())
                    e.onError(Error("Database error"))
                }
            })
        }
    }

    override fun getNotesForCurrentUser(uid: String): Maybe<List<Note>> {
        return Maybe.create { e ->
            val rootRef = FirebaseDatabase.getInstance().reference
            val idRef = rootRef.child(USERS).child(uid).child(NOTES)
            idRef.addListenerForSingleValueEvent(object : ValueEventListener {
                //does this check node for activeUser exists?
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val notes = ArrayList<Note>()
                        for (postSnapshot in snapshot.children) {
                            Log.d("FIREBASE", "onDataChange: " + postSnapshot.toString())
                            val n = postSnapshot.getValue(Note::class.java) as Note
                            notes.add(n)
                        }
                        e.onSuccess(notes)
                    } else {
                        e.onError(Error("Notes not found"))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FIREBASE", databaseError.toString())
                }
            })
        }
    }

    override fun createOrUpdateNote(uid: String, note: Note): Completable {
        return Completable.create { e ->
            val rootRef = FirebaseDatabase.getInstance().reference
            val idRef = rootRef.child(USERS).child(uid).child(NOTES).child(note.noteId)
            idRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    idRef.setValue(note)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    e.onComplete()
                                } else {
                                    e.onError(task.exception)
                                }
                            }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("FIREBASE", databaseError.toString())
                }
            })
        }
    }

    override fun deleteProfile(uid: String): Completable {
        return Completable.create { e ->
            val rootRef = FirebaseDatabase.getInstance().reference
            rootRef.child(USERS)
                    .child(uid)
                    .setValue(null)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            e.onComplete()
                        } else {
                            e.onError(task.exception)
                        }
                    }
        }
    }

    override fun updateProfile(profile: Profile): Completable {
        return Completable.create { e ->
            val rootRef = FirebaseDatabase.getInstance().reference
            rootRef.child(USERS)
                    .child(profile.uid)
                    .setValue(profile)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            e.onComplete()
                        } else {
                            e.onError(task.exception)
                        }
                    }
        }
    }

    override fun setReturnFail(bool: Boolean) {
        //only for testing purposes
    }

    override fun setReturnEmpty(bool: Boolean) {
        //only for testing purposes

    }

    companion object {

        private val USERS = "USERS"
        private val NOTES = "NOTES"

        val instance: DatabaseSource
            get() = FirebaseDatabaseService()
    }

}