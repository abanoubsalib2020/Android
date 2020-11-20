package com.example.tracker.User_MVVM;


import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {User.class},version =  1)
public abstract class DATABASE extends RoomDatabase {
    public abstract UserDao getUserDao();
    private static DATABASE instance ;

    public static synchronized DATABASE getInstance(Context context) {
        if(instance == null)
        {
           instance =  Room.databaseBuilder(context.getApplicationContext(),DATABASE.class,"users").allowMainThreadQueries().build();
        }
        return instance;
    }
}
