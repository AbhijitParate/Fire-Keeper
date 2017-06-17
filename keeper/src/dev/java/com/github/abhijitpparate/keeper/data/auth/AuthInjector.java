package com.github.abhijitpparate.keeper.data.auth;


public class AuthInjector {

    public static AuthSource getAuthSource() {
        return FirebaseAuthService.getAuthSource();
    }
}