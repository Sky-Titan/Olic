package com.jun.vacancyclassroom;

import android.app.Application;

public class Myapplication extends Application {

    private String currentSemester;

    public String getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(String currentSemester) {
        this.currentSemester = currentSemester;
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }
}
