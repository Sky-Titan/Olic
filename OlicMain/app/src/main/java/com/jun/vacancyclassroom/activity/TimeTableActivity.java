package com.jun.vacancyclassroom.activity;

import android.content.Intent;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.vacancyclassroom.R;
import com.jun.vacancyclassroom.Myapplication;
import com.jun.vacancyclassroom.model.Lecture;
import com.jun.vacancyclassroom.model.LectureRoom;
import com.jun.vacancyclassroom.viewmodel.TimeTableViewModel;
import com.jun.vacancyclassroom.viewmodel.TimeTableViewModelFactory;

import org.techtown.timetablelayout.CollegeTimeTableLayout;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TimeTableActivity extends AppCompatActivity {


    private Button bookmark_button;
    private Button isPossible_button;

    private TextView classroom_textview;
    private String lectureRoom;

    private View view;
    private CollegeTimeTableLayout timeTableLayout;

    private TimeTableViewModel viewModel;

    private Myapplication myapplication;

    private static final String TAG = "TimeTableActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().from(this).inflate(R.layout.activity_time_table,null);

        myapplication = (Myapplication)getApplication();
        setTitle(myapplication.getCurrentSemester());
        setContentView(view);

        viewModel = new ViewModelProvider(this, new TimeTableViewModelFactory(getApplication())).get(TimeTableViewModel.class);

        //표시할 강의실 이름 가져옴
        Intent intent = getIntent();
        lectureRoom = intent.getExtras().getString("classroom");

        timeTableLayout = findViewById(R.id.timetable_layout);
        setTimeTable();

        //강의실 이름 설정
        classroom_textview = findViewById(R.id.classroom_name_timetable);
        classroom_textview.setText(lectureRoom);


        //북마크 상태 설정
        bookmark_button = findViewById(R.id.bookmarkButton_timetable);
        setBookMarkStatus();

        //이용 가능 상태 설정
        isPossible_button = findViewById(R.id.isPossibleButton_timetable);
        isPossible_button.setBackgroundColor(Color.GREEN);

        makeTimeTable();
    }

    private void setBookMarkStatus() {

        Observer<LectureRoom> observer = (bookMarkedRoom) -> {
            //북마크에 없음
            if(bookMarkedRoom == null)
            {
                bookmark_button.setBackground(getDrawable(R.drawable.ripple_lime_green));
                bookmark_button.setText("즐겨찾기 추가");
            }
            //북마크에 있음
            else {
                bookmark_button.setBackground(getDrawable(R.drawable.ripple_red));
                bookmark_button.setText("즐겨찾기 해제");
            }
        };

        viewModel.getBookMarked(lectureRoom).observe(this, observer);

        bookmark_button.setOnClickListener(view1 -> {
            //북마크 추가
            if(bookmark_button.getText().equals("즐겨찾기 추가"))
                viewModel.addBookMarkedRoom(lectureRoom);
            //북마크해제
            else
                viewModel.removeBookMarkedRoom(lectureRoom);
        });

    }

    private void setTimeTable()
    {
        //행이름 설정
        String[] rowNames = new String[timeTableLayout.getRowCount()];

        rowNames[0] = "";

        int time = 9;
        for(int i = 1;i < rowNames.length;i += 2)
        {
            rowNames[i] = "";
            if(time < 10)
                rowNames[i] = "0";

            rowNames[i] += time + ":";
            rowNames[i] += "00";
            time ++;
        }

        time = 9;
        for(int i = 2;i < rowNames.length;i += 2)
        {
            rowNames[i] = "";
            if(time < 10)
                rowNames[i] = "0";

            rowNames[i] += time + ":";
            rowNames[i] += "30";
            time ++;
        }

        timeTableLayout.setRowNames(rowNames);

        //열이름 설정
        String[] columnNames = {"","월","화","수","목","금"};
        timeTableLayout.setColumnNames(columnNames);
    }

    public void makeTimeTable()
    {
        io.reactivex.Observable.create(emitter -> {
            emitter.onNext(viewModel.getLectureList(lectureRoom));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    List<Lecture> lectureList = (List<Lecture>) o;

                    for(int k = 0;k < lectureList.size();k++)
                    {
                        String[] times = lectureList.get(k).lecture_time.split(" ");


                        String before_hour = "", before_minute = "";
                        String day = "";
                        String after_hour = "", after_minute = "";

                        for (int i = 0; i < times.length; i++)
                        {
                            if (times[i].length() == 6)//새로운 시간대 beforetime(시작시간)이랑 요일구하기
                            {
                                //ex)화16:00
                                //요일
                                day = times[i].substring(0, 1);

                                //시작 시간
                                before_hour = times[i].substring(1, 3);
                                before_minute = times[i].substring(4, 6);
                            }
                            else if (times[i].length() == 5)//aftertime 구하기 (종료시간)
                            {
                                // ex)16:00
                                //종료시간
                                after_hour = times[i].substring(0, 2);
                                after_minute = times[i].substring(3, 5);

                                setUsable(before_hour, before_minute, after_hour, after_minute, day);
                                calculateSpanCells(before_hour, before_minute, day, after_hour, after_minute, lectureList.get(k));
                            }
                        }
                    }
                });

    }

    //강의실 이용가능 여부 판단
    private void setUsable(String before_hour, String before_minute, String after_hour, String after_minute, String day)
    {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");

        Calendar before = Calendar.getInstance(timeZone);
        before.set(Calendar.DAY_OF_WEEK, dayToNum(day));
        before.set(Calendar.HOUR_OF_DAY, Integer.parseInt(before_hour));
        before.set(Calendar.MINUTE, Integer.parseInt(before_minute));
        //Log.d(TAG, "BEFORE : "+before.get(Calendar.DATE)+" "+dayToKorean(before.get(Calendar.DAY_OF_WEEK))+ " "+before.get(Calendar.HOUR_OF_DAY)+" "+before.get(Calendar.MINUTE));

        Calendar after = Calendar.getInstance(timeZone);
        after.set(Calendar.DAY_OF_WEEK, dayToNum(day));
        after.set(Calendar.HOUR_OF_DAY, Integer.parseInt(after_hour));
        after.set(Calendar.MINUTE, Integer.parseInt(after_minute));
        //Log.d(TAG, "AFTER : "+dayToKorean(after.get(Calendar.DAY_OF_WEEK))+ " "+after.get(Calendar.HOUR_OF_DAY)+" "+after.get(Calendar.MINUTE));

        Calendar now = Calendar.getInstance(timeZone);
        //Log.d(TAG, "NOW : "+now.get(Calendar.DATE)+" "+dayToKorean(now.get(Calendar.DAY_OF_WEEK))+ " "+now.get(Calendar.HOUR_OF_DAY)+" "+now.get(Calendar.MINUTE));

        if( before.compareTo(now) <= 0 && after.compareTo(now) >= 0)
        {
            isPossible_button.setBackgroundColor(Color.RED);
        }
    }

    private void calculateSpanCells(String before_hour, String before_minute, String day, String after_hour, String after_minute, Lecture lecture) {
        //숫자로변경
        int before_hour_num = Integer.parseInt(before_hour);
        int after_hour_num = Integer.parseInt(after_hour);
        int before_minute_num = Integer.parseInt(before_minute);
        int after_minute_num = Integer.parseInt(after_minute);

        //토,일요일은 제외
        if(day.equals("토") || day.equals("일"))
            return ;

        //최소 시간 오전 9시00분
        if(before_hour_num < 9)
        {
            before_hour_num = 9;
            before_hour = "09";
        }
        //최대 시간 오후 10시00분
        if(after_hour_num >= 22 && after_minute_num > 0 )
        {
            after_hour_num = 22;
            after_minute_num = 0;
        }

        //시작시간 종료 시간 같은 경우는 skip
        if(before_hour_num != after_hour_num || before_minute_num != after_minute_num)
        {
            int hour_subtract = (after_hour_num - before_hour_num) * 2;//시간 차이
            int minute_subtract = (after_minute_num - before_minute_num) / 30;//분 차이

            int blocks = hour_subtract + minute_subtract;//span 해야될 row 개수

            spanCells(before_hour+":"+before_minute, day, blocks, lecture);
        }

    }

    //셀 병합
    private void spanCells(String row, String column, int blocks, Lecture lecture)
    {
        timeTableLayout.addSchedule(lecture.lecture_code, row, column, blocks, getColor(R.color.mint));
        timeTableLayout.findCell(row, column).setOnClickListener(view1 -> {
            String code = ((TextView)view1).getText().toString();

            Observable.create(emitter -> {
                emitter.onNext(viewModel.getLecture(code));
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> {

                        Lecture lecture1 = (Lecture) o;

                        View dialogView = getLayoutInflater().inflate(R.layout.lecture_dialog, null);

                        TextView name = dialogView.findViewById(R.id.name);
                        name.setText(lecture1.lecture_name);

                        TextView prof = dialogView.findViewById(R.id.professor);
                        prof.setText(lecture1.professor);

                        TextView room = dialogView.findViewById(R.id.room);
                        room.setText(lecture1.lecture_room);

                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setView(dialogView);
                        builder.setTitle(code).setMessage("강의 정보");
                        builder.setNegativeButton("확인", null);

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    });
        });
    }

    public int dayToNum(String day)
    {
        if (day.equals("일"))
            return 1;
        else if (day.equals("월"))
            return 2;
        else if (day.equals("화"))
            return 3;
        else if (day.equals("수"))
            return 4;
        else if (day.equals("목"))
            return 5;
        else if (day.equals("금"))
            return 6;
        else if (day.equals("토"))
            return 7;
        else
            return 0;
    }

    public String dayToKorean(int day) {

        if (day==2)
            return "월";
        else if (day==3)
            return "화";
        else if (day==4)
            return "수";
        else if (day==5)
            return "목";
        else if (day==6)
            return "금";
        else if (day==7)
            return "토";
        else if (day==1)
            return "일";
        else
            return "";
    }
    public String dayToEnglish(String day) {

        if (day.equals("월"))
            return "monday";
        else if (day.equals("화"))
            return "tuesday";
        else if (day.equals("수"))
            return "wednesday";
        else if (day.equals("목"))
            return "thursday";
        else if (day.equals("금"))
            return "friday";
        else if (day.equals("토"))
            return "saturday";
        else if (day.equals("일"))
            return "sunday";
        else
            return "";
    }
}
