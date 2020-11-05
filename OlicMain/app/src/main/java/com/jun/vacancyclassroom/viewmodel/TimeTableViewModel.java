package com.jun.vacancyclassroom.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.model.Lecture;
import com.jun.vacancyclassroom.model.LectureRoom;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimeTableViewModel extends ViewModel {

    private MyDAO dao;

    private ExecutorService executorService;

    public TimeTableViewModel(@NonNull Application application)
    {
        dao = MyDatabase.getInstance(application).dao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<LectureRoom> getBookMarked(String lecture_room)
    {
        return dao.selectIFBookmarked(lecture_room);
    }

    //즐겨찾기 강의실 추가
    public void addBookMarkedRoom(String bookMarkedRoom)
    {
        executorService.execute(() -> dao.bookMarkRoom(bookMarkedRoom));
    }

    //즐겨찾기 강의실 삭제
    public void removeBookMarkedRoom(String bookMarkedRoom)
    {
        executorService.execute(() -> dao.unBookMarkRoom(bookMarkedRoom));
    }

    public List<Lecture> getLectureList(String lecture_room)
    {
        return dao.selectAllLectureIn(lecture_room);
    }

    public Lecture getLecture(String code)
    {
        return dao.selectLecture(code);
    }
}
