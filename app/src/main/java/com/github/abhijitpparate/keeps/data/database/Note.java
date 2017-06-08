package com.github.abhijitpparate.keeps.data.database;


import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Note {

    private String noteId;
    private String title;
    private String body;
    private String created;
    private String checklist;

    public Note(String title, String body) {
        this.noteId = UUID.randomUUID().toString();
        this.title = title;
        this.body = body;
        this.created = Calendar.getInstance().getTime().toString();
    }

    public Note() {
        this.noteId = UUID.randomUUID().toString();
        this.created = Calendar.getInstance().getTime().toString();
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }
}