package com.jun.vacancyclassroom.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class TimeTableViewModelFactory implements ViewModelProvider.Factory {

    private Application application;

    public TimeTableViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(TimeTableViewModel.class))
            return (T)new TimeTableViewModel(application);
        return null;
    }
}
