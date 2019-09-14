package com.jun.vacancyclassroom;

import android.content.Intent;
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

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 함
    }


}
