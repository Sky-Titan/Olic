package com.jun.vacancyclassroom.adapter;


import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.ListAdapter;
import androidx.room.RoomDatabase;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.databinding.LectureroomlistItemBinding;
import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.database.MyViewHolder;
import com.jun.vacancyclassroom.item.BookMarkedRoom;
import com.jun.vacancyclassroom.item.LectureRoom;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class LectureRoomListAdapter extends ListAdapter<LectureRoom, MyViewHolder<LectureroomlistItemBinding>> {

    private MainViewModel viewModel;
    private MyDAO dao;
    private ExecutorService executorService;
    private String searchWord = "";

    private HashSet<String> bookmarkedSet = new HashSet<>();
    private ArrayList<LectureRoom> lectureRooms = new ArrayList<>();

    private static final String TAG = "LectureRoomListAdapter";

    public LectureRoomListAdapter(Application application, MainViewModel viewModel)
    {
        super(LectureRoom.DIFF_CALLBACK);
        dao = MyDatabase.getInstance(application).dao();
        this.viewModel = viewModel;
        executorService = Executors.newSingleThreadExecutor();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder<>(inflater.inflate(R.layout.lectureroomlist_item, parent, false));
    }

    //검색어 지정
    public void setSearchWord(String searchWord)
    {
        this.searchWord = searchWord;
        notifyDataSetChanged();
    }

    //북마크 변경
    public void setBookmarkedSet(List<String> bookmarkedSet)
    {
        this.bookmarkedSet.clear();
        this.bookmarkedSet.addAll(bookmarkedSet);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder<LectureroomlistItemBinding> holder, int position) {

        Log.i(TAG, "onBindViewHolder : "+getItem(position).lecture_room+" search word : "+searchWord);

        LectureRoom lectureRoom = getItem(position);
        holder.binding().setLectureRoom(lectureRoom);


        //북마크 목록에 있음
        if(bookmarkedSet.contains(holder.binding().getLectureRoom().lecture_room))
            holder.binding().checkBox1.setChecked(true);
        else
            holder.binding().checkBox1.setChecked(false);

        holder.itemView.setOnClickListener(view -> {

            if(!holder.binding().checkBox1.isChecked())
            {
                Log.i(TAG, "북마크 추가 "+ holder.binding().getLectureRoom().lecture_room);
                holder.binding().checkBox1.setChecked(true);
                bookmarkedSet.add(holder.binding().getLectureRoom().lecture_room);
                viewModel.addBookMarkedRoom(new BookMarkedRoom(holder.binding().getLectureRoom().lecture_room));
            }
            else
            {
                Log.i(TAG, "북마크 삭제 "+holder.binding().getLectureRoom().lecture_room);
                holder.binding().checkBox1.setChecked(false);
                bookmarkedSet.remove(holder.binding().getLectureRoom().lecture_room);
                viewModel.removeBookMarkedRoom(new BookMarkedRoom(holder.binding().getLectureRoom().lecture_room));
            }
        });
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }

}
