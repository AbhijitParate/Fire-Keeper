package com.github.abhijitpparate.keeps


import com.github.abhijitpparate.keeps.data.auth.Credentials
import com.github.abhijitpparate.keeps.data.auth.User
import com.github.abhijitpparate.keeps.data.database.Note
import com.github.abhijitpparate.keeps.data.database.Profile

import java.util.ArrayList
import java.util.UUID

object Constants {

    val USERNAME = "john"
    val EMAIL = "a@b.com"
    val PASSWORD = "123456"

    val CREDENTIALS = Credentials(USERNAME, EMAIL, PASSWORD)
    val USER = User(UUID.randomUUID().toString(), USERNAME, EMAIL)

    val FAKE_PROFILE = Profile(
            "123e4567-e89b-12d3-a456-426655440000",
            "John",
            "a@b.com",
            "https://cdn.pixabay.com/photo/2015/09/02/12/27/matterhorn-918442_960_720.jpg"
    )

    val FAKE_NOTES: MutableList<Note> = ArrayList()

    init {
        FAKE_NOTES.add(Note(
                "To do list",
                "Talk to Tom"
        ))

        FAKE_NOTES.add(Note(
                "New Idea",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."))

        FAKE_NOTES.add(Note(
                "Reminder",
                "Call Alice"
        ))
    }


}