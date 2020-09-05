package com.jun.vacancyclassroom.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.database.DatabaseLibrary;
import com.jun.vacancyclassroom.adapter.BookmarkListAdapter;

public class BuildingActivity extends AppCompatActivity {

    private BookmarkListAdapter adapter;
    private ListView listView;
    private AdView mAdView;


    private String buildingName;
    private TextView buildingName_textview;

    private DatabaseLibrary databaseLibrary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

 /*       databaseLibrary = DatabaseLibrary.getInstance(null);

        mAdView = (AdView) findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        adapter=new BookmarkListAdapter();
        listView=(ListView)findViewById(R.id.classroomlist_buildingActivity);
        listView.setAdapter(adapter);

        //인텐트 받아오고 건물 이름 지정
        buildingName_textview = (TextView) findViewById(R.id.buildingName_buildingActivity);
        Intent intent = getIntent();
        buildingName = intent.getExtras().getString("buildingName");
        buildingName_textview.setText(buildingName);


        listView.setOnItemClickListener((adapterView, view, i, l) -> {
                BookMarkedRoom item=(BookMarkedRoom)adapter.getItem(i);

                Intent intent2 = new Intent(BuildingActivity.this,TimeTableActivity.class);
                intent2.putExtra("classroom",item.getLecture_room());
                intent2.putExtra("isBuilding",true);
                startActivity(intent2);
        });

        loadList();*/
    }
   /* public void loadList(){

        new AsyncTask<Void, Void, Cursor>()
        {
            @Override
            protected Cursor doInBackground(Void... voids) {
                return databaseLibrary.selectLectureRoomList();
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);

                while(cursor.moveToNext()){
                    String classroom = cursor.getString(0);
                    String time=cursor.getString(1);

                    //강의실 이름에 빌딩 이름 포함시 추가
                    if(classroom.contains(buildingName)) {
                        adapter.addItem(classroom, time, Color.RED);
                    }
                }

                for(int i=0;i<adapter.getCount();i++)
                {
                    BookMarkedRoom item=(BookMarkedRoom)adapter.getItem(i);
                    if(classification(item.getLecture_room())==true)//이용가능시 초록색
                    {
                        item.setButton_color(Color.GREEN);
                    }
                    else {//이용불가시 빨간색
                        item.setButton_color(Color.RED);
                    }
                }

                cursor.close();
            }

        }.execute();

    }

    //해당 교실이 현재 시간에 이용가능한지 판단
    private boolean classification(String lectureRoom)
    {
        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {

                boolean isPossible = true;

                Cursor c = databaseLibrary.selectLectureRoomList(lectureRoom);

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

                            day = times[i].substring(0, 1);
                            before_hour = times[i].substring(1, 3);
                            before_minute = times[i].substring(4, 6);
                        }
                        else if (i % 3 == 2)//aftertime(종료시간) 구하기
                        {
                            times[i] = tokens.nextToken();// ex)16:00
                            after_hour = times[i].substring(0, 2);
                            after_minute = times[i].substring(3, 5);

                            //숫자로변경
                            int before_hour_num = Integer.parseInt(before_hour);
                            int hour_today_num = Integer.parseInt(hour_today);
                            int after_hour_num = Integer.parseInt(after_hour);
                            int before_minute_num = Integer.parseInt(before_minute);
                            int minute_today_num = Integer.parseInt(minute_today);
                            int after_minute_num = Integer.parseInt(after_minute);

                            //현재 강의실 이용가능 한지 구분 시작
                            //요일이 같으면 그다음 단계
                            if (day.equals(day_today))
                            {
                                if (before_hour_num < hour_today_num && hour_today_num < after_hour_num)//현재시간이 사이에 있다면 이용불가
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
                                    else
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
        }.execute();

        boolean isPossible = true;

        try {
            isPossible= asyncTask.get();
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
    }*/
}
