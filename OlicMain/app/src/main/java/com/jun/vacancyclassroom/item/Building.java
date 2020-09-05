package com.jun.vacancyclassroom.item;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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
}
