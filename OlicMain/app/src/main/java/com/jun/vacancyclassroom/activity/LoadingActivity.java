package com.jun.vacancyclassroom.activity;

import android.app.AlertDialog;
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
import com.jun.vacancyclassroom.interfaces.UpdateCallback;

import java.io.IOException;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

public class LoadingActivity extends AppCompatActivity {

    private String semester;
    private int year;

    private MyDatabase database;

    private MyDAO dao;

    private static final String TAG = "LoadingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

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

        new AsyncTask<Void, Object, Boolean>(){

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
            protected Boolean doInBackground(Void... voids) {
                //강의 정보 다운로드

                try
                {
                    database.doUpdate(year, semester, (current_page) -> publishProgress(current_page));
                }
                catch (IllegalStateException e)
                {
                    e.printStackTrace();

                    rollBackDB();

                    AlertDialog.Builder builder = new AlertDialog.Builder(LoadingActivity.this);
                    builder.setTitle("데이터베이스 충돌 발생")
                            .setMessage("설정 → 애플리케이션 → 'Olic' → 저장공간 → 데이터 삭제를 진행하신 후 다시 실행해주세요.")
                            .setNegativeButton("확인",(dialogInterface, i) -> {
                                finish();
                            });

                    publishProgress(builder);
                    return false;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    rollBackDB();

                    return false;
                }


                return true;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);

                if(values[0].getClass() == Integer.class)
                {
                    int current_page = (Integer) values[0];

                    asyncDialog.setMessage("시간표 동기화 중 ("+current_page+" / "+database.getUrlListSize()+")");
                }
                else
                {
                    AlertDialog.Builder builder = (AlertDialog.Builder) values[0];
                    builder.show();
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);

                //정상 종료 시
                if(result)
                {
                    asyncDialog.dismiss();
                    asyncDialog.cancel(); //메모리 누수방지지

                    //mainActivity로
                    Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    //로딩 액티비티 닫음
                    finish();
                }

            }

        }.execute();
    }

    private void rollBackDB()
    {
        SharedPreferences sf = getSharedPreferences("sFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sf.edit();
        editor.putString("DB_Room",""); // exception 발생되서 업데이트 중간에 끊기면 버전 초기화시켜버림

        //최종 커밋
        editor.commit();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;

    }


}
