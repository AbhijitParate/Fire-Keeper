package com.github.abhijitpparate.keeps.data;


import com.github.abhijitpparate.keeps.data.database.Profile;

public class Constants {

    public static final String USERNAME = "ab";
    public static final String EMAIL = "a@b.com";
    public static final String PASSWORD = "123456";

    public static final Profile FAKE_PROFILE =
            new Profile(
                    "123e4567-e89b-12d3-a456-426655440000",
                    "Abhijit",
                    "a@b.com",
                    "https://cdn.pixabay.com/photo/2015/09/02/12/27/matterhorn-918442_960_720.jpg"
            );


}