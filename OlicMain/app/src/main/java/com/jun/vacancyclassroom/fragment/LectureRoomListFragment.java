package com.jun.vacancyclassroom.fragment;


import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacancyclassroom.R;


import com.jun.vacancyclassroom.adapter.LectureRoomListAdapter;
import com.jun.vacancyclassroom.model.LectureRoom;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LectureRoomListFragment extends Fragment {

    private static LectureRoomListAdapter adapter;

    private View view;
    private EditText search_edittext;

    private static MainViewModel viewModel;


    private RecyclerView recyclerView;
    private String searchWord = "";

    private static final String TAG = "LectureRoomListFragment";


    public LectureRoomListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lectureroomlist,container,false);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        adapter = new LectureRoomListAdapter(getActivity().getApplication(), viewModel);

        //강의실 데이터 변할때마다 호출
        renewal();

        recyclerView = view.findViewById(R.id.lecture_room_list_recyclerview);

        //구분선 적용
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

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
                searchWord = arg0.toString();

                renewal();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
    }

    private void renewal()
    {
        //검색어 없으면 전체 포함
        viewModel.getLectureRooms().observe(getViewLifecycleOwner(), lectureRooms -> {

            List<LectureRoom> list = new ArrayList<>();
            list.clear();
            if(searchWord.isEmpty())
            {
                list.addAll(lectureRooms);
            }
            else {
                for(int i = 0;i < lectureRooms.size();i++)
                {
                    if(lectureRooms.get(i).lecture_room.toLowerCase().contains(searchWord.toLowerCase()))
                        list.add(lectureRooms.get(i));
                }
            }
            Log.d(TAG, "lecturerooms 사이즈 : "+lectureRooms.size());
            Log.d(TAG, "리스트 사이즈 : "+list.size());
            Collections.sort(list);
            //리스트 업데이트
            adapter.submitList(list);
        });
    }



}
