package com.github.abhijitpparate.keeps.data.auth

object AuthInjector {

    val authSource: AuthSource
        get() = MockAuthSource()
}