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
import com.jun.vacancyclassroom.database.DatabaseLibrary;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

public class LoadingActivity extends AppCompatActivity {

    private String semester;
    private int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent intent = getIntent();
        year = intent.getIntExtra("year",0);
        semester = intent.getStringExtra("semester");

        doDatabaseUpdate();
    }

    public void doDatabaseUpdate(){
        ProgressDialog asyncDialog = new ProgressDialog(
                LoadingActivity.this, R.style.AppCompatAlertDialogStyle);

        new AsyncTask(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                asyncDialog.setMessage("시간표 동기화 중");
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
            protected Object doInBackground(Object[] objects) {
                //강의 정보 다운로드
                DatabaseLibrary databaseLibrary = DatabaseLibrary.getInstance(null);

                //exeception 발생 시
                if(!databaseLibrary.doUpdate(year, semester))
                {
                    SharedPreferences sf = getSharedPreferences("sFile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sf.edit();
                    editor.putString("DB",""); // exception 발생되서 업데이트 중간에 끊기면 버전 초기화시켜버림

                    //최종 커밋
                    editor.commit();

                    //로딩 액티비티 닫음
                    finish();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
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
