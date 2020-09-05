package com.jun.vacancyclassroom.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Lecture {

    @PrimaryKey
    @NonNull
    public String lecture_code="";
    public String lecture_name="";
    public String lecture_credit="";//학점
    public String professor="";//강의교수
    public String quota="";//수강정원
    public String req_cnt="";//수강신청인원
    public String lecture_room="";
    public String lecture_time="";


    public Lecture()
    {

    }

    public Lecture(String lecture_code, String lecture_name, String lecture_credit, String professor, String quota, String req_cnt, String lecture_room, String lecture_time) {
        this.lecture_code = lecture_code;
        this.lecture_name = lecture_name;
        this.lecture_credit = lecture_credit;
        this.professor = professor;
        this.quota = quota;
        this.req_cnt = req_cnt;
        this.lecture_room = lecture_room;
        this.lecture_time = lecture_time;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        Lecture other = (Lecture) obj;

        if(lecture_code.equals(other.lecture_code))
            return true;
        return false;

    }

    @Ignore
    public static DiffUtil.ItemCallback<Lecture> DIFF_CALLBACK = new  DiffUtil.ItemCallback<Lecture>() {
        @Override
        public boolean areItemsTheSame(@NonNull Lecture oldItem, @NonNull Lecture newItem) {
            return oldItem.lecture_code.equals(newItem.lecture_code);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Lecture oldItem, @NonNull Lecture newItem) {
            return oldItem.equals(newItem);
        }

    };

}
