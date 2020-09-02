package com.jun.vacancyclassroom.fragment;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.activity.LoadingActivity;
import com.jun.vacancyclassroom.adapter.SearchAdapter;
import com.jun.vacancyclassroom.database.DatabaseLibrary;
import com.jun.vacancyclassroom.item.SearchItem;

import java.util.ArrayList;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;


public class LectureRoomListFragment extends Fragment {

    private SearchAdapter adapter;
    private ListView listView;
    private AdView mAdView;

    private View view;
    private ArrayList<String> old_checked=new ArrayList<>();
    private EditText search_edittext;

    private DatabaseLibrary databaseLibrary;

    public LectureRoomListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume(){
        super.onResume();

        getChecked();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_lectureroomlist,container,false);

        databaseLibrary = DatabaseLibrary.getInstance(null);

        setAdView();

        setSearchEdit();

        adapter=new SearchAdapter();

        setListView();

        loadList("");//초기 리스트 불러오기
        getChecked();//체크설정
        return view;
    }

    private void setListView() {

        ProgressDialog asyncDialog = new ProgressDialog(
                getContext(), R.style.AppCompatAlertDialogStyle);

        listView=(ListView)view.findViewById(R.id.searchlist_a);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {

            SearchItem item=(SearchItem)adapter.getItem(i);

            new AsyncTask<Void, Void, Boolean>(){

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Boolean doInBackground(Void... voids) {
                    Cursor cursor = databaseLibrary.selectBookmarkList(item.getClassroom());

                    //만약 즐겨찾기 db에 없다면 추가
                    if(cursor.getCount()==0)
                    {
                        databaseLibrary.insertBookmarkList(item.getClassroom());
                        cursor.close();
                        return true;
                    }
                    else {//즐겨찾기에 있다면 삭제
                        databaseLibrary.deleteBookmarkList(item.getClassroom());
                        cursor.close();
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean isAdd) {
                    super.onPostExecute(isAdd);

                    if(isAdd)
                    {
                        listView.setItemChecked(i,true);
                        Toast.makeText(getContext(),"즐겨찾기에 추가했습니다.",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        listView.setItemChecked(i,false);
                        Toast.makeText(getContext(),"즐겨찾기가 해제됐습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        });
    }

    private void setSearchEdit() {
        //검색
        search_edittext=(EditText)view.findViewById(R.id.search_classroom);
        search_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                String s=arg0.toString();

                //새로 어댑터 구성
                adapter=new SearchAdapter();
                listView.setAdapter(adapter);

                loadList(s);

                getChecked();
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
    public void loadList(String searching_word)
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


    //체크 되어 있는지 여부
    private void getChecked(){

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
                    SearchItem item=(SearchItem)adapter.getItem(i);

                    cursor = databaseLibrary.selectBookmarkList(item.getClassroom());

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

                listView.setItemChecked(index, check);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                asyncDialog.dismiss();
                asyncDialog.cancel(); //메모리 누수방지지
            }
        }.execute();

    }

}
