package com.jun.vacancyclassroom;

import android.app.Application;
import android.util.Log;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Myapplication extends Application {

    private String currentSemester;

    public String getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(String currentSemester) {
        this.currentSemester = currentSemester;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //DatabaseLibrary.getInstance(getApplicationContext());
    }


    //해당 교실이 현재 시간에 이용가능한지 판단
    public boolean classification(List<String> time_list, int now_day, int now_hour, int now_min)
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
                if (times[i].length() == 6)//새로운 시간대 beforetime(시작시간)이랑 요일구하기
                {
                    //ex)화16:00
                   // Log.d(TAG, times[i]);
                    day = times[i].substring(0, 1);
                    before_hour = times[i].substring(1, 3);
                    before_minute = times[i].substring(4, 6);
                }
                else if (times[i].length() == 5)//aftertime(종료시간) 구하기
                {
                    // ex)16:00
                    after_hour = times[i].substring(0, 2);
                    after_minute = times[i].substring(3, 5);

                    if(!isUsable(before_hour, before_minute, after_hour, after_minute, day, now_day, now_hour, now_min))
                        return false;
                }

            }
        }

        return true;
    }

    //강의실 이용가능 여부 판단
    private boolean isUsable(String before_hour, String before_minute, String after_hour, String after_minute, String day, int now_day, int now_hour, int now_min)
    {
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");

        Calendar before = Calendar.getInstance(timeZone);
        before.set(Calendar.DAY_OF_WEEK, dayToNum(day));
        before.set(Calendar.HOUR_OF_DAY, Integer.parseInt(before_hour));
        before.set(Calendar.MINUTE, Integer.parseInt(before_minute));
        //Log.d(TAG, "BEFORE : "+before.get(Calendar.DATE)+" "+dayToKorean(before.get(Calendar.DAY_OF_WEEK))+ " "+before.get(Calendar.HOUR_OF_DAY)+" "+before.get(Calendar.MINUTE));

        Calendar after = Calendar.getInstance(timeZone);
        after.set(Calendar.DAY_OF_WEEK, dayToNum(day));
        after.set(Calendar.HOUR_OF_DAY, Integer.parseInt(after_hour));
        after.set(Calendar.MINUTE, Integer.parseInt(after_minute));
       // Log.d(TAG, "AFTER : "+dayToKorean(after.get(Calendar.DAY_OF_WEEK))+ " "+after.get(Calendar.HOUR_OF_DAY)+" "+after.get(Calendar.MINUTE));

        Calendar now = Calendar.getInstance(timeZone);
        now.set(Calendar.DAY_OF_WEEK, now_day);
        now.set(Calendar.HOUR_OF_DAY, now_hour);
        now.set(Calendar.MINUTE, now_min);

       // Log.d(TAG, "NOW : "+now.get(Calendar.DATE)+" "+dayToKorean(now.get(Calendar.DAY_OF_WEEK))+ " "+now.get(Calendar.HOUR_OF_DAY)+" "+now.get(Calendar.MINUTE));

        if( before.compareTo(now) <= 0 && after.compareTo(now) >= 0)
            return false;
        return true;
    }

    public int dayToNum(String day)
    {
        if (day.equals("일"))
            return 1;
        else if (day.equals("월"))
            return 2;
        else if (day.equals("화"))
            return 3;
        else if (day.equals("수"))
            return 4;
        else if (day.equals("목"))
            return 5;
        else if (day.equals("금"))
            return 6;
        else if (day.equals("토"))
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
