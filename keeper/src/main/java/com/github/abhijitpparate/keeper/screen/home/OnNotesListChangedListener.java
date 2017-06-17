package com.github.abhijitpparate.keeper.screen.home;

import com.github.abhijitpparate.keeper.data.database.Note;

import java.util.List;

public interface OnNotesListChangedListener {
    void onNoteListChanged(List<Note> customers);
}