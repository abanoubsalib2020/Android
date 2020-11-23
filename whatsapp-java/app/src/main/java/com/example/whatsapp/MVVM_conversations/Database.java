package com.example.whatsapp.MVVM_conversations;


import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;


@androidx.room.Database(entities = { Conversation.class},version =  1)
public abstract class Database extends RoomDatabase {
    public abstract ConversationDao getConversationDao();
    private static Database instance ;
    public static synchronized Database  getInstance(Context context)
    {
        if(instance == null)
        {
            instance = Room.databaseBuilder(context.getApplicationContext(), Database.class,"conversations").allowMainThreadQueries().build();
        }
        return instance;
    }
}
