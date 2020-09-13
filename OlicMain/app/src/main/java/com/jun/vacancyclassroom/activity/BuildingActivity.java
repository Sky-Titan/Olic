package com.jun.vacancyclassroom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.database.DatabaseLibrary;
import com.jun.vacancyclassroom.adapter.BookmarkListAdapter;
import com.jun.vacancyclassroom.model.BookMarkedRoom;
import com.jun.vacancyclassroom.viewmodel.BuildingViewModel;
import com.jun.vacancyclassroom.viewmodel.BuildingViewModelFactory;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

public class BuildingActivity extends AppCompatActivity {

    private BookmarkListAdapter adapter;
    private RecyclerView recyclerView;
    private AdView mAdView;

    private BuildingViewModel viewModel;

    private String buildingName;
    private TextView buildingName_textview;

    private static final String TAG = "BuildingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);

        //인텐트 받아오고 건물 이름 지정
        buildingName_textview = findViewById(R.id.buildingName_buildingActivity);
        Intent intent = getIntent();
        buildingName = intent.getExtras().getString("buildingName");
        buildingName_textview.setText(buildingName);


        mAdView = findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        recyclerView = findViewById(R.id.lectureBuilding_recyclerview);
        adapter = new BookmarkListAdapter(getApplication(), this);

        viewModel = new ViewModelProvider(this, new BuildingViewModelFactory(getApplication())).get(BuildingViewModel.class);
        viewModel.getLectureRooms(buildingName+"%").observe(this, lectureRooms -> {
            adapter.submitList(lectureRooms);
        });

        //구분선 적용
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

    }

}
