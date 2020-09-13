package com.jun.vacancyclassroom.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.adapter.BookmarkListAdapter;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.Calendar;
import java.util.Collections;
import java.util.TimeZone;


public class BookmarkListFragment extends Fragment {

    private LinearLayout layout_time;
    private Button currentTime,visibility_time;

    private BookmarkListAdapter adapter;

    private AdView mAdView;

    private View view;

    private TimePicker timePicker;
    private NumberPicker dayPicker;

    private MainViewModel viewModel;

    public BookmarkListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_bookmarklist,container,false);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);


        layout_time = view.findViewById(R.id.linearLayout_time);
        visibility_time = view.findViewById(R.id.visible_btn);
        currentTime = view.findViewById(R.id.currentTime_btn);
        dayPicker = view.findViewById(R.id.dayPicker);
        timePicker = view.findViewById(R.id.timePicker);//타임픽커 현재 시간으로 설정

        RecyclerView recyclerView = view.findViewById(R.id.bookmakrlist_recyclerview);

        adapter = new BookmarkListAdapter(getActivity().getApplication(), getContext());
        viewModel.getBookMarkedRoomsData().observe(getViewLifecycleOwner(), bookmakredrooms -> {
            Collections.sort(bookmakredrooms);
            adapter.submitList(bookmakredrooms);
        });

        //구분선 적용
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        Calendar now = setTimePicker();

        setDayPicker(now);

        setCurrentTime();

        setVisibilityTime();

        setAdView();

        return view;
    }

    //시간 설정 레이아웃
    private void setVisibilityTime() {

        visibility_time.setOnClickListener(view -> {

            if(layout_time.getVisibility() == View.VISIBLE)//끄기
            {
                layout_time.setVisibility(View.GONE);
                visibility_time.setText("시간 설정 보이기");
                visibility_time.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.ic_arrow_drop_down_black_36dp),null);
            }
            else//켜기
            {
                layout_time.setVisibility(View.VISIBLE);
                visibility_time.setText("시간 설정 숨기기");
                visibility_time.setCompoundDrawablesWithIntrinsicBounds(null,null,getActivity().getDrawable(R.drawable.ic_arrow_drop_up_black_36dp),null);
            }
        });
    }

    //현재시간으로 이동
    private void setCurrentTime() {

        currentTime.setOnClickListener(view -> {

            TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
            Calendar now = Calendar.getInstance(timeZone);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            timePicker.setHour(hour);
            timePicker.setMinute(minute);
            dayPicker.setValue(now.get(Calendar.DAY_OF_WEEK));

            adapter.setTime(timePicker.getHour(), timePicker.getMinute(), dayPicker.getValue());
        });
    }

    //요일 피커 설정
    private void setDayPicker(Calendar now)
    {
        dayPicker.setDisplayedValues(new String[]{"일","월","화","수","목","금","토"});
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(7);
        dayPicker.setValue(now.get(Calendar.DAY_OF_WEEK));

        dayPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
                adapter.setTime(timePicker.getHour(), timePicker.getMinute(), dayPicker.getValue());
        });
    }

    //시간 피커 설정
    private Calendar setTimePicker() {
        timePicker.setIs24HourView(true);

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar now = Calendar.getInstance(timeZone);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        timePicker.setHour(hour);
        timePicker.setMinute(minute);

        timePicker.setOnTimeChangedListener((timePicker, i, i1) -> {
            adapter.setTime(timePicker.getHour(), timePicker.getMinute(), dayPicker.getValue());
        });

        return now;
    }

    private void setAdView() {
        mAdView = view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }
}
