package com.jun.vacancyclassroom.model;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class SearchLecture {

    @PrimaryKey
    @NonNull
    @ForeignKey(entity = Lecture.class, parentColumns = "lecture_code", childColumns = "lecture_code")
    public String lecture_code="";

    public SearchLecture(String lecture_code)
    {
        this.lecture_code = lecture_code;
    }

    @Ignore
    public static DiffUtil.ItemCallback<SearchLecture> DIFF_CALLBACK = new  DiffUtil.ItemCallback<SearchLecture>() {
        @Override
        public boolean areItemsTheSame(@NonNull SearchLecture oldItem, @NonNull SearchLecture newItem) {
            return oldItem.lecture_code.equals(newItem.lecture_code);
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchLecture oldItem, @NonNull SearchLecture newItem) {
            return oldItem.lecture_code.equals(newItem.lecture_code);
        }

    };
}
