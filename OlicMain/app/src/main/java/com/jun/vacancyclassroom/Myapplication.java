package com.jun.vacancyclassroom;

import android.app.Application;

import com.jun.vacancyclassroom.database.DatabaseLibrary;

public class Myapplication extends Application {

    private String currentSemester;

    private DatabaseLibrary databaseLibrary;

    public String getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(String currentSemester) {
        this.currentSemester = currentSemester;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DatabaseLibrary.getInstance(getApplicationContext());
    }
}
