package com.jun.vacancyclassroom;


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
import com.jun.vacancyclassroom.adapter.SearchAdapter;
import com.jun.vacancyclassroom.item.SearchItem;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentA extends Fragment {

    SearchAdapter adapter;
    ListView listView;
    private AdView mAdView;
    MyDBHelper helper;

    View view;
    ArrayList<String> old_checked=new ArrayList<>();
    EditText search_edittext;

    public FragmentA() {
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
        view=inflater.inflate(R.layout.fragment_a,container,false);
        System.out.println("Fragment A 출력");
        mAdView = (AdView) view.findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

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
                System.out.print(s);
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

        adapter=new SearchAdapter();
        listView=(ListView)view.findViewById(R.id.searchlist_a);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchItem item=(SearchItem)adapter.getItem(i);

                helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
                SQLiteDatabase db=helper.getReadableDatabase();
                db.execSQL("CREATE TABLE IF NOT EXISTS bookmarklist (classroom TEXT)");
                Cursor cursor=db.rawQuery("SELECT * FROM bookmarklist WHERE classroom ='"+item.getClassroom()+"';",null);

                //만약 즐겨찾기 db에 없다면 추가
                if(cursor.getCount()==0)
                {
                    db.execSQL("INSERT INTO bookmarklist (classroom) VALUES ('"+item.getClassroom()+"');");
                    listView.setItemChecked(i,true);
                    Toast.makeText(getContext(),"즐겨찾기에 추가했습니다.",Toast.LENGTH_SHORT).show();
                }
                else {//즐겨찾기에 있다면 삭제
                    db.execSQL("DELETE FROM bookmarklist WHERE classroom = '"+item.getClassroom()+"';");
                    listView.setItemChecked(i,false);
                    Toast.makeText(getContext(),"즐겨찾기가 해제됐습니다.",Toast.LENGTH_SHORT).show();
                }
                cursor.close();
                if(db!=null)
                    db.close();
            }
        });

        loadList("");//초기 리스트 불러오기
        getChecked();//체크설정
        return view;
    }

    //리스트뷰 생성
    public void loadList(String searching_word){
        helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM classroomlist ;", null);
        if(searching_word.equals(""))//전체보여주기
        {
            while (c.moveToNext()) {
                String classroom = c.getString(0);
                String time = c.getString(1);
                adapter.addItem(classroom, time);
            }
        }
        else//검색어 존재할시
        {
            while (c.moveToNext()) {
                String classroom = c.getString(0);
                String time = c.getString(1);
                if(classroom.toUpperCase().contains(searching_word.trim()) || classroom.toLowerCase().contains(searching_word.trim()))//영어 대소문자 둘다 검사
                    adapter.addItem(classroom, time);
            }
        }
        c.close();
        if(db!=null)
            db.close();
    }
    public void getChecked(){
        helper=new MyDBHelper(getContext(),"lecture_list.db",null,1);
        SQLiteDatabase db=helper.getReadableDatabase();
        db.execSQL("CREATE TABLE IF NOT EXISTS bookmarklist (classroom TEXT)");

        old_checked=new ArrayList<>();

        Cursor cursor=db.rawQuery("SELECT * FROM bookmarklist ;",null);
        while(cursor.moveToNext())
        {
            old_checked.add(cursor.getString(0));
        }
        cursor.close();
        for(int i=0;i<adapter.getCount();i++){
            SearchItem item=(SearchItem)adapter.getItem(i);
            cursor=db.rawQuery("SELECT * FROM bookmarklist WHERE classroom ='"+item.getClassroom()+"';",null);
            //만약 즐겨찾기 db에 없다면 체크해제
            if(cursor.getCount()==0)
            {
                listView.setItemChecked(i,false);
            }
            else {//즐겨찾기에 있다면 체크
                listView.setItemChecked(i,true);
            }
            cursor.close();
        }
        if (db!=null)
            db.close();

    }

}
