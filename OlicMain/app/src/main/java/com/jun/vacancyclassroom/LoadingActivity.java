package com.jun.vacancyclassroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.vacancyclassroom.R;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

public class LoadingActivity extends AppCompatActivity {

    String semester;
    int year;
    public static Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Intent intent = getIntent();
        year = intent.getIntExtra("year",0);
        semester = intent.getStringExtra("semester");


        activity = LoadingActivity.this;
        //로딩창 실행
        CheckTypesTask task = new CheckTypesTask();
        task.execute();
    }

    public void loadingfinish()
    {
        finish();
    }
    private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                LoadingActivity.this, R.style.AppCompatAlertDialogStyle);


        @Override
        protected void onPreExecute() {
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
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            //강의 정보 다운로드
            UpdateDB updateDB = new UpdateDB(asyncDialog);
            updateDB.doUpdate(year,semester,getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            super.onPostExecute(result);



        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    public boolean onTouchEvent(MotionEvent event) {
        return false;

    }
}
