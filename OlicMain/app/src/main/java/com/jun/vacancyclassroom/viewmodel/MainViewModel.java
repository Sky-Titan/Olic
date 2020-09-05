package com.jun.vacancyclassroom.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.item.BookMarkedRoom;
import com.jun.vacancyclassroom.item.Lecture;
import com.jun.vacancyclassroom.item.LectureRoom;
import com.jun.vacancyclassroom.item.ListLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel {

    private MyDAO dao;

    private ExecutorService executorService;


    public MainViewModel(@NonNull Application application)
    {
        dao = MyDatabase.getInstance(application).dao();
        executorService = Executors.newSingleThreadExecutor();
    }


    public LiveData<List<Lecture>> getLectures()
    {
        return dao.selectAllLectures();
    }

    public List<String> getTimeListOf(String lecture_room)
    {
        return dao.selectAllLectureTimesIn(lecture_room);
    }

    public LiveData<List<LectureRoom>> getLectureRooms()
    {
        return dao.selectAllLectureRooms();
    }



    public void addLecture(Lecture lecture)
    {
        executorService.execute(() -> dao.insertLecture(lecture));
    }

    public void removeLecture(Lecture lecture)
    {
        executorService.execute(() -> dao.deleteLecture(lecture));
    }

    public LiveData<List<BookMarkedRoom>> getBookMarkedRoomsData()
    {
        return dao.selectAllBookMarkedRoom();
    }

    public void addBookMarkedRoom(BookMarkedRoom bookMarkedRoom)
    {
        executorService.execute(() -> dao.insertBookMarkedRoom(bookMarkedRoom));
    }

    public void removeBookMarkedRoom(BookMarkedRoom bookMarkedRoom)
    {
        executorService.execute(() -> dao.deleteBookMarkedRoom(bookMarkedRoom));
    }

}
