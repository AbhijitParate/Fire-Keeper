package com.github.abhijitpparate.keeper.data.auth


object AuthInjector {

    val authSource: AuthSource
        get() = FirebaseAuthService.authSource
}