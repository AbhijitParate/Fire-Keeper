package com.github.abhijitpparate.keeps.screen.note.presenter


import android.support.annotation.ColorRes
import android.support.annotation.StringRes

import com.github.abhijitpparate.checklistview.CheckListItem
import com.github.abhijitpparate.keeps.data.database.Note

interface NoteContract {

    enum class NoteColor {
        DEFAULT, WHITE, PURPLE, RED, GREEN, YELLOW, BLUE, ORANGE
    }

    interface View {

        val noteId: String
        var noteTitle: String
        var noteBody: String
        val checkList: String
        val noteColor: String

        fun setNote(notes: Note)
        fun setNoteChecklist(checklist: List<CheckListItem>)
        fun setNoteColor(@ColorRes colorInt: Int, colorString: String)

        fun showOptionsPanel()
        fun hideOptionsPanel()
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
        fun onAudioClick()
        fun onVideoClick()
        fun onImageClick()

        fun onOptionsClick()
        fun onDeleteClick()
        fun onSendClick()
        fun onDuplicateClick()
        fun onLabelClick()
        fun onColorSelected(color: NoteColor)
    }
}
