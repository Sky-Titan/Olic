package com.jun.vacancyclassroom.activity;


import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jun.vacancyclassroom.fragment.*;

import com.jun.vacancyclassroom.Myapplication;
import com.jun.vacancyclassroom.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    //메인화면구성

    LectureRoomListFragment fragment_lectureRoomList;
    BookmarkListFragment fragment_bookmarkList;
    BuildingListFragment fragment_buildingList;
    LectureSearchFragment fragment_lectureSearch;

    private ViewPager2 mViewPager;

    private static final String TAG = "MainActivity";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Myapplication myapplication = (Myapplication)getApplication();
        setTitle(myapplication.getCurrentSemester());

        MobileAds.initialize(this, "ca-app-pub-7245602797811817~6821353940");


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setViewPager();
    }

    private void setViewPager() {
        mViewPager=(ViewPager2) findViewById(R.id.fragment_container);
        mViewPager.setUserInputEnabled(false);//터치 스와이프 못하게 하기

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
                    builder.setPositiveButton("동기화",(dialog, which) -> {

                        Toast.makeText(getApplicationContext(),"시간표를 새로 동기화합니다.",Toast.LENGTH_LONG).show();

                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                        //네트워크 연결되어있으면
                        if(networkInfo != null && networkInfo.isConnected())
                        {
                            //db업데이트

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
                    });

                    builder.setNegativeButton("취소", null);
                    builder.show();
                }
                else//수강신청현황
                {
                    fragment_lectureSearch.loadAdapter();//새로고침
                }

                return true;
            default:
                return true;
        }
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
                    fragment_lectureRoomList.onResume();
                    return true ;

                case R.id.navigation_dashboard:
                    mViewPager.setCurrentItem(1);
                    fragment_bookmarkList.onResume();
                    return true;

                case R.id.navigation_buildingSearch:
                    mViewPager.setCurrentItem(2);
                    fragment_buildingList.onResume();
                    return true;

                case R.id.navigation_lectureSearch:
                    mViewPager.setCurrentItem(3);
                    fragment_lectureSearch.onResume();
                    return true;
            }

            return false;
        }
    };

    private void setupViewPagerAdapter(ViewPager2 viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        fragment_lectureRoomList = new LectureRoomListFragment();
        fragment_bookmarkList = new BookmarkListFragment();
        fragment_buildingList = new BuildingListFragment();
        fragment_lectureSearch = new LectureSearchFragment();

        viewPagerAdapter.addFragment(fragment_lectureRoomList);
        viewPagerAdapter.addFragment(fragment_bookmarkList);
        viewPagerAdapter.addFragment(fragment_buildingList);
        viewPagerAdapter.addFragment(fragment_lectureSearch);

        viewPager.setAdapter(viewPagerAdapter);
    }

}
