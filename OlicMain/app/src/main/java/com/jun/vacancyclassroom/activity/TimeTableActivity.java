package com.jun.vacancyclassroom.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.database.DatabaseLibrary;
import com.jun.vacancyclassroom.database.MyDBHelper;
import com.jun.vacancyclassroom.Myapplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class TimeTableActivity extends AppCompatActivity {

    private AdView mAdView;
    private Button bookmark;
    private Button isPossibleButton;

    private Boolean isBookMarked;
    private TextView classroom_textview;
    private String classroom;

    private View view;

    private DatabaseLibrary databaseLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().from(this).inflate(R.layout.activity_time_table,null);

        databaseLibrary = DatabaseLibrary.getInstance(null);

        Myapplication myapplication = (Myapplication)getApplication();
        setTitle(myapplication.getCurrentSemester());
        setContentView(view);

        //표시할 강의실 이름 가져옴
        Intent intent = getIntent();
        classroom = intent.getExtras().getString("classroom");

        //강의실 이름 설정
        classroom_textview = (TextView) findViewById(R.id.classroom_name_timetable);
        classroom_textview.setText(classroom);

        //애드뷰 설정
        setAdView();

        //북마크 상태 설정
        setBookMarkStatus();

        makeTimeTable();
    }

    private void setBookMarkStatus() {

        new AsyncTask<Void, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(Void... voids) {
                Cursor c = databaseLibrary.selectBookmarkList(classroom);

                if(c.getCount()==0)
                {
                    c.close();
                    return false;
                }
                else
                {
                    c.close();
                    return true;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                //즐겨찾기 해제
                bookmark = (Button)findViewById(R.id.bookmarkButton_timetable);

                if(!aBoolean)
                {
                    isBookMarked = false;
                    bookmark.setText("즐겨찾기 추가");
                }
                else
                {
                    isBookMarked = true;
                    bookmark.setText("즐겨찾기 해제");
                }

                //즐겨찾기 버튼 클릭 리스너
                bookmark.setOnClickListener(view -> {
                    if(isBookMarked == true) {

                        new Thread(() -> databaseLibrary.deleteBookmarkList(classroom)).start();
                        Toast.makeText(getApplicationContext(), "즐겨찾기가 해제됐습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        new Thread(() -> databaseLibrary.insertBookmarkList(classroom)).start();
                        Toast.makeText(getApplicationContext(), "즐겨찾기에 추가됐습니다.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                });
            }


        }.execute();

    }


    private void setAdView() {
        //애드뷰 설정
        mAdView = (AdView) findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    public void makeTimeTable()
    {
        new AsyncTask<Void, Void, Cursor>()
        {
            @Override
            protected Cursor doInBackground(Void... voids) {
                return databaseLibrary.selectLectureRoomList(classroom);
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);

                String time="";

                while (cursor.moveToNext())
                    time = cursor.getString(1);

                cursor.close();

                //이용가능상태 색깔 설정
                setPossibleBtn();

                // 시간표 표시
                StringTokenizer tokens = new StringTokenizer(time, " ");
                String[] times = new String[tokens.countTokens()];
                String before_hour = "", before_minute = "";
                String day = "";
                String after_hour = "", after_minute = "";

                for (int i = 0; i < times.length; i++)
                {
                    if (i == 0)
                    {
                        times[i] = tokens.nextToken();//ex)화16:00

                        day = times[i].substring(0, 1);
                        before_hour = times[i].substring(1, 3);
                        before_minute = times[i].substring(4, 6);

                    }
                    else if (i % 3 == 0)//새로운 시간대 beforetime(시작시간)이랑 요일구하기
                    {
                        times[i] = tokens.nextToken();//ex)화16:00

                        //요일
                        day = times[i].substring(0, 1);

                        //시작 시간
                        before_hour = times[i].substring(1, 3);
                        before_minute = times[i].substring(4, 6);
                    }
                    else if (i % 3 == 2)//aftertime 구하기 (종료시간)
                    {
                        times[i] = tokens.nextToken();// ex)16:00

                        //종료시간
                        after_hour = times[i].substring(0, 2);
                        after_minute = times[i].substring(3, 5);


                        before_hour = calculateSpanCells(before_hour, before_minute, day, after_hour, after_minute);

                    }
                    else if (i % 3 == 1)
                    {//~
                        times[i] = tokens.nextToken();
                    }

                }
            }
        }.execute();

    }

    private String calculateSpanCells(String before_hour, String before_minute, String day, String after_hour, String after_minute) {
        //숫자로변경
        int before_hour_num = Integer.parseInt(before_hour);
        int after_hour_num = Integer.parseInt(after_hour);
        int before_minute_num = Integer.parseInt(before_minute);
        int after_minute_num = Integer.parseInt(after_minute);

        //토,일요일은 제외
        if(day.equals("토") || day.equals("일"))
            return before_hour;

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
            after_hour = "22";
            after_minute_num = 0;
            after_minute = "00";
        }

        //시작시간 종료 시간 같은 경우는 skip
        if(before_hour_num != after_hour_num || before_minute_num != after_minute_num)
        {
            int hour_subtract = (after_hour_num - before_hour_num) * 2;//시간 차이
            int minute_subtract = (after_minute_num - before_minute_num) / 30;//분 차이

            int result = hour_subtract + minute_subtract;//span 해야될 row 개수
            int row = (before_hour_num - 9) * 2 + (before_minute_num == 30 ?  2 : 1)   ;//행번호


            spanCells(before_hour, before_minute, day, before_hour_num, before_minute_num, result, row);

        }
        return before_hour;
    }

    //셀 병합
    private void spanCells(String before_hour, String before_minute, String day, int before_hour_num, int before_minute_num, int result, int row) {

        deleteCellsBeforeSpan(day, before_hour_num, before_minute_num, result);

        String IDofSpanCell = dayToEnglish(day) + before_hour + before_minute;//span해야할 cell의 id

        TextView spanCell = (TextView) view.findViewWithTag(IDofSpanCell);//span cell

        GridLayout.LayoutParams layoutParams = (GridLayout.LayoutParams)spanCell.getLayoutParams();
        layoutParams.columnSpec = GridLayout.spec(dayToNum(day));
        layoutParams.rowSpec = GridLayout.spec(row,result);//병합할 셀 수 정함

        spanCell.setLayoutParams(layoutParams);//적용

        layoutParams.setGravity(Gravity.FILL);//gravity 설정

        spanCell.setLayoutParams(layoutParams);//다시 적용

        spanCell.setText("     ");//빈 텍스트 적용
        /*TODO :기기마다 버그 발생할 확률 높음!!!!!! */

        spanCell.setBackground(getResources().getDrawable(R.drawable.fill_cell));
    }

    //병합된 셀들 제거 작업 result수만큼 제거
    private void deleteCellsBeforeSpan(String day, int before_hour_num, int before_minute_num, int result) {

        for(int j = 0;j < result - 1; j++)
        {
            String delete_cell_hour = before_minute_num == 30 ? ( before_hour_num + 1 < 10 ? "0" + String.valueOf(before_hour_num + 1) : String.valueOf(before_hour_num + 1)) : (before_hour_num < 10 ? "0" + String.valueOf(before_hour_num) : String.valueOf(before_hour_num) );
            before_hour_num = before_minute_num == 30 ? before_hour_num + 1 : before_hour_num;

            String delete_cell_minute = before_minute_num == 30 ? "00" : "30";
            before_minute_num = before_minute_num == 30 ? 0 : 30;

            String delete_cell_name = dayToEnglish(day)+delete_cell_hour+delete_cell_minute;
            TextView deleteCell = (TextView) view.findViewWithTag(delete_cell_name);

            GridLayout gridLayout2 = (GridLayout)view.findViewById(R.id.gridlayout_timetable);
            gridLayout2.removeView(deleteCell);
        }
    }

    private void setPossibleBtn() {
        //가능여부 색깔 표시 버튼
        isPossibleButton = (Button)findViewById(R.id.isPossibleButton_timetable);

        //버튼 색깔 판정
        if(classification(classroom))//이용가능시 초록색
            isPossibleButton.setBackgroundColor(Color.GREEN);
        else//이용불가시 빨간색
            isPossibleButton.setBackgroundColor(Color.RED);
    }

    public int dayToNum(String day)
    {
        if (day.equals("월"))
            return 1;
        else if (day.equals("화"))
            return 2;
        else if (day.equals("수"))
            return 3;
        else if (day.equals("목"))
            return 4;
        else if (day.equals("금"))
            return 5;
        else if (day.equals("토"))
            return 6;
        else if (day.equals("일"))
            return 7;
        else
            return 0;
    }

    //현재 시간 기준 이용가능 여부 판단
    public boolean classification(String lectureRoom) {

        boolean isPossible = true;

        try {
            isPossible = new AsyncTask<Void, Void, Boolean>()
            {

                @Override
                protected Boolean doInBackground(Void... voids) {
                    Cursor c = databaseLibrary.selectLectureRoomList(lectureRoom);
        /*
        현재 시간 불러오기
         */
                    boolean isPossible = true;

                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                    Calendar now = Calendar.getInstance(timeZone);

                    String day1=dayToKorean(now.get(Calendar.DAY_OF_WEEK));

                    String hour=String.valueOf(now.get(Calendar.HOUR_OF_DAY));

                    String minute=String.valueOf(now.get(Calendar.MINUTE));
                    System.out.println("현재시각 : "+day1+hour+":"+minute);

                    String day_today = day1;//현재요일
                    String hour_today = hour;//현재시간
                    String minute_today = minute;//현재분

                    while (c.moveToNext())
                    {   //지정된 classroom 에 왔다고 가정

                        String classroom = c.getString(0);
                        String time = c.getString(1);

                        StringTokenizer tokens = new StringTokenizer(time, " ");
                        String[] times = new String[tokens.countTokens()];

                        String before_hour = "", before_minute = "";
                        String day = "";
                        String after_hour = "", after_minute = "";

                        for (int i = 0; i < times.length; i++)
                        {
                            if (i == 0)
                            {
                                times[i] = tokens.nextToken();//ex)화16:00

                                day = times[i].substring(0, 1);
                                before_hour = times[i].substring(1, 3);
                                before_minute = times[i].substring(4, 6);
                            }
                            else if (i % 3 == 0)//새로운 시간대 beforetime이랑 요일구하기
                            {
                                times[i] = tokens.nextToken();//ex)화16:00

                                day = times[i].substring(0, 1);
                                before_hour = times[i].substring(1, 3);
                                before_minute = times[i].substring(4, 6);
                            }
                            else if (i % 3 == 2)//aftertime 구하기
                            {
                                times[i] = tokens.nextToken();// ex)16:00
                                after_hour = times[i].substring(0, 2);
                                after_minute = times[i].substring(3, 5);
                                System.out.println("aftertime : " + after_hour + " : " + after_minute);

                                //숫자로변경
                                int before_hour_num = Integer.parseInt(before_hour);
                                int hour_today_num = Integer.parseInt(hour_today);
                                int after_hour_num = Integer.parseInt(after_hour);
                                int before_minute_num = Integer.parseInt(before_minute);
                                int minute_today_num = Integer.parseInt(minute_today);
                                int after_minute_num = Integer.parseInt(after_minute);

                                //현재 강의실 이용가능 한지 구분 시작
                                if (day.equals(day_today))
                                {
                                    //요일이 같으면 그다음 단계
                                    if (before_hour_num < hour_today_num && hour_today_num < after_hour_num)//현재시간이 사이에 있다면 이용 불가
                                        isPossible = false;
                                    else if (before_hour_num > hour_today_num && hour_today_num > after_hour_num)//현재시간이 밖에 있다면 이용가능
                                        isPossible = true;
                                    else if (before_hour_num == hour_today_num)//before 시간과 같은 경우
                                    {
                                        //before 분과 비교
                                        if (before_minute_num <= minute_today_num)//before minute보다 같거나 크면 이용불가
                                            isPossible = false;
                                            //작다면 이용가능
                                        else
                                            isPossible = true;

                                    }
                                    else if (after_hour_num == hour_today_num)//after 시간과 같은 경우
                                    {
                                        if (after_minute_num >= minute_today_num)//after minute보다 같거나 작으면 이용불가
                                            isPossible = false;
                                        else//크다면 이용가능
                                            isPossible = true;
                                    }
                                }
                                else//요일다르면 그냥 다음단계로 넘어감
                                    isPossible = true;
                            }
                            else if (i % 3 == 1)
                            {//~
                                times[i] = tokens.nextToken();
                            }

                            //하나라도 이용불가라면 종료
                            if (isPossible == false)
                            {
                                c.close();
                                return isPossible;
                            }
                        }
                    }
                    c.close();

                    return isPossible;
                }
            }.execute().get();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return isPossible;
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
