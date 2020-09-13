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
import com.jun.vacancyclassroom.activity.TimeTableActivity;
import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.database.MyViewHolder;

import com.jun.vacancyclassroom.model.BookMarkedRoom;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BookmarkListAdapter extends ListAdapter<BookMarkedRoom, MyViewHolder<BookmarklistItemBinding>> {

    //private MainViewModel viewModel;
    private Context context;

    private int hour, minute, day;

    private static final String TAG = "BookmarkListAdapter";
    private MyDAO dao;

    public BookmarkListAdapter(Application application, Context context)
    {
        super(BookMarkedRoom.DIFF_CALLBACK);
        this.dao = MyDatabase.getInstance(application).dao();
        this.context = context;

        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        Calendar now = Calendar.getInstance(timeZone);
        hour = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);
        day = now.get(Calendar.DAY_OF_WEEK);
    }

    @Override
    public void submitList(@Nullable List<BookMarkedRoom> list) {
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
                    if(!classification(time_list, getItem(position).lecture_room, day, hour, minute))
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

    //해당 교실이 현재 시간에 이용가능한지 판단
    private boolean classification(List<String> time_list, String lectureRoom, int day_n, int hour_n, int minute_n)
    {

        for(int k = 0;k < time_list.size();k++)
        {
            String time = time_list.get(k);

            String[] times = time.split(" ");

            String before_hour = "", before_minute = "";
            String day = "";
            String after_hour = "", after_minute = "";

            for (int i = 0; i < times.length; i++)
            {
                if (i % 3 == 0)//새로운 시간대 beforetime(시작시간)이랑 요일구하기
                {
                    //ex)화16:00
                    day = times[i].substring(0, 1);
                    before_hour = times[i].substring(1, 3);
                    before_minute = times[i].substring(4, 6);
                }
                else if (i % 3 == 2)//aftertime(종료시간) 구하기
                {
                    // ex)16:00
                    after_hour = times[i].substring(0, 2);
                    after_minute = times[i].substring(3, 5);

                    if(!isUsable(before_hour, before_minute, after_hour, after_minute, day))
                        return false;
                }

            }
        }

        return true;
    }

    //강의실 이용가능 여부 판단
    private boolean isUsable(String before_hour, String before_minute, String after_hour, String after_minute, String day)
    {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");

        Calendar before = Calendar.getInstance(timeZone);
        before.set(Calendar.DAY_OF_WEEK, dayToNum(day));
        before.set(Calendar.HOUR_OF_DAY, Integer.parseInt(before_hour));
        before.set(Calendar.MINUTE, Integer.parseInt(before_minute));
       // Log.d(TAG, "BEFORE : "+before.get(Calendar.DATE)+" "+dayToKorean(before.get(Calendar.DAY_OF_WEEK))+ " "+before.get(Calendar.HOUR_OF_DAY)+" "+before.get(Calendar.MINUTE));

        Calendar after = Calendar.getInstance(timeZone);
        after.set(Calendar.DAY_OF_WEEK, dayToNum(day));
        after.set(Calendar.HOUR_OF_DAY, Integer.parseInt(after_hour));
        after.set(Calendar.MINUTE, Integer.parseInt(after_minute));
       // Log.d(TAG, "AFTER : "+dayToKorean(after.get(Calendar.DAY_OF_WEEK))+ " "+after.get(Calendar.HOUR_OF_DAY)+" "+after.get(Calendar.MINUTE));

        Calendar now = Calendar.getInstance(timeZone);
        now.set(Calendar.DAY_OF_WEEK, this.day);
        now.set(Calendar.HOUR_OF_DAY, this.hour);
        now.set(Calendar.MINUTE, this.minute);

       // Log.d(TAG, "NOW : "+now.get(Calendar.DATE)+" "+dayToKorean(now.get(Calendar.DAY_OF_WEEK))+ " "+now.get(Calendar.HOUR_OF_DAY)+" "+now.get(Calendar.MINUTE));

        if( before.compareTo(now) <= 0 && after.compareTo(now) >= 0)
            return false;
        return true;
    }

    public int dayToNum(String day)
    {
        if (day.equals("월"))
            return 1;
        else if (day.equals("화"))
            return 2;
        else if (day.equals("수"))
            return 3;
        else if (day.equals("목"))
            return 4;
        else if (day.equals("금"))
            return 5;
        else if (day.equals("토"))
            return 6;
        else if (day.equals("일"))
            return 7;
        else
            return 0;
    }

    public String dayToKorean(int day) {

        if (day==2)
            return "월";
        else if (day==3)
            return "화";
        else if (day==4)
            return "수";
        else if (day==5)
            return "목";
        else if (day==6)
            return "금";
        else if (day==7)
            return "토";
        else if (day==1)
            return "일";
        else
            return "";
    }
}
