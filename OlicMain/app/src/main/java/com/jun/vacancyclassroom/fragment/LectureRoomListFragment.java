package com.jun.vacancyclassroom.fragment;


import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.databinding.FragmentLectureroomlistBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.adapter.LectureRoomListAdapter;
import com.jun.vacancyclassroom.model.LectureRoom;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LectureRoomListFragment extends Fragment {

    private static LectureRoomListAdapter adapter;
    private AdView mAdView;

    private View view;
    private EditText search_edittext;

    private static MainViewModel viewModel;
    private FragmentLectureroomlistBinding binding;



    public LectureRoomListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lectureroomlist, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        view = binding.getRoot();

        setAdView();

        adapter = new LectureRoomListAdapter(getActivity().getApplication(), viewModel);

        //강의실 데이터 변할때마다 호출
        viewModel.getLectureRooms().observe(getViewLifecycleOwner(), lecturerooms -> {
            ArrayList<LectureRoom> list = new ArrayList<>(lecturerooms);
            Collections.sort(list);
            adapter.submitList(list);
        });

        //즐겨찾기 데이터
        viewModel.getBookMarkedRoomsData().observe(getViewLifecycleOwner(), bookMarkedRooms -> {
            ArrayList<String> list = new ArrayList<>();

            for(int i = 0;i < bookMarkedRooms.size();i++)
                list.add(bookMarkedRooms.get(i).lecture_room);
            Collections.sort(list);
            adapter.setBookmarkedSet(list);
        });

        //구분선 적용
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.lecturelistRecyclerview.addItemDecoration(dividerItemDecoration);
        binding.lecturelistRecyclerview.setAdapter(adapter);

        setSearchEdit();

        return view;
    }


    //검색창 설정
    private void setSearchEdit() {

        search_edittext = view.findViewById(R.id.search_classroom);
        search_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                String searchWord = arg0.toString();

                List<LectureRoom> list = new ArrayList<>();

                //검색어 없으면 전체 포함
                if(searchWord.isEmpty())
                {
                    viewModel.getLectureRooms().observe(getViewLifecycleOwner(), lectureRooms -> {
                        list.addAll(lectureRooms);
                    });

                }
                else
                {
                    viewModel.getLectureRooms().observe(getViewLifecycleOwner(), lectureRooms -> {
                        for(int i = 0;i < lectureRooms.size();i++)
                        {
                            if(lectureRooms.get(i).lecture_room.toLowerCase().contains(searchWord.toLowerCase()))
                                list.add(lectureRooms.get(i));
                        }
                    });
                }

                Collections.sort(list);
                //리스트 업데이트
                adapter.submitList(list);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
    }

    private void setAdView() {
        mAdView = view.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }


}
