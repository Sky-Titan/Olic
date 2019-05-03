package com.jun.vacancyclassroom;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.adapter.BookMarkAdapter;
import com.jun.vacancyclassroom.adapter.BuildingSearchAdapter;
import com.jun.vacancyclassroom.item.BookMarkItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class BuildingActivity extends AppCompatActivity {

    BookMarkAdapter adapter;
    ListView listView;
    private AdView mAdView;
    MyDBHelper helper;

    String buildingName;
    TextView buildingName_textview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        mAdView = (AdView) findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        adapter=new BookMarkAdapter();
        listView=(ListView)findViewById(R.id.classroomlist_buildingActivity);
        listView.setAdapter(adapter);

        //인텐트 받아오고 건물 이름 지정
        buildingName_textview = (TextView) findViewById(R.id.buildingName_buildingActivity);
        Intent intent = getIntent();
        buildingName = intent.getExtras().getString("buildingName");
        buildingName_textview.setText(buildingName);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookMarkItem item=(BookMarkItem)adapter.getItem(i);

                Intent intent = new Intent(BuildingActivity.this,TimeTableActivity.class);
                intent.putExtra("classroom",item.getClassroom());
                intent.putExtra("isBuilding",true);
                startActivity(intent);
            }
        });

        loadList();
    }
    public void loadList(){
        helper=new MyDBHelper(this,"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();

        Cursor c;
        int count=0;
        c = db.rawQuery("SELECT * FROM classroomlist ;", null);
        while(c.moveToNext()){
            String classroom=c.getString(0);
            String time=c.getString(1);

            //강의실 이름에 빌딩 이름 포함시 추가
            if(classroom.contains(buildingName)) {
                adapter.addItem(classroom, time, Color.RED);
                count++;
            }
        }

        for(int i=0;i<adapter.getCount();i++)
        {
            BookMarkItem item=(BookMarkItem)adapter.getItem(i);
            if(classification(item.getClassroom())==true)//이용가능시 초록색
            {
                item.setButton_color(Color.GREEN);
            }
            else {//이용불가시 빨간색
                item.setButton_color(Color.RED);
            }
        }
        c.close();
        if(db!=null)
            db.close();

    }
    //해당 교실이 현재 시간에 이용가능한지 판단
    public boolean classification(String classroom_name) {

        boolean isPossible = true;

        helper=new MyDBHelper(this,"lecture_list.db",null,1);
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
        System.out.println(day1+hour+":"+minute);

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
}
