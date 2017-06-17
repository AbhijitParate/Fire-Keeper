package com.github.abhijitpparate.keeps.screen.home;

import com.github.abhijitpparate.keeps.data.database.Note;

import java.util.List;

public interface OnNotesListChangedListener {
    void onNoteListChanged(List<Note> customers);
}