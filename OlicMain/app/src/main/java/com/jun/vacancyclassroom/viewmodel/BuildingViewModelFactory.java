package com.jun.vacancyclassroom.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.jun.vacancyclassroom.model.Building;

public class BuildingViewModelFactory implements ViewModelProvider.Factory {

    private Application application;

    public BuildingViewModelFactory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if(modelClass.isAssignableFrom(BuildingViewModel.class))
            return (T)new BuildingViewModel(application);
        return null;
    }
}
