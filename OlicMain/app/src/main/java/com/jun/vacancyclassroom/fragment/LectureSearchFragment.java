package com.jun.vacancyclassroom.fragment;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacancyclassroom.R;
import com.jun.vacancyclassroom.activity.LoadingActivity;
import com.jun.vacancyclassroom.adapter.SearchLectureAdapter;

import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.model.Lecture;
import com.jun.vacancyclassroom.model.SearchLecture;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;


public class LectureSearchFragment extends Fragment {

    private View view;
    private AutoCompleteTextView autocomplete;

    private MainViewModel viewModel;

    private SearchLectureAdapter adapter;
    private MyDatabase database;

    private static final String TAG = "LectureSearchFragment";

    public LectureSearchFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_lecturesearch,container,false);

        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        database = MyDatabase.getInstance(getContext());

        RecyclerView recyclerView = view.findViewById(R.id.lecturesearch_recyclerview);

        adapter = new SearchLectureAdapter(getContext(), viewModel);

        //검색 강의 목록 observe
        viewModel.getSearchLectures().observe(getViewLifecycleOwner(), searchLectures -> {

           renewal(searchLectures);

        });

        //구분선 적용
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);

        //자동완성 뷰 설정
        autocomplete = view.findViewById(R.id.add_lecture_autocomplete);
        autocomplete.setCompletionHint("목록에 없다면 강의 추가 버튼을 눌러주세요.");

        //전체 강의 목록에서 가져온다.
        viewModel.getLectures().observe(getViewLifecycleOwner(), lectures -> {
            ArrayList<String> list = new ArrayList<>();

            for(int i = 0;i < lectures.size();i++)
                list.add(lectures.get(i).lecture_code);

            autocomplete.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, list));

            autocomplete.setOnItemClickListener((adapterView, view1, i, l) -> {
                Log.d(TAG, ((TextView)view1).getText().toString());
                viewModel.addSearchLecture(new SearchLecture( ((TextView)view1).getText().toString()));
            });
        });

        //강의 추가 버튼
        Button add_button = view.findViewById(R.id.add_lecture_btn);
        add_button.setOnClickListener(view1 -> {
            viewModel.addSearchLecture(new SearchLecture(autocomplete.getText().toString()));
        });

        return view;
    }

    //새로고침
    public void renewal(List<SearchLecture> searchLectures)
    {
        ProgressDialog asyncDialog = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);
        asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        asyncDialog.setTitle("잠시만 기다려주세요.");
        asyncDialog.setCancelable(false);
        asyncDialog.setOwnerActivity(getActivity());
        asyncDialog.setCanceledOnTouchOutside(false);//터치해도 다이얼로그 안 사라짐
        asyncDialog.show();

        Observable.create(emitter -> {
            ArrayList<Lecture> lectures = new ArrayList<>();

            for(int i = 0;i < searchLectures.size();i++)
            {
                //크롤링 해온다.
                final String lecture_code = searchLectures.get(i).lecture_code;
                Lecture lecture = database.searchLecture(lecture_code);

                if(lecture!=null)
                    lectures.add(lecture);
                else
                {
                    viewModel.removeSearchLecture(new SearchLecture(lecture_code));
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), lecture_code+" 강의를 찾을 수 없습니다.",Toast.LENGTH_SHORT).show());
                }
            }

            emitter.onNext(lectures);

        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    asyncDialog.dismiss();
                    asyncDialog.cancel(); //메모리 누수방지지
                    ArrayList<Lecture> lectures = (ArrayList<Lecture>)o;
                    adapter.submitList(lectures);
                });
    }



}
