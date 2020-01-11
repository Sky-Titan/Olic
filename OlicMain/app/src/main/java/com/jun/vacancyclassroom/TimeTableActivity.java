package com.jun.vacancyclassroom;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class TimeTableActivity extends AppCompatActivity {

    ArrayList<String> arrayList_timetable=new ArrayList<>();
    MyDBHelper helper;

    private AdView mAdView;
    Button bookmark;
    Button isPossibleButton;

    Boolean isBookMarked;
    TextView classroom_textview;
    String classroom;

    View view;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().from(this).inflate(R.layout.activity_time_table,null);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getColor(R.color.statusBar_color));
        }

        Myapplication myapplication = (Myapplication)getApplication();
        setTitle(myapplication.getCurrentSemester());
        setContentView(view);

        Intent intent = getIntent();
        classroom = intent.getExtras().getString("classroom");

        //강의실 이름 설정
        classroom_textview = (TextView) findViewById(R.id.classroom_name_timetable);
        classroom_textview.setText(classroom);

        //애드뷰 설정
        mAdView = (AdView) findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);


        helper=new MyDBHelper(this,"lecture_list.db",null,1);
        db=helper.getReadableDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarklist (classroom TEXT)");
        Cursor c = db.rawQuery("SELECT * FROM bookmarklist WHERE classroom ='"+classroom+"';", null);

        //즐겨찾기 해제
        bookmark = (Button)findViewById(R.id.bookmarkButton_timetable);
        if(c.getCount()==0)
        {
            isBookMarked = false;
            bookmark.setText("즐겨찾기 추가");
        }
        else
        {
            isBookMarked = true;
            bookmark.setText("즐겨찾기 해제");
        }
        c.close();
        if(db!=null)
            db.close();
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                helper = new MyDBHelper(getApplicationContext(), "lecture_list.db", null, 1);
                SQLiteDatabase db = helper.getReadableDatabase();
                if(isBookMarked == true) {

                    db.execSQL("DELETE FROM bookmarklist WHERE classroom = '" + classroom + "';");
                    Toast.makeText(getApplicationContext(), "즐겨찾기가 해제됐습니다.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    db.execSQL("INSERT INTO bookmarklist (classroom) VALUES ('"+classroom+"');");
                    Toast.makeText(getApplicationContext(), "즐겨찾기에 추가됐습니다.", Toast.LENGTH_SHORT).show();
                }
                if(db!=null)
                    db.close();
                finish();
            }
        });
        //가능여부 색깔 표시 버튼
        isPossibleButton = (Button)findViewById(R.id.isPossibleButton_timetable);
        //즐겨찾기 목록 불러오기
        helper=new MyDBHelper(getApplicationContext(),"lecture_list.db",null,1);
        db=helper.getReadableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarklist (classroom TEXT)");
        Cursor cursor=db.rawQuery("SELECT * FROM bookmarklist;",null);

        while (cursor.moveToNext())
        {
            cursor.getString(0);
            arrayList_timetable.add(cursor.getString(0));
        }
        cursor.close();
        if(db!=null)
            db.close();



        makeTimeTable();
        //db닫기

    }
    public void makeTimeTable()
    {

        helper=new MyDBHelper(getApplicationContext(),"lecture_list.db",null,1);
        db=helper.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM classroomlist WHERE classroom = '"+classroom+"';",null);
        String time="";
        while (cursor.moveToNext())
            time = cursor.getString(1);

        cursor.close();
        if(db!=null)
            db.close();
        //버튼 색깔 판정
        if(classification(classroom)==true)//이용가능시 초록색
        {
            isPossibleButton.setBackgroundColor(Color.GREEN);
        }
        else {//이용불가시 빨간색
            isPossibleButton.setBackgroundColor(Color.RED);
        }

        /* TODO : 시간표표시*/
        StringTokenizer tokens = new StringTokenizer(time, " ");
        String[] times = new String[tokens.countTokens()];
        String before_hour = "", before_minute = "";
        String day = "";
        String after_hour = "", after_minute = "";
        for (int i = 0; i < times.length; i++) {
            if (i == 0) {
                times[i] = tokens.nextToken();//ex)화16:00
                System.out.println("i : " + i + " " + times[i]);
                day = times[i].substring(0, 1);
                before_hour = times[i].substring(1, 3);
                before_minute = times[i].substring(4, 6);
                System.out.println("beforetime : " + day + " " + before_hour + " : " + before_minute + " i: " + i);
            } else if (i % 3 == 0)//새로운 시간대 beforetime이랑 요일구하기
            {
                times[i] = tokens.nextToken();//ex)화16:00
                System.out.println("i : " + i + " " + times[i]);
                day = times[i].substring(0, 1);
                before_hour = times[i].substring(1, 3);
                before_minute = times[i].substring(4, 6);
                System.out.println("beforetime : " + day + " " + before_hour + " : " + before_minute + " i: " + i);
            } else if (i % 3 == 2)//aftertime 구하기
            {
                times[i] = tokens.nextToken();// ex)16:00
                after_hour = times[i].substring(0, 2);
                after_minute = times[i].substring(3, 5);
                System.out.println("aftertime : " + after_hour + " : " + after_minute);

                //숫자로변경
                int before_hour_num = Integer.parseInt(before_hour);
                int after_hour_num = Integer.parseInt(after_hour);
                int before_minute_num = Integer.parseInt(before_minute);
                int after_minute_num = Integer.parseInt(after_minute);

                //토,일요일은 제외
                if(day.equals("토") || day.equals("일"))
                    continue;

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
                if(before_hour_num == after_hour_num && before_minute_num == after_minute_num)
                {

                }
                else {
                    int hour_subtract = (after_hour_num - before_hour_num) * 2;//시간 차이
                    int minute_subtract = (after_minute_num - before_minute_num) / 30;//분 차이

                    int result = hour_subtract + minute_subtract;//span 해야될 row 개수
                    int row = (before_hour_num - 9) * 2 + (before_minute_num == 30 ?  2 : 1)   ;//행번호

                    //병합된 셀들 제거 작업 result수만큼 제거
                    for(int j = 0;j < result - 1; j++)
                    {
                        String delete_cell_hour = before_minute_num == 30 ? ( before_hour_num + 1 < 10 ? "0" + String.valueOf(before_hour_num + 1) : String.valueOf(before_hour_num + 1)) : (before_hour_num < 10 ? "0" + String.valueOf(before_hour_num) : String.valueOf(before_hour_num) );
                        before_hour_num = before_minute_num == 30 ? before_hour_num + 1 : before_hour_num;
                        String delete_cell_minute = before_minute_num == 30 ? "00" : "30";
                        before_minute_num = before_minute_num == 30 ? 0 : 30;
                        String delete_cell_name = dayToEnglish(day)+delete_cell_hour+delete_cell_minute;
                        TextView deleteCell = (TextView) view.findViewWithTag(delete_cell_name);

                        //System.out.println("delete : " + delete_cell_name);
                        GridLayout gridLayout2 = (GridLayout)view.findViewById(R.id.gridlayout_timetable);
                        gridLayout2.removeView(deleteCell);
                    }

                    String IDofSpanCell = dayToEnglish(day) + before_hour + before_minute;//span해야할 cell의 id
                    //System.out.println("Spancell id : "+IDofSpanCell);
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
            } else if (i % 3 == 1) {//~
                times[i] = tokens.nextToken();
            }

        }
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
    public boolean classification(String classroom_name) {

        boolean isPossible = true;

        helper=new MyDBHelper(getApplicationContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();

        // String test_classroom = "IT융복합관(IT융복합공학관)-245";
        Cursor c = db.rawQuery("SELECT * FROM classroomlist where classroom='" + classroom_name + "';", null);
        /*
        현재 시간 불러오기
         */

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar now = Calendar.getInstance(timeZone);

        String day1=dayToKorean(now.get(Calendar.DAY_OF_WEEK));

        String hour=String.valueOf(now.get(Calendar.HOUR_OF_DAY));

        String minute=String.valueOf(now.get(Calendar.MINUTE));
        System.out.println("현재시각 : "+day1+hour+":"+minute);

        String day_today = day1;//현재요일
        String hour_today = hour;//현재시간
        String minute_today = minute;//현재분

        while (c.moveToNext()) {//지정된 classroom 에 왔다고 가정
            String classroom = c.getString(0);
            String time = c.getString(1);
            StringTokenizer tokens = new StringTokenizer(time, " ");
            String[] times = new String[tokens.countTokens()];
            String before_hour = "", before_minute = "";
            String day = "";
            String after_hour = "", after_minute = "";

            for (int i = 0; i < times.length; i++) {
                if (i == 0) {
                    times[i] = tokens.nextToken();//ex)화16:00
                    System.out.println("i : " + i + " " + times[i]);
                    day = times[i].substring(0, 1);
                    before_hour = times[i].substring(1, 3);
                    before_minute = times[i].substring(4, 6);
                    System.out.println("beforetime : " + day + " " + before_hour + " : " + before_minute + " i: " + i);
                } else if (i % 3 == 0)//새로운 시간대 beforetime이랑 요일구하기
                {
                    times[i] = tokens.nextToken();//ex)화16:00
                    System.out.println("i : " + i + " " + times[i]);
                    day = times[i].substring(0, 1);
                    before_hour = times[i].substring(1, 3);
                    before_minute = times[i].substring(4, 6);
                    System.out.println("beforetime : " + day + " " + before_hour + " : " + before_minute + " i: " + i);
                } else if (i % 3 == 2)//aftertime 구하기
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
                    if (day.equals(day_today)) {//요일이 같으면 그다음 단계
                        if (before_hour_num < hour_today_num && hour_today_num < after_hour_num)//현재시간이 사이에 있다면
                        {
                            isPossible = false;
                            //이용불가
                        } else if (before_hour_num > hour_today_num && hour_today_num > after_hour_num)//현재시간이 밖에 있다면
                        {
                            //이용가능
                            isPossible = true;
                        }
                        else if (before_hour_num == hour_today_num)//before 시간과 같은 경우
                        {
                            //before 분과 비교
                            if (before_minute_num <= minute_today_num)//before minute보다 같거나 크면 이용불가
                            {
                                //이용불가
                                isPossible = false;
                            } else {//작다면 이용가능
                                //이용가능
                                isPossible = true;
                            }
                        } else if (after_hour_num == hour_today_num)//after 시간과 같은 경우
                        {
                            if (after_minute_num >= minute_today_num)//after minute보다 같거나 작으면 이용불가
                            {
                                //이용불가
                                isPossible = false;
                            } else {//크다면 이용가능
                                //이용가능
                                isPossible = true;
                            }
                        }
                    } else {//요일다르면 그냥 다음단계로 넘어감

                        isPossible = true;
                    }
                } else if (i % 3 == 1) {//~
                    times[i] = tokens.nextToken();
                    System.out.println("i : " + i + " " + times[i]);
                }
                if (isPossible == false)//하나라도 이용불가라면 종료
                {
                    c.close();
                    return isPossible;
                }
            }
        }
        c.close();
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
