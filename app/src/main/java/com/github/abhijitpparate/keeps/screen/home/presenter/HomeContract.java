package com.github.abhijitpparate.keeps.screen.home.presenter;


import android.support.annotation.StringRes;

import com.github.abhijitpparate.keeps.data.auth.User;
import com.github.abhijitpparate.keeps.data.database.Note;
import com.github.abhijitpparate.keeps.data.database.Profile;

import java.util.List;
import java.util.UUID;

public interface HomeContract {

    public static enum NoteType {
        TEXT,
        IMAGE,
        VIDEO,
        AUDIO,
        DRAWING,
        LIST
    }

    interface View {
        void setUserInfo(Profile profile);
        void setNotes(List<Note> notes);

        void showLoginScreen();
        void showNewNoteScreen(NoteType noteType);

        void showNoteScreen(Note note);

        void setPresenter(Presenter presenter);
        void showProgressBar(boolean bool);
        void makeToast(@StringRes int strId);
        void makeToast(String message);
    }

    interface Presenter {

        void onRefresh();

        void onLogoutClick();
        void onNewNoteClick(NoteType noteType);

        void onNoteClick(Note note);

        void subscribe();
        void unsubscribe();
    }
}
