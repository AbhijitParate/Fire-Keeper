package com.github.abhijitpparate.keeps.screen.home.presenter


import android.support.annotation.StringRes

import com.github.abhijitpparate.keeps.data.auth.User
import com.github.abhijitpparate.keeps.data.database.Note
import com.github.abhijitpparate.keeps.data.database.Profile
import java.util.UUID

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
