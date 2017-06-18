package com.github.abhijitpparate.keeper.data.database;


import com.github.abhijitpparate.keeper.data.storage.File;

import java.util.Calendar;
import java.util.UUID;

public class Note {

    private int index;
    private String noteId;
    private String title;
    private String body;
    private String created;
    private String updated;
    private String checklist;
    private String color;
    private Place place;
    private File file;

    public static class NoteColor {
        public static final String DEFAULT = "DEFAULT";
        public static final String WHITE = "WHITE";
        public static final String RED = "RED";
        public static final String GREEN = "GREEN";
        public static final String YELLOW = "YELLOW";
        public static final String BLUE = "BLUE";
        public static final String ORANGE = "ORANGE";
    }

    public Note(String title, String body) {
        this.noteId = UUID.randomUUID().toString();
        this.title = title;
        this.body = body;
        this.created = Calendar.getInstance().getTime().toString();
        this.updated = Calendar.getInstance().getTime().toString();
    }

    public Note() {
        // Required by Firebase
    }

    public static Note newNote() {
        Note note = new Note();
        note.setNoteId(UUID.randomUUID().toString());
        note.setCreated(Calendar.getInstance().getTime().toString());
        note.setUpdated(Calendar.getInstance().getTime().toString());
        return note;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getChecklist() {
        return checklist;
    }

    public void setChecklist(String checklist) {
        this.checklist = checklist;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public static class Place {

        String name;
        LatLng location;

        public Place(){
            // Required by Firebase
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LatLng getLocation() {
            return location;
        }

        public void setLocation(LatLng location) {
            this.location = location;
        }

        public static class LatLng {

            String latitude;
            String longitude;

            public LatLng(double latitude, double longitude) {
                setLatitude(latitude);
                setLongitude(longitude);
            }

            public LatLng(){
                // Required by Firebase
            }

            public double getLatitude() {
                double d;
                try {
                    d = Double.parseDouble(latitude);
                } catch (Exception e){
                    d = 0f;
                }

                return d;
            }

            public void setLatitude(double latitude) {
                this.latitude = String.valueOf(latitude);
            }

            public double getLongitude() {
                double d;
                try {
                    d = Double.parseDouble(longitude);
                } catch (Exception e){
                    d = 0f;
                }
                return d;
            }

            public void setLongitude(double longitude) {
                this.longitude = String.valueOf(longitude);
            }
        }
    }
}