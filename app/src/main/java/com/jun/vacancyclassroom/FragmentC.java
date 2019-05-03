package com.jun.vacancyclassroom;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vacancyclassroom.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jun.vacancyclassroom.adapter.BuildingSearchAdapter;
import com.jun.vacancyclassroom.adapter.SearchAdapter;
import com.jun.vacancyclassroom.item.BuildingItem;
import com.jun.vacancyclassroom.item.SearchItem;

import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentC extends Fragment {

    BuildingSearchAdapter adapter;
    ListView listView;
    private AdView mAdView;
    MyDBHelper helper;

    ArrayList<String> buildingName_list = new ArrayList<>();
    View view;
    EditText search_edittext;
    public FragmentC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_c,container,false);

        mAdView = (AdView) view.findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        //검색
        search_edittext=(EditText)view.findViewById(R.id.search_building);
        search_edittext.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트에 변화가 있을 때

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // 입력이 끝났을 때
                String s=arg0.toString();
                System.out.print(s);
                adapter=new BuildingSearchAdapter();
                listView.setAdapter(adapter);
                loadList(s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
            }
        });

        adapter=new BuildingSearchAdapter();
        listView=(ListView)view.findViewById(R.id.searchlist_c);
        listView.setAdapter(adapter);

        //리스트 뷰 클릭시 buildingActivity 띄움
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BuildingItem item=(BuildingItem)adapter.getItem(i);
                Intent intent = new Intent(getContext(),BuildingActivity.class);
                intent.putExtra("buildingName",item.getBuildingName());
                startActivity(intent);
            }
        });

        loadList("");//초기 리스트 불러오기
        return view;
    }
    //리스트뷰 생성
    public void loadList(String searching_word){
        boolean isIn;
        helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM classroomlist ;", null);

        buildingName_list = new ArrayList<>();
        if(searching_word.equals(""))//전체보여주기
        {
            while (c.moveToNext()) {
                isIn=false;
                String classroom = c.getString(0);
                if(classroom.equals("") || classroom.equals("-"))//이름없는 강의실이면 스킵
                    continue;
                StringTokenizer tokens = new StringTokenizer(classroom, "-");
                String[] buildingName = new String[tokens.countTokens()];
                System.out.println("ss : "+classroom);
                buildingName[0] = tokens.nextToken();
                for(int i=0;i<buildingName_list.size();i++)
                {
                    if(buildingName_list.get(i).equals(buildingName[0]))
                    {
                        isIn = true;
                        break;
                    }
                }
                if(isIn == false) {
                    System.out.println("token : " + buildingName[0]);
                    buildingName_list.add(buildingName[0]);
                    adapter.addItem(buildingName[0]);
                }
            }
        }
        else//검색어 존재할시
        {
            while (c.moveToNext()) {
                isIn=false;
                String classroom = c.getString(0);
                if(classroom.equals("") || classroom.equals("-"))//이름없는 강의실이면 스킵
                    continue;
                StringTokenizer tokens = new StringTokenizer(classroom, "-");
                String[] buildingName = new String[tokens.countTokens()];
                buildingName[0] = tokens.nextToken();

                for(int i=0;i<buildingName_list.size();i++)
                {
                    if(buildingName_list.get(i).equals(buildingName[0]))
                    {
                        isIn = true;
                        break;
                    }
                }
                if(buildingName[0].toUpperCase().contains(searching_word.trim()) || buildingName[0].toLowerCase().contains(searching_word.trim()))//영어 대소문자 둘다 검사
                {
                    if(isIn == false)
                    {
                        buildingName_list.add(buildingName[0]);
                        adapter.addItem(buildingName[0]);
                    }
                }
            }
        }
        c.close();
        if(db!=null)
            db.close();
    }

}
