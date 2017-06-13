package com.github.abhijitpparate.keeps.screen.note.presenter


import android.util.Log

import com.github.abhijitpparate.keeps.R
import com.github.abhijitpparate.keeps.data.auth.AuthInjector
import com.github.abhijitpparate.keeps.data.auth.AuthSource
import com.github.abhijitpparate.keeps.data.auth.User
import com.github.abhijitpparate.keeps.data.database.DatabaseInjector
import com.github.abhijitpparate.keeps.data.database.DatabaseSource
import com.github.abhijitpparate.keeps.data.database.Note
import com.github.abhijitpparate.keeps.scheduler.SchedulerInjector
import com.github.abhijitpparate.keeps.scheduler.SchedulerProvider
import com.github.abhijitpparate.keeps.utils.Utils

import java.util.HashMap

import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver

import com.github.abhijitpparate.keeps.data.database.Note.NoteColor.BLUE
import com.github.abhijitpparate.keeps.data.database.Note.NoteColor.DEFAULT
import com.github.abhijitpparate.keeps.data.database.Note.NoteColor.GREEN
import com.github.abhijitpparate.keeps.data.database.Note.NoteColor.ORANGE
import com.github.abhijitpparate.keeps.data.database.Note.NoteColor.RED
import com.github.abhijitpparate.keeps.data.database.Note.NoteColor.WHITE
import com.github.abhijitpparate.keeps.data.database.Note.NoteColor.YELLOW
import com.github.abhijitpparate.keeps.utils.Utils.getNoteColor

class NotePresenter(private val view: NoteContract.View) : NoteContract.Presenter {

    private val schedulerProvider: SchedulerProvider
    private val disposable: CompositeDisposable

    private val authSource: AuthSource = AuthInjector.authSource
    private val databaseSource: DatabaseSource = DatabaseInjector.databaseSource

    private var currentUser: User? = null
    private var currentNote: Note? = null

    private var isOptionsOpen = false

    init {

        this.schedulerProvider = SchedulerInjector.scheduler
        this.disposable = CompositeDisposable()

        view.setPresenter(this)
    }

    override fun onSaveClick() {
        assembleNote()
        disposable.add(
                databaseSource
                        .createOrUpdateNote(currentUser!!.uid.toString(), currentNote!!)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                object : DisposableCompletableObserver() {
                                    override fun onComplete() {
                                        view.showHomeScreen()
                                    }

                                    override fun onError(e: Throwable) {
                                        view.makeToast(e.message.toString())
                                    }
                                }
                        )
        )
    }

    private fun assembleNote(): Note {
        if (currentNote == null) currentNote = Note()

        if (currentNote!!.noteId != view.noteId) {
            currentNote = Note()
        }

        currentNote!!.title = view.noteTitle
        currentNote!!.body = view.noteBody
        currentNote!!.checklist = view.checkList
        currentNote!!.color = view.noteColor
        return currentNote as Note
    }

    override fun loadNote(noteId: String) {
        Log.d(TAG, "loadNote: ")
        view.showProgressbar(true)
        disposable.add(
                databaseSource
                        .getNoteFromId(currentUser!!.uid.toString(), noteId)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                object : DisposableMaybeObserver<Note>() {

                                    override fun onComplete() {

                                    }

                                    override fun onSuccess(@NonNull note: Note) {
                                        currentNote = note
                                        view.setNote(note)
                                        view.noteTitle = note.title.toString()
                                        if (note.body != null && !note.body!!.isEmpty()) {
                                            view.noteBody = note.body!!
                                        } else if (note.checklist != null && note.checklist != "[]") {
                                            view.setNoteChecklist(Utils.parseNoteChecklist(note.checklist!!))
                                            view.switchToChecklist()
                                        }
                                        view.setNoteColor(getNoteColor(note.color), note.color.toString())
                                        view.showProgressbar(false)
                                    }

                                    override fun onError(e: Throwable) {
                                        view.makeToast(e.message.toString())
                                    }
                                }
                        )
        )
    }

    override fun subscribe() {
        getUserData()
    }

    private fun getUserData() {
        //        Log.d(TAG, "getUserData: ");
        disposable.add(
                authSource
                        .user
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<User>() {
                            override fun onSuccess(@NonNull user: User) {
                                currentUser = user
                                //                                Log.d(TAG, "onSuccess: ");
                                view.loadNoteIfAvailable()
                            }

                            override fun onError(@NonNull e: Throwable) {
                                //                                Log.d(TAG, "onError: ");
                            }

                            override fun onComplete() {
                                //                                Log.d(TAG, "onComplete: ");
                            }
                        })
        )
    }

    override fun unsubscribe() {
        disposable.clear()
    }

    override fun onChecklistClick(isChecked: Boolean) {
        if (isChecked)
            view.switchToChecklist()
        else
            view.switchToText()
    }

    override fun onDrawingClick() {

    }

    override fun onAudioClick() {

    }

    override fun onVideoClick() {

    }

    override fun onImageClick() {

    }

    override fun onOptionsClick() {
        toggleOptionsPanel()
    }

    private fun toggleOptionsPanel() {
        if (isOptionsOpen) {
            view.hideOptionsPanel()
        } else {
            view.showOptionsPanel()
        }
        isOptionsOpen = !isOptionsOpen
    }

    override fun onDeleteClick() {
        Log.d(TAG, "onDeleteClick: ")
        toggleOptionsPanel()
    }

    override fun onSendClick() {
        Log.d(TAG, "onSendClick: ")
        toggleOptionsPanel()
    }

    override fun onDuplicateClick() {
        Log.d(TAG, "onDuplicateClick: ")
        toggleOptionsPanel()
    }

    override fun onLabelClick() {
        Log.d(TAG, "onLabelClick: ")
        toggleOptionsPanel()
    }

    override fun onColorSelected(color: NoteContract.NoteColor) {
        Log.d(TAG, "onColorSelected: " + color)
        when (color) {
            NoteContract.NoteColor.WHITE -> view.setNoteColor(android.R.color.white, WHITE)
            NoteContract.NoteColor.RED -> view.setNoteColor(android.R.color.holo_red_light, RED)
            NoteContract.NoteColor.GREEN -> view.setNoteColor(android.R.color.holo_green_light, GREEN)
            NoteContract.NoteColor.YELLOW -> view.setNoteColor(android.R.color.holo_orange_light, YELLOW)
            NoteContract.NoteColor.BLUE -> view.setNoteColor(android.R.color.holo_blue_bright, BLUE)
            NoteContract.NoteColor.ORANGE -> view.setNoteColor(android.R.color.holo_orange_dark, ORANGE)
            else -> view.setNoteColor(R.color.colorBackground, DEFAULT)
        }
    }

    companion object {

        val TAG = "NotePresenter"

        @JvmStatic var colorMap: MutableMap<String, Int> = HashMap()

        init {
            colorMap.put(DEFAULT, R.color.colorBackground)
            colorMap.put(WHITE, android.R.color.white)
            colorMap.put(RED, android.R.color.holo_red_light)
            colorMap.put(GREEN, android.R.color.holo_green_light)
            colorMap.put(YELLOW, android.R.color.holo_orange_light)
            colorMap.put(BLUE, android.R.color.holo_blue_bright)
            colorMap.put(ORANGE, android.R.color.holo_orange_dark)
        }
    }
}
