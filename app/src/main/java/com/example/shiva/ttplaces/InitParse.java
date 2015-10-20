package com.example.shiva.ttplaces;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Shiva on 9/26/2015.
 */

    //This class is used to initialize the parse by passing the required id's into the initialize function.
    //We would now be able to use the parse system and its function in our app.
public class InitParse extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "4SkJs9vZM7ev0Hpj02ZaC4fnQ6Sy6yyQEkJdnwsK", "ZmvXXAkbYBCdrzP8iAx5nqUUvuUIPHwgOmXhIbyC");
    }
}
