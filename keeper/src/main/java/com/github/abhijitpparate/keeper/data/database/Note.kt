package com.github.abhijitpparate.keeper.data.database


import com.google.android.gms.maps.model.LatLng
import java.util.*

class Note {

    var noteId: String? = null
    var title: String? = null
    var body: String? = null
    var created: String? = null
    var checklist: String? = null
    var color: String? = null
    var place: Place? = null

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

    class Place() {
        var location:Location? = null
        var name:String? = null

        class Location() {
            var lat: Double? = null
            var lng: Double? = null
        }
    }
}