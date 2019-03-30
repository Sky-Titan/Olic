package com.example.vacancyclassroom.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.item.BookMarkItem;
import com.example.vacancyclassroom.item.SearchItem;

import java.util.ArrayList;

public class BookMarkAdapter extends BaseAdapter {

    private ArrayList<BookMarkItem> bookmarkItemList = new ArrayList<BookMarkItem>() ;
    private Button color_button;
    public BookMarkAdapter(){

    }
    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return bookmarkItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bookmarklist_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        color_button = (Button) convertView.findViewById(R.id.button_bookmark);
        TextView classroom_name = (TextView) convertView.findViewById(R.id.classroom_name_bookmark);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        BookMarkItem bookmarkItem = bookmarkItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영

        classroom_name.setText(bookmarkItem.getClassroom());
        color_button.setBackgroundColor(bookmarkItem.getButton_color());

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
        return bookmarkItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String classroom, String time, int color) {
        BookMarkItem item = new BookMarkItem();

        item.setClassroom(classroom);
        item.setTime(time);
        item.setButton_color(color);
        bookmarkItemList.add(item);
    }

}
