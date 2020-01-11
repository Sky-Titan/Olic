package com.jun.vacancyclassroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vacancyclassroom.R;

import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.TimeZone;

public class SplashActivity extends AppCompatActivity {

    private String DBversion;//현재 학기를 db버전으로 사용
    Myapplication myapplication ;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        myapplication = (Myapplication)getApplication();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(getColor(R.color.statusBar_color));
        }

        //상,하단 바 제거
        int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

        //애니메이션 효과 적용
        TextView lovenu=(TextView)findViewById(R.id.olic_splash);
        TextView appment=(TextView)findViewById(R.id.app_ment_splash);
        final ImageView heart=(ImageView)findViewById(R.id.roomimg_splash);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 100);




    /*    View view = getLayoutInflater().from(this).inflate(R.layout.activity_splash,null);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        final Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.blink);
        lovenu.startAnimation(animation);
        appment.startAnimation(animation);


        //블링크끝나면 로그인창띄우기
        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                heart.setImageResource(R.drawable.baseline_meeting_room_white_48);
                heart.startAnimation(animation1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/

    }
    @Override
    protected void onStop() {
        super.onStop();

        // Activity가 종료되기 전에 저장한다.
        //SharedPreferences를 sFile이름, 기본모드로 설정
        SharedPreferences sharedPreferences = getSharedPreferences("sFile",MODE_PRIVATE);

        //저장을 하기위해 editor를 이용하여 값을 저장시켜준다.
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DB",DBversion); // key, value를 이용하여 저장하는 형태


        //최종 커밋
        editor.commit();


    }
    private class splashhandler implements Runnable{
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

            SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
            String text = sf.getString("DB","");//첫 사용인지 구분
            System.out.println("current version : "+text);
            if(!text.equals(DBversion))//첫사용 혹은 db업데이트일때
            {

                System.out.println("업데이트!");
                //db업데이트
                Intent intent = new Intent(SplashActivity.this, LoadingActivity.class);
                intent.putExtra("semester",semester);
                intent.putExtra("year",year);
                startActivity(intent);

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
