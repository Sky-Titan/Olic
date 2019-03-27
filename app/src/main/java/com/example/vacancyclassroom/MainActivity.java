package com.example.vacancyclassroom;

import android.app.ProgressDialog;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    private ArrayList<String> classrooms=new ArrayList<>();

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
               time2+=","+time;
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

        deleteTable();

        TextView textviewHtmlDocument = (TextView)findViewById(R.id.textView);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod()); //스크롤 가능한 텍스트뷰로 만들기
        String a="";

        insertData();

        SQLiteDatabase db = openOrCreateDatabase(
                "lecture_list.db",
                SQLiteDatabase.CREATE_IF_NECESSARY,
                null);
        Cursor c=db.rawQuery("SELECT * FROM classroomlist",null);
        while(c.moveToNext()){
            String classroom=c.getString(0);
            String time=c.getString(1);
            a+=classroom+" "+time+"\n";
        }


        if(db!=null)
            db.close();
        textviewHtmlDocument.setText(a);
    }
}
