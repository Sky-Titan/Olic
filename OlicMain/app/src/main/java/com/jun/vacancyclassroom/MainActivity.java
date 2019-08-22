package com.jun.vacancyclassroom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.MobileAds;
import com.jun.vacancyclassroom.adapter.ViewPagerAdapter;
import com.jun.vacancyclassroom.item.Lecture;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String DATABASE_NAME = "lecture_list.db";
    private static final String PACKAGE_DIR = "/data/data/com.jun.vacancyclassroom/databases";


    private ArrayList<String> url_List=new ArrayList<>();
    private ArrayList<Lecture> mysql_List=new ArrayList<>();
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat="";
    String myJSON;
    JSONArray lectures = null;

    private static final String TAG_RESULTS = "result";
    private static final String TAG_CLASSROOM = "classroom";
    private static final String TAG_CODE = "code";
    private static final String TAG_TITLE = "title";
    private static final String TAG_TIME = "time";

    private String DBversion;//현재 학기를 db버전으로 사용
    /*
    db 만들기 parsing->insertdata->classification(강의실 이름)
    */

    //메인화면구성
    MenuItem prevMenuItem;
    FragmentA fragment_A;
    FragmentB fragment_B;
    FragmentC fragment_C;
    static final int G_NOTIFY_NUM = 1;
    private NonSwipeViewPager mViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fr = null;
            FragmentManager fm =null;
            FragmentTransaction fragmentTransaction=null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0);
                    fragment_A.onResume();
                    return true ;

                case R.id.navigation_dashboard:
                    mViewPager.setCurrentItem(1);
                    fragment_B.onResume();
                    return true;

                case R.id.navigation_buildingSearch:
                    mViewPager.setCurrentItem(2);
                    fragment_C.onResume();
                    return true;
            }

            return false;
        }
    };
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
    @Override
    public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        setTitle(R.string.semester);//타이틀바 텍스트
            setContentView(R.layout.activity_main);

            DBversion = getResources().getString(R.string.semester);//현재 학기를 db버전으로 사용

        MobileAds.initialize(this, "ca-app-pub-7245602797811817~6821353940");
        SharedPreferences sf = getSharedPreferences("sFile",MODE_PRIVATE);
        String text = sf.getString("DB","");//첫 사용인지 구분
        if(!text.equals(DBversion))//첫사용 혹은 db업데이트일때 -> db복제
        {
            //db복제
            initialize(getApplicationContext());
        }
       final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        mViewPager=(NonSwipeViewPager)findViewById(R.id.fragment_container);
        mViewPager.setPagingDisabled();//터치 스와이프 못하게 하기
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if(prevMenuItem!=null){
                    prevMenuItem.setChecked(false);
                }
                else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                navigation.getMenu().getItem(i).setChecked(true);
                prevMenuItem=navigation.getMenu().getItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        setupViewPager(mViewPager);
    }

    //처음 접속시 db파일 에셋에서 불러옴
    public static void initialize(Context ctx) {
        File folder = new File(PACKAGE_DIR);
        folder.mkdirs();

        File outfile = new File(PACKAGE_DIR + "/" + DATABASE_NAME);

 //       if (outfile.length() <= 0) {
            AssetManager assetManager = ctx.getResources().getAssets();
            try {
                InputStream is = assetManager.open(DATABASE_NAME, AssetManager.ACCESS_BUFFER);
                long filesize = is.available();
                byte [] tempdata = new byte[(int)filesize];
                is.read(tempdata);
                is.close();
                outfile.createNewFile();
                FileOutputStream fo = new FileOutputStream(outfile);
                fo.write(tempdata);
                fo.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
   //     }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragment_A = new FragmentA();
        fragment_B = new FragmentB();
        fragment_C = new FragmentC();
        viewPagerAdapter.addFragment(fragment_A);
        viewPagerAdapter.addFragment(fragment_B);
        viewPagerAdapter.addFragment(fragment_C);
        viewPager.setAdapter(viewPagerAdapter);
    }

}
