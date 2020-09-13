package com.jun.vacancyclassroom.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class LectureRoom implements Comparable{

    @PrimaryKey
    @NonNull
    public String lecture_room = "";

    public LectureRoom(String lecture_room) {
        this.lecture_room = lecture_room;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        LectureRoom other = (LectureRoom)obj;
        if(this.lecture_room.equals(other.lecture_room))
            return true;
        return false;
    }



    @Ignore
    public static DiffUtil.ItemCallback<LectureRoom> DIFF_CALLBACK = new  DiffUtil.ItemCallback<LectureRoom>() {
        @Override
        public boolean areItemsTheSame(@NonNull LectureRoom oldItem, @NonNull LectureRoom newItem) {
            return oldItem.lecture_room.equals(newItem.lecture_room);
        }

        @Override
        public boolean areContentsTheSame(@NonNull LectureRoom oldItem, @NonNull LectureRoom newItem) {
            return oldItem.lecture_room.equals(newItem.lecture_room);
        }

    };

    @Override
    public int compareTo(Object o) {

        return lecture_room.compareTo(((LectureRoom) o).lecture_room);
    }
}
