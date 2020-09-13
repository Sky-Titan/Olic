package com.jun.vacancyclassroom.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jun.vacancyclassroom.model.Building;
import com.jun.vacancyclassroom.model.Lecture;
import com.jun.vacancyclassroom.model.LectureRoom;
import com.jun.vacancyclassroom.model.SearchLecture;

import java.util.List;

@Dao
public interface MyDAO {

    @Query("SELECT * FROM Lecture")
    public LiveData<List<Lecture>> selectAllLectures();

    @Query("SELECT * FROM Lecture WHERE lecture_code = :code")
    public Lecture selectLecture(String code);

    @Query("SELECT * FROM LectureRoom")
    public LiveData<List<LectureRoom>> selectAllLectureRooms();

    @Query("SELECT * FROM LectureRoom WHERE lecture_room LIKE :building")
    public LiveData<List<LectureRoom>> selectLectureRooms(String building);

    @Query("SELECT * FROM LectureRoom WHERE isBookMarked = 1")
    public LiveData<List<LectureRoom>> selectAllBookMarkedRooms();

    @Query("SELECT * FROM LectureRoom WHERE lecture_room = :lecture_room AND isBookMarked = 1")
    public LiveData<LectureRoom> selectIFBookmarked(String lecture_room);

    @Query("SELECT * FROM SearchLecture")
    public LiveData<List<SearchLecture>> selectAllSearchLectures();

    @Query("SELECT * FROM Building")
    public LiveData<List<Building>> selectAllBuildings();

    @Query("SELECT DISTINCT lecture_time FROM Lecture WHERE lecture_room = :lecture_room")
    public List<String> selectAllLectureTimesIn(String lecture_room);

    @Query("SELECT DISTINCT * FROM Lecture WHERE lecture_room = :lecture_room")
    public List<Lecture> selectAllLectureIn(String lecture_room);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertLecture(Lecture lecture);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertBuilding(Building building);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertLectureRoom(LectureRoom lectureRoom);

    @Query("UPDATE LectureRoom SET isBookMarked = 1 WHERE lecture_room = :lectureRoom")
    public void unBookMarkRoom(String lectureRoom);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public void insertSearchLecture(SearchLecture searchlecture);


    @Query("DELETE FROM Building")
    public void deleteAllBuildings();

    @Query("DELETE FROM Lecture")
    public void deleteAllLectures();

    @Query("DELETE FROM LectureRoom")
    public void deleteAllLectureRooms();

    @Query("DELETE FROM SearchLecture")
    public void deleteAllSearchLecture();

    @Delete
    public void deleteLecture(Lecture lecture);

    @Query("UPDATE LectureRoom SET isBookMarked = 0 WHERE lecture_room = :lectureRoom")
    public void bookMarkRoom(String lectureRoom);

    @Delete
    public void deleteSearchLecture(SearchLecture searchLecture);
}
