package com.github.abhijitpparate.keeps.data.auth;

public class AuthInjector {

    public static AuthSource getAuthSource() {
        return new MockAuthSource();
    }
}