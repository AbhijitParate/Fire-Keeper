package com.github.abhijitpparate.keeper.screen.note.presenter;


import android.support.annotation.ColorRes;
import android.support.annotation.StringRes;

import com.github.abhijitpparate.checklistview.CheckListItem;
import com.github.abhijitpparate.keeper.data.database.Note;
import com.google.android.gms.location.places.Place;

import java.util.List;

public interface NoteContract {

    public enum NoteColor {
        DEFAULT, WHITE, PURPLE, RED, GREEN, YELLOW, BLUE, ORANGE
    }

    interface View {

        String getNoteId();
        String getNoteTitle();
        String getNoteBody();
        String getCheckList();
        String getNoteColor();
        Note.Place getNotePlace();

        void setNote(Note notes);
        void setNoteTitle(String title);
        void setNoteBody(String body);
        void setNoteChecklist(List<CheckListItem> checklist);
        void setNoteColor(@ColorRes int colorInt, String colorString);
        void setNotePlace(Note.Place place);

        void showOptionsPanel();
        void hideOptionsPanel();
        void showPlacePicker();

        void showHomeScreen();

        void loadNoteIfAvailable();

        void setPresenter(NoteContract.Presenter presenter);
        void showProgressbar(boolean bool);
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
        void onLocationClick();
        void onAudioClick();
        void onVideoClick();
        void onImageClick();

        void onOptionsClick();
        void onDeleteClick();
        void onSendClick();
        void onDuplicateClick();
        void onLabelClick();
        void onColorSelected(NoteColor color);
        void onPlaceSelected(Place place);
    }
}
