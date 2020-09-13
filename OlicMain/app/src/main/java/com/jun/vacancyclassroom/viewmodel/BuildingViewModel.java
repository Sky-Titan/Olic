package com.jun.vacancyclassroom.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.model.LectureRoom;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BuildingViewModel extends ViewModel {

    private MyDAO dao;

    private ExecutorService executorService;

    public BuildingViewModel(@NonNull Application application)
    {
        dao = MyDatabase.getInstance(application).dao();
        executorService = Executors.newSingleThreadExecutor();
    }

    //강의실 가져오기
    public LiveData<List<LectureRoom>> getLectureRooms(String building)
    {
        return dao.selectLectureRooms(building);
    }
}
