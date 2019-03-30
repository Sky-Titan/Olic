package com.example.vacancyclassroom;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vacancyclassroom.adapter.BookMarkAdapter;
import com.example.vacancyclassroom.adapter.SearchAdapter;
import com.example.vacancyclassroom.item.BookMarkItem;
import com.example.vacancyclassroom.item.SearchItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentB extends Fragment {

    BookMarkAdapter adapter;
    ListView listView;
    ArrayList<String> checkedlist=new ArrayList<>();
    MyDBHelper helper;

    View view;
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

        adapter=new BookMarkAdapter();
        listView=(ListView)view.findViewById(R.id.searchlist_b);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BookMarkItem item=(BookMarkItem)adapter.getItem(i);

                helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
                SQLiteDatabase db=helper.getReadableDatabase();
                db.execSQL("CREATE TABLE IF NOT EXISTS bookmarklist (classroom TEXT)");
                Cursor cursor=db.rawQuery("SELECT * FROM bookmarklist WHERE classroom ='"+item.getClassroom()+"';",null);

                //만약 즐겨찾기 db에 없다면 추가
                if(cursor.getCount()==0)
                {
                    db.execSQL("INSERT INTO bookmarklist (classroom) VALUES ('"+item.getClassroom()+"');");
                    listView.setItemChecked(i,true);
                    Toast.makeText(getContext(),"즐겨찾기에 추가했습니다.",Toast.LENGTH_SHORT).show();
                }
                else {//즐겨찾기에 있다면 삭제
                    db.execSQL("DELETE FROM bookmarklist WHERE classroom = '"+item.getClassroom()+"';");
                    listView.setItemChecked(i,false);
                    onResume();
                    Toast.makeText(getContext(),"즐겨찾기가 해제됐습니다.",Toast.LENGTH_SHORT).show();
                }
                cursor.close();
                if(db!=null)
                    db.close();
            }
        });

        loadList();
        return view;
    }

    public void loadList(){
        helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();

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

        for(int i=0;i<checkedlist.size();i++)
        {
            BookMarkItem item=(BookMarkItem)adapter.getItem(i);
            listView.setItemChecked(i,true);//전부 체크 시켜주기
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
    public boolean classification(String classroom_name) {

        boolean isPossible = true;

        helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();

        // String test_classroom = "IT융복합관(IT융복합공학관)-245";
        Cursor c = db.rawQuery("SELECT * FROM classroomlist where classroom='" + classroom_name + "';", null);
        /*
        현재 시간 불러오기
         */

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar now = Calendar.getInstance(timeZone);


        String day1=dayToKorean(now.get(Calendar.DAY_OF_WEEK));

        String hour=String.valueOf(now.get(Calendar.HOUR));

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
                            //이용불가
                        } else if (before_hour_num > hour_today_num && hour_today_num > after_hour_num)//현재시간이 밖에 있다면
                        {
                            //이용가능
                        } else if (before_hour_num == hour_today_num)//before 시간과 같은 경우
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
