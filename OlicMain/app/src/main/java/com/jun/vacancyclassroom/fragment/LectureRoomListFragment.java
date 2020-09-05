package com.jun.vacancyclassroom.fragment;


import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.databinding.FragmentLectureroomlistBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.adapter.LectureRoomListAdapter;
import com.jun.vacancyclassroom.database.DatabaseLibrary;
import com.jun.vacancyclassroom.item.Lecture;
import com.jun.vacancyclassroom.item.LectureRoom;
import com.jun.vacancyclassroom.item.ListLiveData;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;


public class LectureRoomListFragment extends Fragment {

    private static LectureRoomListAdapter adapter;
    private RecyclerView recyclerView;
    private AdView mAdView;

    private View view;
    private ArrayList<String> old_checked=new ArrayList<>();
    private EditText search_edittext;

    private DatabaseLibrary databaseLibrary;

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

//        databaseLibrary = DatabaseLibrary.getInstance(null);

        setAdView();

        adapter = new LectureRoomListAdapter(getActivity().getApplication(), viewModel);

        //강의실 데이터 변할때마다 호출
        viewModel.getLectureRooms().observe(getViewLifecycleOwner(), lecturerooms -> {
            ArrayList<LectureRoom> list = new ArrayList<>(lecturerooms);
            adapter.submitList(list);
        });

        //q
        viewModel.getBookMarkedRoomsData().observe(getViewLifecycleOwner(), bookMarkedRooms -> {
            adapter.setBookmarkedSet(bookMarkedRooms);
        });

        binding.lecturelistRecyclerview.setAdapter(adapter);

        setSearchEdit();

        return view;
    }


    //검색
    private void setSearchEdit() {

        search_edittext=(EditText)view.findViewById(R.id.search_classroom);
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

                //검색
                adapter.submitList(list);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });
    }

    private void setAdView() {
        mAdView = (AdView) view.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    //리스트뷰 생성
/*    public void loadList(String searching_word)
    {
        ProgressDialog asyncDialog = new ProgressDialog(
                getContext(), R.style.AppCompatAlertDialogStyle);

        new AsyncTask<Void, Void, Cursor>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                asyncDialog.setCancelable(false);
                asyncDialog.setCanceledOnTouchOutside(false);//터치해도 다이얼로그 안 사라짐

                asyncDialog.show();
            }

            @Override
            protected Cursor doInBackground(Void... voids) {
                return databaseLibrary.selectLectureRoomList();
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);
                if(searching_word.equals(""))//전체보여주기
                {
                    while (cursor.moveToNext()) {

                        String classroom = cursor.getString(0);
                        String time = cursor.getString(1);
                        adapter.addItem(classroom, time);
                    }
                }
                else//검색어 존재할시
                {
                    while (cursor.moveToNext()) {
                        String classroom = cursor.getString(0);
                        String time = cursor.getString(1);

                        //영어 대소문자 둘다 검사
                        if(classroom.toUpperCase().contains(searching_word.trim()) || classroom.toLowerCase().contains(searching_word.trim()))
                            adapter.addItem(classroom, time);
                    }
                }
                cursor.close();

                asyncDialog.dismiss();
                asyncDialog.cancel(); //메모리 누수방지지
            }

        }.execute();

    }
*/

    //체크 되어 있는지 여부
 /*   private void getChecked(){

        ProgressDialog asyncDialog = new ProgressDialog(
                getContext(), R.style.AppCompatAlertDialogStyle);

        old_checked = new ArrayList<>();

        new AsyncTask<Void, Object, Void>()
        {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                asyncDialog.setCancelable(false);
                asyncDialog.setCanceledOnTouchOutside(false);//터치해도 다이얼로그 안 사라짐

                asyncDialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {


                Cursor cursor = databaseLibrary.selectBookmarkList();

                while(cursor.moveToNext())
                    old_checked.add(cursor.getString(0));

                cursor.close();

                for(int i = 0;i<adapter.getCount();i++){
                    LectureRoomItem item=(LectureRoomItem)adapter.getItem(i);

                    cursor = databaseLibrary.selectBookmarkList(item.getLectureRoom());

                    //만약 즐겨찾기 db에 없다면 체크해제
                    if(cursor.getCount()==0)
                        publishProgress(i, false);
                        //즐겨찾기에 있다면 체크
                    else
                        publishProgress(i, true);
                    cursor.close();
                }

                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);

                int index = (int)values[0];
                boolean check = (boolean)values[1];

                recyclerView.setItemChecked(index, check);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                asyncDialog.dismiss();
                asyncDialog.cancel(); //메모리 누수방지지
            }
        }.execute();

    }*/

}
