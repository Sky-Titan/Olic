package com.jun.vacancyclassroom.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacancyclassroom.R;
import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

public class LoadingActivity extends AppCompatActivity {

    private String semester;
    private int year;
    //private DatabaseLibrary databaseLibrary;

    private MyDatabase database;

    private MyDAO dao;

    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //databaseLibrary = DatabaseLibrary.getInstance(null);

        database = MyDatabase.getInstance(getApplicationContext());
        dao = database.dao();

        Intent intent = getIntent();
        year = intent.getIntExtra("year",0);
        semester = intent.getStringExtra("semester");

        doDatabaseUpdate();
    }

    public void doDatabaseUpdate(){
        ProgressDialog asyncDialog = new ProgressDialog(
                LoadingActivity.this, R.style.AppCompatAlertDialogStyle);

        new AsyncTask<Void, Integer, Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                int total_page = database.getUrlListSize();

                asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                asyncDialog.setMessage("시간표 동기화 중 (0 / "+total_page+")");
                asyncDialog.setCancelable(false);
                asyncDialog.setCanceledOnTouchOutside(false);//터치해도 다이얼로그 안 사라짐

                // show dialog 상하단바 제거
                int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
                int newUiOptions = uiOptions;
                newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
                newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                newUiOptions ^= SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

                asyncDialog.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
                asyncDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                //강의 정보 다운로드
               // DatabaseLibrary databaseLibrary = DatabaseLibrary.getInstance(null);

                //exeception 발생 시
                if(!database.doUpdate(year, semester, (current_page) -> publishProgress(current_page)))
                {
                    SharedPreferences sf = getSharedPreferences("sFile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putString("DB_Room",""); // exception 발생되서 업데이트 중간에 끊기면 버전 초기화시켜버림

                    //최종 커밋
                    editor.commit();

                    //로딩 액티비티 닫음
                    finish();
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                int current_page = values[0];

                asyncDialog.setMessage("시간표 동기화 중 ("+current_page+" / "+database.getUrlListSize()+")");
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);

                asyncDialog.dismiss();
                asyncDialog.cancel(); //메모리 누수방지지

                //mainActivity로
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                //로딩 액티비티 닫음
                finish();
            }

        }.execute();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;

    }


}
