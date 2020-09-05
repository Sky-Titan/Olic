package com.jun.vacancyclassroom.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Room;

@Entity
public class BookMarkedRoom {

    @PrimaryKey
    @NonNull
    @ForeignKey(entity = LectureRoom.class, parentColumns = "lecture_room", childColumns = "lecture_room")
    public String lecture_room = "";

    public BookMarkedRoom(String lecture_room) {
        this.lecture_room = lecture_room;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        BookMarkedRoom other = (BookMarkedRoom) obj;

        if(this.lecture_room.equals(other.lecture_room))
            return true;
        return false;
    }

    @Ignore
    public static DiffUtil.ItemCallback<BookMarkedRoom> DIFF_CALLBACK = new  DiffUtil.ItemCallback<BookMarkedRoom>() {
        @Override
        public boolean areItemsTheSame(@NonNull BookMarkedRoom oldItem, @NonNull BookMarkedRoom newItem) {
            return oldItem.lecture_room.equals(newItem.lecture_room);
        }

        @Override
        public boolean areContentsTheSame(@NonNull BookMarkedRoom oldItem, @NonNull BookMarkedRoom newItem) {
            return oldItem.lecture_room.equals(newItem.lecture_room);
        }

    };
}
