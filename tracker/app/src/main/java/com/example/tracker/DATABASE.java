package com.example.tracker;


import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {User.class},version =  1)
public abstract class DATABASE extends RoomDatabase {
    public abstract userDao getUserDao();
}
