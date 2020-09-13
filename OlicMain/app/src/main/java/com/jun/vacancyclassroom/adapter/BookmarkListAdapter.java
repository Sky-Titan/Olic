package com.jun.vacancyclassroom.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import com.example.vacancyclassroom.R;
import com.example.vacancyclassroom.databinding.BookmarklistItemBinding;
import com.jun.vacancyclassroom.activity.TimeTableActivity;
import com.jun.vacancyclassroom.database.MyDAO;
import com.jun.vacancyclassroom.database.MyDatabase;
import com.jun.vacancyclassroom.database.MyViewHolder;

import com.jun.vacancyclassroom.model.BookMarkedRoom;
import com.jun.vacancyclassroom.viewmodel.MainViewModel;

import java.util.Calendar;
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
        boolean isPossible = true;

        String day_today = dayToKorean(day_n);//현재요일
        String hour_today = String.valueOf(hour_n);//현재시간
        String minute_today =String.valueOf(minute_n);//현재분

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

                    //숫자로변경
                    int before_hour_num = Integer.parseInt(before_hour);
                    int hour_today_num = Integer.parseInt(hour_today);
                    int after_hour_num = Integer.parseInt(after_hour);
                    int before_minute_num = Integer.parseInt(before_minute);
                    int minute_today_num = Integer.parseInt(minute_today);
                    int after_minute_num = Integer.parseInt(after_minute);

                    //현재 강의실 이용가능 한지 구분 시작
                    //요일이 같으면 그다음 단계
                    if (day.equals(day_today))
                    {
                        if (before_hour_num < hour_today_num && hour_today_num < after_hour_num)//현재시간이 사이에 있다면 이용불가
                            isPossible = false;
                        else if (before_hour_num > hour_today_num && hour_today_num > after_hour_num)//현재시간이 밖에 있다면 이용가능
                            isPossible = true;
                        else if (before_hour_num == hour_today_num)//before 시간과 같은 경우
                        {
                            //before 분과 비교
                            if (before_minute_num <= minute_today_num)//before minute보다 같거나 크면 이용불가
                                isPossible = false;
                            //작다면 이용가능
                            else
                                isPossible = true;
                        }
                        else if (after_hour_num == hour_today_num)//after 시간과 같은 경우
                        {
                            if (after_minute_num >= minute_today_num)//after minute보다 같거나 작으면 이용불가
                                isPossible = false;
                            else
                                isPossible = true;
                        }
                    }
                    else//요일다르면 그냥 다음단계로 넘어감
                        isPossible = true;
                }

                if (isPossible == false)//하나라도 이용불가라면 종료
                    return isPossible;
            }
        }

        return isPossible;

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
