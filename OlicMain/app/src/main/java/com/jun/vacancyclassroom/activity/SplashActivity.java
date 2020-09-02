package com.jun.vacancyclassroom.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;


import android.os.Bundle;
import android.view.View;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vacancyclassroom.R;
import com.jun.vacancyclassroom.database.DatabaseLibrary;
import com.jun.vacancyclassroom.Myapplication;

import java.util.Calendar;
import java.util.TimeZone;

public class SplashActivity extends AppCompatActivity {

    private String DBversion;//현재 학기를 db버전으로 사용
    Myapplication myapplication ;

    private DatabaseLibrary databaseLibrary;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        databaseLibrary = DatabaseLibrary.getInstance(null);

        myapplication = (Myapplication)getApplication();

        deleteBars();

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 100);
    }

    private void deleteBars() {
        //상,하단 바 제거
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    //DB버전 검사 후 동기화 시작 or 바로 MainActivity로 이동
    private class splashhandler implements Runnable
    {
        public void run(){

            TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
            Calendar now = Calendar.getInstance(timeZone);

            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH)+1;

            String semester="";

            if(1 <= month && month <= 2) //1~2월 겨울 계절학기
            {
                year -=1;
                semester = "W";
            }
            else if(3 <= month && month <=6)//3~6월 1학기
            {
                semester = "1";
            }
            else if(7 <= month && month <=8)//7~8월 여름 계절학기
            {
                semester = "S";
            }
            else if(9 <= month && month <=12)//9~12월 2학기
            {
                semester = "2";
            }
            DBversion = year+""+semester;//현재 학기를 db버전으로 사용


            myapplication.setCurrentSemester(DBversion);

            //현재 DB 버전 불러오기
            SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
            String text = sf.getString("DB","");//첫 사용인지 구분

            //첫사용 혹은 db업데이트 할때
            if(!text.equals(DBversion))
            {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if(networkInfo != null && networkInfo.isConnected()) {
                    databaseLibrary.deleteAllRecords();

                    //db업데이트
                    Intent intent = new Intent(SplashActivity.this, LoadingActivity.class);
                    intent.putExtra("semester",semester);
                    intent.putExtra("year",year);
                    startActivity(intent);
                }
                else
                {
                    // 연결되지않음
                    Toast.makeText(SplashActivity.this,"시간표 동기화를 위해서 인터넷을 연결후 시도해주세요.", Toast.LENGTH_SHORT).show();

                    finish();
                    return;
                }

            }
            else
            {
                //업데이트 필요없으면 바로 메인으로
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }

            SharedPreferences.Editor editor = sf.edit();
            editor.putString("DB",DBversion); // key, value를 이용하여 저장하는 형태

            //최종 커밋
            editor.commit();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }


}
