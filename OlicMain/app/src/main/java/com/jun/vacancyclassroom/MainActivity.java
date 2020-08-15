package com.jun.vacancyclassroom;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.MobileAds;
import com.jun.vacancyclassroom.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    //메인화면구성
    MenuItem prevMenuItem;
    FragmentA fragment_A;
    FragmentB fragment_B;
    FragmentC fragment_C;
    FragmentD fragment_D;

    private NonSwipeViewPager mViewPager;

    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Myapplication myapplication = (Myapplication)getApplication();
        setTitle(myapplication.getCurrentSemester());

        MobileAds.initialize(this, "ca-app-pub-7245602797811817~6821353940");


        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setViewPager(navigation);
    }

    private void setViewPager(final BottomNavigationView navigation) {
        mViewPager=(NonSwipeViewPager)findViewById(R.id.fragment_container);
        mViewPager.setPagingDisabled();//터치 스와이프 못하게 하기

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

        setupViewPagerAdapter(mViewPager);
    }

    //동기화버튼 표시
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timetable_synch, menu);
        return true;
    }

    //동기화 버튼 누르면 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        switch (item.getItemId()) {
            case R.id.synch_btn1:
                if(mViewPager.getCurrentItem()!=3)//db 업데이트
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("시간표 동기화");
                    builder.setMessage("시간표를 새로 동기화 하시겠습니까?");
                    builder.setPositiveButton("동기화",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(),"시간표를 새로 동기화합니다.",Toast.LENGTH_LONG).show();

                                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                                    //네트워크 연결되어있으면
                                    if(networkInfo != null && networkInfo.isConnected())
                                    {
                                        //db업데이트
                                        dropTables();

                                        //동기화
                                        Myapplication myapplication = (Myapplication) getApplication();

                                        String version = myapplication.getCurrentSemester();
                                        int year = Integer.parseInt(version.substring(0,4));
                                        String semester = version.substring(4,5);

                                        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
                                        intent.putExtra("semester",semester);
                                        intent.putExtra("year",year);
                                        startActivity(intent);

                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this,"시간표 동기화를 위해서 인터넷을 연결후 시도해주세요.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                }
                            });

                    builder.setNegativeButton("취소", null);
                    builder.show();
                }
                else//수강신청현황
                {
                    fragment_D.loadAdapter();//새로고침
                }

                return true;
            default:
                return true;
        }
    }

    private void dropTables() {
        MyDBHelper helper=new MyDBHelper(getApplicationContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS classroomlist");
        db.execSQL("DROP TABLE IF EXISTS bookmarklist");
        db.execSQL("DROP TABLE IF EXISTS lecture");
        db.close();
    }

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

                case R.id.navigation_lectureSearch:
                    mViewPager.setCurrentItem(3);
                    fragment_D.onResume();
                    return true;
            }

            return false;
        }
    };

    private void setupViewPagerAdapter(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragment_A = new FragmentA();
        fragment_B = new FragmentB();
        fragment_C = new FragmentC();
        fragment_D = new FragmentD();

        viewPagerAdapter.addFragment(fragment_A);
        viewPagerAdapter.addFragment(fragment_B);
        viewPagerAdapter.addFragment(fragment_C);
        viewPagerAdapter.addFragment(fragment_D);

        viewPager.setAdapter(viewPagerAdapter);
    }

}
