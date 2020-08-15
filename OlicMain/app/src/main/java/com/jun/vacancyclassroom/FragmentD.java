package com.jun.vacancyclassroom;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorLong;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.vacancyclassroom.R;
import com.jun.vacancyclassroom.adapter.LectureAdapter;
import com.jun.vacancyclassroom.adapter.SearchAdapter;
import com.jun.vacancyclassroom.item.Lecture;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentD extends Fragment {

    private MyDBHelper helper;
    View view;
    private EditText add_lecture_edit;
    private Button add_lecture;

    private String current_subj_cde="";

    private String url ="";


    ArrayList<String> codes;
    LectureAdapter adapter;
    ListView listView;

    public FragmentD() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_d,container,false);


        setListView();

        //adapter 새로고침
        loadAdapter();

        add_lecture_edit = (EditText) view.findViewById(R.id.add_lecture_edit);

        setAddLectureBtn();

        return view;
    }

    private void setAddLectureBtn() {
        add_lecture = (Button) view.findViewById(R.id.add_lecture_btn);//강의 추가하기 버튼
        add_lecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //강의 추가 버튼 누르면 Jsoup으로 해당강의가 존재하는지 검색 있으면 바로 추가, 없으면 에러 메시지 띄움
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });
    }

    private void setListView()
    {
        listView=(ListView)view.findViewById(R.id.lecturelist_d);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //해당 item 삭제
                final Lecture lecture = (Lecture) adapter.getItem(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(lecture.getCode() + " " +lecture.getTitle());
                builder.setMessage("해당 강의를 삭제하시겠습니까?");
                builder.setPositiveButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                deleteLecture_DB(lecture.getCode());
                                Toast.makeText(getContext(),"삭제하였습니다.",Toast.LENGTH_SHORT).show();

                            }
                        });

                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });
    }

    //새로고침
    public void loadAdapter()
    {
        if(adapter!=null)
            adapter = null;
        adapter=new LectureAdapter();
        listView.setAdapter(adapter);

        SQLiteDatabase db = getDatabase();

        db.execSQL("CREATE TABLE IF NOT EXISTS lecture_mark"
                +"(code TEXT PRIMARY KEY);");

        Cursor cursor=db.rawQuery("SELECT * FROM lecture_mark",null);

        //존재하는 lecture가 없으면 바로 종료
        if(cursor.getCount() == 0)
            return;

        codes = new ArrayList<>();
        while(cursor.moveToNext()){
            String code=cursor.getString(0);

            codes.add(code);
        }

        SelectAsyncTask selectAsyncTask = new SelectAsyncTask();
        selectAsyncTask.execute();

        if(db!=null)
            db.close();
    }

    private SQLiteDatabase getDatabase() {
        helper = new MyDBHelper(getContext(), "lecture_mark.db", null, 1);
        return helper.getReadableDatabase();
    }

    //db에 lecture 추가
    public void addLecture_DB(String code)
    {
        SQLiteDatabase db = getDatabase();


        //     db.execSQL("DROP TABLE IF EXISTS lecture;");
        db.execSQL("CREATE TABLE IF NOT EXISTS lecture_mark"
                +"(code TEXT PRIMARY KEY);");

        db.execSQL("REPLACE INTO lecture_mark (code) VALUES('" + code + "')");

        if(db!=null)
            db.close();
    }

    //강의 삭제
    public void deleteLecture_DB(String code)
    {
        SQLiteDatabase db = getDatabase();

        db.execSQL("DELETE FROM lecture_mark WHERE code = '" + code + "';");
        loadAdapter();//새로고침

        if(db!=null)
            db.close();
    }


    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                current_subj_cde = add_lecture_edit.getText().toString();
                url = "http://my.knu.ac.kr/stpo/stpo/cour/lectReqCntEnq/list.action?lectReqCntEnq.search_subj_cde=" + current_subj_cde.substring(0, 7) + "&lectReqCntEnq.search_sub_class_cde=" + current_subj_cde.substring(7) + "&searchValue=" + current_subj_cde + "";

                    final Document doc = Jsoup.connect(url).get();

                    System.out.println("main url : " + url);


                    final Elements titles = doc.select("td.subj_class_cde");//과목코드


                    //해당 강의 존재
                    if (titles.hasText())
                    {
                        //DB 리스트에 추가
                        addLecture_DB(titles.get(0).text());

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //adater 새로고침
                                loadAdapter();
                                Toast.makeText(getContext(),"추가 되었습니다.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {//해당강의 존재 x
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"존재하지않는 과목코드입니다. (대소문자를 구분해주세요)",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }


                }
                catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        Toast.makeText(getContext(), "에러발생", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                return  null;

        }
    }

    private class SelectAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                for(int i=0;i<codes.size();i++)
                {
                    current_subj_cde = codes.get(i);
                    url = "http://my.knu.ac.kr/stpo/stpo/cour/lectReqCntEnq/list.action?lectReqCntEnq.search_subj_cde=" + current_subj_cde.substring(0, 7) + "&lectReqCntEnq.search_sub_class_cde=" + current_subj_cde.substring(7) + "&searchValue=" + current_subj_cde + "";

                    final Document doc = Jsoup.connect(url).get();

                    //System.out.println("main url : " + url);


                    final Elements titles = doc.select("td.subj_class_cde");//과목코드
                    final Elements titles2 = doc.select("td.subj_nm");//과목이름
                    final Elements titles3 = doc.select("td.unit");//학점
                    final Elements titles4 = doc.select("td.prof_nm");//강의교수
                    final Elements titles5 = doc.select("td.lect_wk_tm");//강의시간
                    final Elements titles6 = doc.select("td.lect_quota");//수강정원
                    final Elements titles7 = doc.select("td.lect_req_cnt");//수강신청인원

                    if(titles.hasText())
                    {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int color =0;

                                if(Integer.parseInt(titles6.get(0).text()) > Integer.parseInt(titles7.get(0).text())) // 수강정원 > 신청현황 => 신청가능
                                    color = Color.GREEN;
                                else//신청불가
                                    color = Color.RED;

                                adapter.addItem(titles.get(0).text(), titles2.get(0).text(), titles3.get(0).text(), titles4.get(0).text(), titles5.get(0).text(), titles6.get(0).text(), titles7.get(0).text(), color);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                    else//존재하지 않으면 삭제
                    {
                        deleteLecture_DB(current_subj_cde);
                    }


                }

            }
            catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "에러발생", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return  null;

        }
    }
}
