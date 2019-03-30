package com.example.vacancyclassroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //애니메이션 효과 적용
        TextView lovenu=(TextView)findViewById(R.id.olic_splash);
        TextView appment=(TextView)findViewById(R.id.app_ment_splash);
        final ImageView heart=(ImageView)findViewById(R.id.roomimg_splash);

        View view = getLayoutInflater().from(this).inflate(R.layout.activity_splash,null);
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
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
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
        });

    }
}
