package com.github.abhijitpparate.keeper.screen.note.presenter


import android.util.Log

import com.github.abhijitpparate.keeper.R
import com.github.abhijitpparate.keeper.data.auth.AuthInjector
import com.github.abhijitpparate.keeper.data.auth.AuthSource
import com.github.abhijitpparate.keeper.data.auth.User
import com.github.abhijitpparate.keeper.data.database.DatabaseInjector
import com.github.abhijitpparate.keeper.data.database.DatabaseSource
import com.github.abhijitpparate.keeper.data.database.Note
import com.github.abhijitpparate.keeper.scheduler.SchedulerInjector
import com.github.abhijitpparate.keeper.scheduler.SchedulerProvider
import com.github.abhijitpparate.keeper.utils.Utils

import java.util.HashMap

import io.reactivex.annotations.NonNull
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableMaybeObserver

import com.github.abhijitpparate.keeper.data.database.Note.NoteColor.BLUE
import com.github.abhijitpparate.keeper.data.database.Note.NoteColor.DEFAULT
import com.github.abhijitpparate.keeper.data.database.Note.NoteColor.GREEN
import com.github.abhijitpparate.keeper.data.database.Note.NoteColor.ORANGE
import com.github.abhijitpparate.keeper.data.database.Note.NoteColor.RED
import com.github.abhijitpparate.keeper.data.database.Note.NoteColor.WHITE
import com.github.abhijitpparate.keeper.data.database.Note.NoteColor.YELLOW
import com.github.abhijitpparate.keeper.utils.Utils.getNoteColor
import com.google.android.gms.location.places.Place

class NotePresenter(private var view: NoteContract.View) : NoteContract.Presenter {

    private var schedulerProvider: SchedulerProvider = SchedulerInjector.scheduler
    private var disposable: CompositeDisposable = CompositeDisposable()
    private var authSource: AuthSource = AuthInjector.authSource
    private var databaseSource: DatabaseSource = DatabaseInjector.databaseSource

    lateinit var currentUser: User
    private var currentNote: Note? = null

    private var isOptionsOpen = false

    init {
        view.setPresenter(this)
    }

    override fun onSaveClick() {
        assembleNote()
        disposable.add(
                databaseSource
                        .createOrUpdateNote(currentUser.uid.toString(), currentNote!!)
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

        if (!currentNote?.noteId.equals(view.getNoteUuid())) {
            currentNote = Note()
        }

        currentNote!!.title = view.getNoteTitle()
        currentNote!!.body = view.getNoteBody()
        currentNote!!.checklist = view.getNoteCheckList()
        currentNote!!.color = view.getNoteColor()
        currentNote!!.place = view.getNotePlace()
        return currentNote as Note
    }

    override fun loadNote(noteId: String) {
//        Log.d(TAG, "loadNote: ")
        view.showProgressbar(true)
        disposable.add(
                databaseSource
                        .getNoteFromId(currentUser.uid.toString(), noteId)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(
                                object : DisposableMaybeObserver<Note>() {

                                    override fun onComplete() {

                                    }

                                    override fun onSuccess(@NonNull note: Note) {
                                        currentNote = note
                                        view.setNote(note)
                                        view.setNoteTitle(note.title)
                                        if (note.body != null && note.body?.isNotEmpty() as Boolean) {
                                            view.setNoteBody(note.body)
                                        } else if (note.checklist != null && note.checklist != "[]") {
                                            view.setNoteChecklist(Utils.parseNoteChecklist(note.checklist!!))
                                            view.switchToChecklist()
                                        }
                                        note.color?.let {
                                            view.setNoteColor(getNoteColor(note.color), it)
                                        }

                                        note.place?.let {
                                            Log.d(TAG, note.place?.name)
                                            view.setNoteLocation(note.place as Note.Place)
                                        }
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
                        .retrieveUser()
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribeWith(object : DisposableMaybeObserver<User>() {
                            override fun onSuccess(@NonNull user: User) {
                                currentUser = user
                                view.loadNoteIfAvailable()
                            }

                            override fun onError(@NonNull e: Throwable) {

                            }

                            override fun onComplete() {
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

    override fun onLocationClick() {
        view.showLocationPicker()
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

    fun toggleOptionsPanel() {
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
            NoteContract.NoteColor.RED -> view.setNoteColor(R.color.noteColorRed, RED)
            NoteContract.NoteColor.GREEN -> view.setNoteColor(R.color.noteColorGreen, GREEN)
            NoteContract.NoteColor.YELLOW -> view.setNoteColor(R.color.noteColorYellow, YELLOW)
            NoteContract.NoteColor.BLUE -> view.setNoteColor(R.color.noteColorBlue, BLUE)
            NoteContract.NoteColor.ORANGE -> view.setNoteColor(R.color.noteColorOrange, ORANGE)
            else -> view.setNoteColor(R.color.colorBackground, DEFAULT)
        }
    }

    override fun onLocationSelected(location: Place) {
        val place:Note.Place = Note.Place()
        place.location?.lat = location.latLng.latitude
        place.location?.lng = location.latLng.longitude
        place.name = location.name.toString()
        currentNote?.place = place
        view.setNoteLocation(place)
    }

    companion object {

        val TAG = "NotePresenter"

        @JvmStatic var colorMap: MutableMap<String, Int> = HashMap()

        init {
            colorMap.put(DEFAULT,   R.color.colorBackground)
            colorMap.put(WHITE,     android.R.color.white)
            colorMap.put(RED,       R.color.noteColorRed)
            colorMap.put(GREEN,     R.color.noteColorGreen)
            colorMap.put(YELLOW,    R.color.noteColorYellow)
            colorMap.put(BLUE,      R.color.noteColorBlue)
            colorMap.put(ORANGE,    R.color.noteColorOrange)
        }
    }
}
