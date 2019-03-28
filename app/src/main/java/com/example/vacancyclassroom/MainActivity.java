package com.example.vacancyclassroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    private ArrayList<String> classrooms=new ArrayList<>();
    private ArrayList<String> timelist=new ArrayList<>();

    public void deleteTable(){
        SQLiteDatabase db = openOrCreateDatabase(
                "lecture_list.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);
        Cursor c= db.rawQuery("SELECT * FROM lecture",null);
        db.execSQL("DROP TABLE IF EXISTS classroomlist;");

        if(db!=null)
            db.close();
    }
    public void insertData(){
        SQLiteDatabase db = openOrCreateDatabase(
                "lecture_list.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);
        Cursor c= db.rawQuery("SELECT * FROM lecture",null);
        db.execSQL("CREATE TABLE IF NOT EXISTS classroomlist (classroom TEXT PRIMARY KEY,time TEXT)");
        System.out.print("크기:"+c.getCount());
        int i=0;
        while(c.moveToNext()){
            String classroom=c.getString(2);
            String time=c.getString(3);
            Cursor c2=db.rawQuery("SELECT * FROM classroomlist WHERE classroom='"+classroom+"'",null);
            int j=0;
            while(c2.moveToNext()){
                j++;
            }
            c2.moveToFirst();
            if(j==0)//classroom이 안들어가있으므로 바로 insert 문사용
            {
                db.execSQL("INSERT INTO classroomlist (classroom,time) values('"+classroom+"','"+time+"');");
            }
            else {//이미 들어가 있으므로 string에 추가만
               String time2=c2.getString(1);
               time2+=" "+time;
               db.execSQL("UPDATE classroomlist SET time='"+time2+"' WHERE classroom='"+classroom+"';");
            }
            i++;
        }
        if(db!=null)
            db.close();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //deleteTable();

        TextView textviewHtmlDocument = (TextView)findViewById(R.id.textView);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod()); //스크롤 가능한 텍스트뷰로 만들기
        String a="";
        String b="";
        String temp="";
      //  insertData();//데이터삽입

        SQLiteDatabase db = openOrCreateDatabase(
                "lecture_list.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);

        TimeZone timeZone=TimeZone.getTimeZone("Asia/Kabul");

        boolean isPossible=false;

        String test_classroom="IT융복합관(IT융복합공학관)-345";
        Cursor c=db.rawQuery("SELECT * FROM classroomlist where classroom='"+test_classroom+"';",null);
        String day_today="화";
        String hour_today="14";
        String minute_today="59";

        while(c.moveToNext()){//지정된 classroom 에 왔다고 가정
            String classroom=c.getString(0);
            String time=c.getString(1);
            StringTokenizer tokens=new StringTokenizer(time," ");
            String[] times=new String[tokens.countTokens()];
            String before_hour="",before_minute="";
            String day="";
            String after_hour="",after_minute="";

            for(int i=0;i<times.length;i++)
            {
                if(i==0){
                    times[i]=tokens.nextToken();//ex)화16:00
                    System.out.println("i : "+i+" " +times[i]);
                    day=times[i].substring(0,1);
                    before_hour=times[i].substring(1,3);
                    before_minute=times[i].substring(4,6);
                    System.out.println("beforetime : "+day+" "+before_hour+" : "+before_minute+" i: "+i);
                }
                else if(i%3==0)//새로운 시간대 beforetime이랑 요일구하기
                {
                    times[i]=tokens.nextToken();//ex)화16:00
                    System.out.println("i : "+i+" " +times[i]);
                    day=times[i].substring(0,1);
                    before_hour=times[i].substring(1,3);
                    before_minute=times[i].substring(4,6);
                    System.out.println("beforetime : "+day+" "+before_hour+" : "+before_minute+" i: "+i);
                }
                else if(i%3==2)//aftertime 구하기
                {
                    times[i]=tokens.nextToken();// ex)16:00
                    after_hour=times[i].substring(0,2);
                    after_minute=times[i].substring(3,5);
                    System.out.println("aftertime : "+after_hour+" : "+after_minute);

                    //숫자로변경
                    int before_hour_num=Integer.parseInt(before_hour);
                    int hour_today_num=Integer.parseInt(hour_today);
                    int after_hour_num=Integer.parseInt(after_hour);
                    int before_minute_num=Integer.parseInt(before_minute);
                    int minute_today_num=Integer.parseInt(minute_today);
                    int after_minute_num=Integer.parseInt(after_minute);

                    //현재 강의실 이용가능 한지 구분 시작
                    if(day.equals(day_today)){//요일이 같으면 그다음 단계
                        if(before_hour_num<hour_today_num && hour_today_num<after_hour_num)//현재시간이 사이에 있다면
                        {
                            //이용불가
                        }
                        else if(before_hour_num > hour_today_num && hour_today_num > after_hour_num)//현재시간이 밖에 있다면
                        {
                            //이용가능
                        }
                        else if(before_hour_num == hour_today_num)//before 시간과 같은 경우
                        {
                            //before 분과 비교
                            if(before_minute_num <= minute_today_num)//before minute보다 같거나 크면 이용불가
                            {
                                //이용불가
                                isPossible=false;
                            }
                            else{//작다면 이용가능
                                //이용가능
                                isPossible=true;
                            }
                        }
                        else if(after_hour_num == hour_today_num)//after 시간과 같은 경우
                        {
                            if(after_minute_num <= minute_today_num)//after minute보다 같거나 크면 이용불가
                            {
                                //이용불가
                                isPossible=false;
                            }
                            else{//작다면 이용가능
                                //이용가능
                                isPossible=true;
                            }
                        }
                    }
                    else{//요일다르면 그냥 다음단계로 넘어감

                        isPossible=false;
                    }
                }
                else if(i%3==1){//~
                    times[i]=tokens.nextToken();
                    System.out.println("i : "+i+" " +times[i]);
                }
                if(isPossible==true)
                    break ;
            }

    /*      long now = System.currentTimeMillis();
            Date date=new Date(now);
            DateFormat format=new SimpleDateFormat("EE");
            format.setTimeZone(timeZone);
            String day=format.format(date);
            format=new SimpleDateFormat("hh");
            String hour=format.format(date);
            format=new SimpleDateFormat("mm");
            String minute=format.format(date);

            hour = String.valueOf(Integer.parseInt(hour)+9);//hour
            day=dayToKorean(day);*/
        }


        if(db!=null)
            db.close();
        if(isPossible==true)
            textviewHtmlDocument.setText("이용가능");
        else
            textviewHtmlDocument.setText("이용불가");
    }

    public String dayToKorean(String day) {

        if (day.equals("Mon"))
            return "월";
        else if (day.equals("Tue"))
            return "화";
        else if (day.equals("Wed"))
            return "수";
        else if (day.equals("Thu"))
            return "목";
        else if (day.equals("Fri"))
            return "금";
        else if (day.equals("Sat"))
            return "토";
        else if (day.equals("Sun"))
            return "일";
        else
            return day;
    }
}
