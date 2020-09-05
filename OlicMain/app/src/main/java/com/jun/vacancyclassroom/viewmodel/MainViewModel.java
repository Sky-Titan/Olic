package com.jun.vacancyclassroom.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.model.BookMarkedRoom;
import com.jun.vacancyclassroom.model.Building;
import com.jun.vacancyclassroom.model.Lecture;
import com.jun.vacancyclassroom.model.LectureRoom;
import com.jun.vacancyclassroom.model.SearchLecture;

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

    //검색강의 가져오기
    public LiveData<List<SearchLecture>> getSearchLectures()
    {
        return dao.selectAllSearchLectures();
    }

    //검색강의 추가하기
    public void addSearchLecture(SearchLecture searchLecture)
    {
        executorService.execute(() -> dao.insertSearchLecture(searchLecture));
    }

    //검색강의 제거하기
    public void removeSearchLecture(SearchLecture searchLecture)
    {
        executorService.execute(() -> dao.deleteSearchLecture(searchLecture));
    }
    //빌딩 가져오기
    public LiveData<List<Building>> getBuildings()
    {
        return dao.selectAllBuildings();
    }

    //강의 가져오기
    public LiveData<List<Lecture>> getLectures()
    {
        return dao.selectAllLectures();
    }

    //강의실별 강의시간들 가져오기
    public List<String> getTimeListOf(String lecture_room)
    {
        return dao.selectAllLectureTimesIn(lecture_room);
    }

    //강의실 가져오기
    public LiveData<List<LectureRoom>> getLectureRooms()
    {
        return dao.selectAllLectureRooms();
    }

    //강의 추가
    public void addLecture(Lecture lecture)
    {
        executorService.execute(() -> dao.insertLecture(lecture));
    }

    //강의 제거
    public void removeLecture(Lecture lecture)
    {
        executorService.execute(() -> dao.deleteLecture(lecture));
    }

    //즐겨찾기 강의실 가져오기
    public LiveData<List<BookMarkedRoom>> getBookMarkedRoomsData()
    {
        return dao.selectAllBookMarkedRooms();
    }

    //즐겨찾기 강의실 추가
    public void addBookMarkedRoom(BookMarkedRoom bookMarkedRoom)
    {
        executorService.execute(() -> dao.insertBookMarkedRoom(bookMarkedRoom));
    }

    //즐겨찾기 강의실 삭제
    public void removeBookMarkedRoom(BookMarkedRoom bookMarkedRoom)
    {
        executorService.execute(() -> dao.deleteBookMarkedRoom(bookMarkedRoom));
    }

}
