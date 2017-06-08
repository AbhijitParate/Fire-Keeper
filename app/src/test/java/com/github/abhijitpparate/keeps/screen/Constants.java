package com.github.abhijitpparate.keeps.screen;


import com.github.abhijitpparate.keeps.data.database.Note;
import com.github.abhijitpparate.keeps.data.database.Profile;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final String USERNAME = "john";
    public static final String EMAIL = "a@b.com";
    public static final String PASSWORD = "123456";

    public static final Profile FAKE_PROFILE =
            new Profile(
                    "123e4567-e89b-12d3-a456-426655440000",
                    "John",
                    "a@b.com",
                    "https://cdn.pixabay.com/photo/2015/09/02/12/27/matterhorn-918442_960_720.jpg"
            );

    public static final List<Note> FAKE_NOTES = new ArrayList<>();

    static {
        FAKE_NOTES.add(new Note(
                "To do list",
                "Talk to Tom"
        ));

        FAKE_NOTES.add(new Note(
                "New Idea",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."));

        FAKE_NOTES.add(new Note(
                "Reminder",
                "Call Alice"
        ));
    }


}