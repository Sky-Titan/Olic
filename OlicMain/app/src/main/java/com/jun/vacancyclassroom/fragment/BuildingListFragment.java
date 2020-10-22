package com.jun.vacancyclassroom.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.adapter.BuildingListAdapter;
import com.jun.vacancyclassroom.model.Building;
import com.jun.vacancyclassroom.model.LectureRoom;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BuildingListFragment extends Fragment {

    private BuildingListAdapter adapter;

    private View view;
    private EditText search_edittext;

    private MainViewModel viewModel;

    private static final String TAG = "BuildingListFragment";

    private String searchWord = "";

    public BuildingListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_buildinglist,container,false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.buildinglist_recyclerview);

        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        adapter = new BuildingListAdapter(getActivity(), viewModel);

        renewal();

        //구분선 적용
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);


        search_edittext = (EditText)view.findViewById(R.id.search_building);
        setSearchEdit();

        return view;
    }

    //검색창 설정
    private void setSearchEdit() {

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
        viewModel.getBuildings().observe(getViewLifecycleOwner(), buildings -> {
            List<Building> list = new ArrayList<>();

            if(searchWord.isEmpty())
                list.addAll(buildings);
            else
            {
                for(int i = 0;i < buildings.size();i++)
                {
                    if(buildings.get(i).buildingName.toLowerCase().contains(searchWord.toLowerCase()))
                        list.add(buildings.get(i));
                }
            }

            Collections.sort(list);
            //리스트 업데이트
            adapter.submitList(list);
        });
    }


}
