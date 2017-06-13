package com.github.abhijitpparate.keeps.data.database


import java.util.Calendar
import java.util.Date
import java.util.UUID

class Note {

    var noteId: String? = null
    var title: String? = null
    var body: String? = null
    var created: String? = null
    var checklist: String? = null
    var color: String? = null

    object NoteColor {
        val DEFAULT = "DEFAULT"
        val WHITE = "WHITE"
        val RED = "RED"
        val GREEN = "GREEN"
        val YELLOW = "YELLOW"
        val BLUE = "BLUE"
        val ORANGE = "ORANGE"
    }

    constructor(title: String, body: String) {
        this.noteId = UUID.randomUUID().toString()
        this.title = title
        this.body = body
        this.created = Calendar.getInstance().time.toString()
    }

    constructor() {
        this.noteId = UUID.randomUUID().toString()
        this.created = Calendar.getInstance().time.toString()
    }
}