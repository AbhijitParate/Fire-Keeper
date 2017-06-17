package com.github.abhijitpparate.keeper.data.database

class Profile {

    var uid: String? = null
    var email: String? = null
    var photoURL: String? = null
    var name: String? = null

    /**
     * Empty constructor is needed by firebase
     */
    constructor()

    constructor(uid: String, name: String, email: String) {
        this.uid = uid
        this.name = name
        this.email = email
        this.photoURL = ""
    }

    constructor(uid: String, name: String, email: String, photoURL: String) {
        this.uid = uid
        this.name = name
        this.email = email
        this.photoURL = photoURL
    }
}
