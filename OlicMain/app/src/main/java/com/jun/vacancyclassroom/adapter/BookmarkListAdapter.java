package com.jun.vacancyclassroom.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.databinding.BookmarklistItemBinding;
import com.jun.vacancyclassroom.Myapplication;
import com.jun.vacancyclassroom.activity.TimeTableActivity;
import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.database.MyViewHolder;

import com.jun.vacancyclassroom.model.LectureRoom;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BookmarkListAdapter extends ListAdapter<LectureRoom, MyViewHolder<BookmarklistItemBinding>> {

    //private MainViewModel viewModel;
    private Context context;

    private int hour, minute, day;

    private Myapplication myapplication;

    private static final String TAG = "BookmarkListAdapter";
    private MyDAO dao;

    public BookmarkListAdapter(Application application, Context context)
    {
        super(LectureRoom.DIFF_CALLBACK);
        this.dao = MyDatabase.getInstance(application).dao();
        this.context = context;

        myapplication = (Myapplication)application;

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar now = Calendar.getInstance(timeZone);
        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);
        day = now.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public void submitList(@Nullable List<LectureRoom> list) {
        Collections.sort(list);
        super.submitList(list);
    }

    //시간 설정
    public void setTime(int hour, int minute, int day)
    {
        this.hour = hour;
        this.minute = minute;
        this.day = day;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder<BookmarklistItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder<>(inflater.inflate(R.layout.bookmarklist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder<BookmarklistItemBinding> holder, int position) {
        holder.binding().setItem(getItem(position));

        Observable.create(emitter -> {
            emitter.onNext(dao.selectAllLectureTimesIn(getItem(position).lecture_room));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                    List<String> time_list = (List<String>)o;

                    //이용 가능 여부 판단
                    if(!myapplication.classification(time_list, day, hour, minute))
                        holder.binding().buttonBookmark.setBackgroundColor(Color.RED);
                    else
                        holder.binding().buttonBookmark.setBackgroundColor(Color.GREEN);
        });


        holder.itemView.setOnClickListener( view -> {
            Intent intent = new Intent(context, TimeTableActivity.class);
            intent.putExtra("classroom", holder.binding().getItem().lecture_room);
            intent.putExtra("isBuilding",false);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return getCurrentList().size();
    }


}
