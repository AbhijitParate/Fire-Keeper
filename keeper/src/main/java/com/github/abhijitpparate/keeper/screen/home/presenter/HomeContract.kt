package com.github.abhijitpparate.keeper.screen.home.presenter


import android.support.annotation.StringRes

import com.github.abhijitpparate.keeper.data.database.Note
import com.github.abhijitpparate.keeper.data.database.Profile

interface HomeContract {

    enum class NoteType {
        TEXT,
        IMAGE,
        VIDEO,
        AUDIO,
        DRAWING,
        LIST
    }

    interface View {
        fun setUserInfo(profile: Profile)
        fun setNotes(notes: List<Note>)

        fun showLoginScreen()
        fun showNewNoteScreen(noteType: NoteType)

        fun showNoteScreen(note: Note)

        fun setPresenter(presenter: Presenter)
        fun showProgressBar(bool: Boolean)
        fun makeToast(@StringRes strId: Int)
        fun makeToast(message: String)
    }

    interface Presenter {

        fun onRefresh()

        fun onLogoutClick()
        fun onNewNoteClick(noteType: NoteType)

        fun onNoteClick(note: Note)

        fun subscribe()
        fun unsubscribe()
    }
}
