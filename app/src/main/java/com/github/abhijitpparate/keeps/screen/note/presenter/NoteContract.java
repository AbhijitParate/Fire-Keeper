package com.github.abhijitpparate.keeps.screen.note.presenter;


import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import com.github.abhijitpparate.checklistview.CheckListItem;
import com.github.abhijitpparate.keeps.data.database.Note;

import java.util.List;

public interface NoteContract {

    public enum NoteColor {
        DEFAULT, WHITE, PURPLE, RED, GREEN, YELLOW, BLUE, ORANGE
    }

    interface View {

        String getNoteTitle();
        String getNoteBody();
        String getCheckList();

        void setNote(Note notes);
        void setNoteTitle(String title);
        void setNoteBody(String body);
        void setNoteChecklist(List<CheckListItem> checklist);
        void setNoteColor(@ColorRes int color);

        void showOptionsPanel();
        void hideOptionsPanel();
        void showHomeScreen();

        void loadNoteIfAvailable();

        void setPresenter(NoteContract.Presenter presenter);
        void makeToast(@StringRes int strId);
        void makeToast(String message);

        void switchToChecklist();
        void switchToText();
    }

    interface Presenter {
        void onSaveClick();

        void loadNote(String noteId);

        void subscribe();
        void unsubscribe();

        void onChecklistClick(boolean isChecked);
        void onDrawingClick();
        void onAudioClick();
        void onVideoClick();
        void onImageClick();

        void onOptionsClick();
        void onDeleteClick();
        void onSendClick();
        void onDuplicateClick();
        void onLabelClick();
        void onColorSelected(NoteColor color);
    }
}
