package com.jun.vacancyclassroom.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Room;

@Entity
public class Building {

    @PrimaryKey
    @NonNull
    public String buildingName;

    public Building(String buildingName) {
        this.buildingName = buildingName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Building other = (Building)obj;

        if(buildingName.equals(other.buildingName))
            return true;
        return false;
    }

    @Ignore
    public static DiffUtil.ItemCallback<Building> DIFF_CALLBACK = new  DiffUtil.ItemCallback<Building>() {
        @Override
        public boolean areItemsTheSame(@NonNull Building oldItem, @NonNull Building newItem) {

            return oldItem.buildingName.equals(newItem.buildingName);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Building oldItem, @NonNull Building newItem) {
            return oldItem.buildingName.equals(newItem.buildingName);
        }
    };
}
