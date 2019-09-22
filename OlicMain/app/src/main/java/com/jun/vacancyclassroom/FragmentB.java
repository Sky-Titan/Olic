package com.jun.vacancyclassroom;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.adapter.BookMarkAdapter;
import com.jun.vacancyclassroom.item.BookMarkItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentB extends Fragment {

    LinearLayout layout_time;
    Button currentTime,visibility_time;
    BookMarkAdapter adapter;
    ListView listView;
    ArrayList<String> checkedlist=new ArrayList<>();
    MyDBHelper helper;
    private AdView mAdView;
    View view;
    TimePicker timePicker;
    NumberPicker dayPicker;
    BroadcastReceiver timeReceiver;

    public FragmentB() {
        // Required empty public constructor
    }
    @Override
    public void onResume(){
        super.onResume();
        adapter=new BookMarkAdapter();
        listView.setAdapter(adapter);
        checkedlist=new ArrayList<String>();
        loadList();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_b,container,false);

        timePicker = (TimePicker) view.findViewById(R.id.timePicker);//타임픽커 현재 시간으로 설정
        timePicker.setIs24HourView(true);

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar now = Calendar.getInstance(timeZone);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        timePicker.setHour(hour);
        timePicker.setMinute(minute);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                adapter=new BookMarkAdapter();
                listView.setAdapter(adapter);
                loadList();
            }
        });

        dayPicker = (NumberPicker) view.findViewById(R.id.dayPicker);
        dayPicker.setDisplayedValues(new String[]{"일","월","화","수","목","금","토"});
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(7);
        dayPicker.setValue(now.get(Calendar.DAY_OF_WEEK));
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                adapter=new BookMarkAdapter();
                listView.setAdapter(adapter);
                loadList();
            }
        });

        currentTime = (Button) view.findViewById(R.id.currentTime_btn);
        currentTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                Calendar now = Calendar.getInstance(timeZone);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minute = now.get(Calendar.MINUTE);
                timePicker.setHour(hour);
                timePicker.setMinute(minute);
                dayPicker.setValue(now.get(Calendar.DAY_OF_WEEK));

                adapter=new BookMarkAdapter();
                listView.setAdapter(adapter);
                loadList();
            }
        });

        layout_time = (LinearLayout) view.findViewById(R.id.linearLayout_time);

        visibility_time = (Button) view.findViewById(R.id.visible_btn);
        visibility_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_time.getVisibility() == View.VISIBLE)//끄기
                {
                    layout_time.setVisibility(View.GONE);
                    visibility_time.setText("시간 설정 보이기");
                    visibility_time.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.ic_arrow_drop_down_black_36dp),null);
                }
                else//켜기
                {
                    layout_time.setVisibility(View.VISIBLE);
                    visibility_time.setText("시간 설정 숨기기");
                    visibility_time.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.ic_arrow_drop_up_black_36dp),null);
                }
            }
        });

        System.out.println("Fragment B 출력");
        mAdView = (AdView) view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        adapter=new BookMarkAdapter();
        listView=(ListView)view.findViewById(R.id.searchlist_b);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookMarkItem item=(BookMarkItem)adapter.getItem(i);

                Intent intent = new Intent(getContext(),TimeTableActivity.class);
                intent.putExtra("classroom",item.getClassroom());
                intent.putExtra("isBuilding",false);
                startActivity(intent);
            }
        });

        loadList();
        return view;
    }

    public void loadList(){
        helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();

        checkedlist.clear();

        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarklist (classroom TEXT)");
        Cursor c = db.rawQuery("SELECT * FROM bookmarklist ;", null);
        while (c.moveToNext()){
            String classroom=c.getString(0);
            checkedlist.add(classroom);
        }
        c.close();
        int count=0;
        c = db.rawQuery("SELECT * FROM classroomlist ;", null);
        while(c.moveToNext()){
            String classroom=c.getString(0);
            String time=c.getString(1);
            for(int i=0;i<checkedlist.size();i++){
                if(checkedlist.get(i).equals(classroom))//즐겨찾기에 추가 되어잇음
                {
                    adapter.addItem(classroom,time,Color.RED);
                    count++;
                    break;
                }
            }
            if(count==checkedlist.size())
                break;
        }

        int day = dayPicker.getValue();
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        for(int i=0;i<checkedlist.size();i++)
        {
            BookMarkItem item=(BookMarkItem)adapter.getItem(i);
            listView.setItemChecked(i,true);//전부 체크 시켜주기
            if(classification(item.getClassroom(),day,hour,minute)==true)//이용가능시 초록색
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
    public boolean classification(String classroom_name,int day_n,int hour_n, int minute_n) {

        boolean isPossible = true;

        helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();

        // String test_classroom = "IT융복합관(IT융복합공학관)-245";
        Cursor c = db.rawQuery("SELECT * FROM classroomlist where classroom='" + classroom_name + "';", null);
        /*
        현재 시간 불러오기
         */

        //TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        //Calendar now = Calendar.getInstance(timeZone);

        String day1=dayToKorean(day_n);

        String hour=String.valueOf(hour_n);

        String minute=String.valueOf(minute_n);
        System.out.println(day1+hour+":"+minute);

        String day_today = day1;//현재요일
        String hour_today = hour;//현재시간
        String minute_today = minute;//현재분

        while (c.moveToNext()) {//지정된 classroom 에 왔다고 가정
            String classroom = c.getString(0);
            String time = c.getString(1);
            System.out.println("시간 : "+time);
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
                    System.out.println("aftertime : " + after_hour + " : " + after_minute + " i: " + i);

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
