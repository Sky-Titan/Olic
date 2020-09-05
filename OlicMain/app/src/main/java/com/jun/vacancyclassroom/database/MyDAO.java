package com.jun.vacancyclassroom.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jun.vacancyclassroom.item.BookMarkedRoom;
import com.jun.vacancyclassroom.item.Building;
import com.jun.vacancyclassroom.item.Lecture;
import com.jun.vacancyclassroom.item.LectureRoom;

import java.util.List;

@Dao
public interface MyDAO {

    @Query("SELECT * FROM Lecture")
    public LiveData<List<Lecture>> selectAllLectures();

    @Query("SELECT * FROM LectureRoom")
    public LiveData<List<LectureRoom>> selectAllLectureRooms();

    @Query("SELECT * FROM BookMarkedRoom")
    public LiveData<List<BookMarkedRoom>> selectAllBookMarkedRoom();

    @Query("SELECT DISTINCT lecture_time FROM Lecture WHERE lecture_room = :lecture_room")
    public List<String> selectAllLectureTimesIn(String lecture_room);

    @Query("SELECT * FROM Building")
    public LiveData<List<Building>> selectAllBuildings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertLecture(Lecture lecture);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertBuilding(Building building);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertLectureRoom(LectureRoom lectureRoom);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertBookMarkedRoom(BookMarkedRoom bookMarkedRoom);

    @Query("DELETE FROM BookMarkedRoom")
    public void deleteAllBookmarkedRooms();

    @Query("DELETE FROM Building")
    public void deleteAllBuildings();

    @Query("DELETE FROM Lecture")
    public void deleteAllLectures();

    @Query("DELETE FROM LectureRoom")
    public void deleteAllLectureRooms();

    @Delete
    public void deleteLecture(Lecture lecture);

    @Delete
    public void deleteBookMarkedRoom(BookMarkedRoom bookMarkedRoom);
}