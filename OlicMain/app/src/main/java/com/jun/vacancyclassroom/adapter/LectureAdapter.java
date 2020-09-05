package com.jun.vacancyclassroom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.vacancyclassroom.R;
import com.jun.vacancyclassroom.item.Lecture;

import java.util.ArrayList;

public class LectureAdapter extends BaseAdapter {

    private ArrayList<Lecture> lectureList = new ArrayList<Lecture>() ;
    private Button color_button;

    public  LectureAdapter(){

    }
    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return lectureList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lecturelist_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        color_button = (Button) convertView.findViewById(R.id.lecture_button);
        TextView lecture_code = (TextView) convertView.findViewById(R.id.lecture_code);
        TextView lecture_name = (TextView) convertView.findViewById(R.id.lecture_name);
        TextView lecture_quota = (TextView) convertView.findViewById(R.id.lecture_quota);
        TextView req_quota = (TextView) convertView.findViewById(R.id.req_quota);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Lecture lecture = lectureList.get(position);



        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return lectureList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String code, String title,String unit , String prof_nm, String time,String lecture_quota, String req_quota, int color) {


    }

}
