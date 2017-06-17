package com.github.abhijitpparate.keeper.screen.note.presenter


import android.support.annotation.ColorRes
import android.support.annotation.StringRes

import com.github.abhijitpparate.checklistview.CheckListItem
import com.github.abhijitpparate.keeper.data.database.Note
import com.google.android.gms.location.places.Place

interface NoteContract {

    enum class NoteColor {
        DEFAULT, WHITE, PURPLE, RED, GREEN, YELLOW, BLUE, ORANGE
    }

    interface View {

        fun getNoteUuid(): String?
        fun getNoteTitle(): String?
        fun getNoteBody(): String?
        fun getNoteCheckList(): String?
        fun getNoteColor(): String?
        fun getNotePlace(): Note.Place?

        fun setNote(note: Note)
        fun setNoteTitle(title: String?)
        fun setNoteBody(body: String?)
        fun setNoteChecklist(checklist: List<CheckListItem>)
        fun setNoteColor(@ColorRes colorInt: Int, colorString: String)
        fun setNoteLocation(place: Note.Place)

        fun showOptionsPanel()
        fun hideOptionsPanel()

        fun showLocationPicker()

        fun showHomeScreen()

        fun loadNoteIfAvailable()

        fun setPresenter(presenter: NoteContract.Presenter)
        fun showProgressbar(bool: Boolean)
        fun makeToast(@StringRes strId: Int)
        fun makeToast(message: String)

        fun switchToChecklist()
        fun switchToText()
    }

    interface Presenter {
        fun onSaveClick()

        fun loadNote(noteId: String)

        fun subscribe()
        fun unsubscribe()

        fun onChecklistClick(isChecked: Boolean)
        fun onDrawingClick()
        fun onLocationClick()
        fun onAudioClick()
        fun onVideoClick()
        fun onImageClick()

        fun onOptionsClick()
        fun onDeleteClick()
        fun onSendClick()
        fun onDuplicateClick()
        fun onLabelClick()
        fun onColorSelected(color: NoteColor)
        fun onLocationSelected(location: Place)
    }
}
