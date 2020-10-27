package com.example.whatsapp.conversation;


import androidx.room.RoomDatabase;


@androidx.room.Database(entities = { Conversation.class},version =  1)
public abstract class Database extends RoomDatabase {
    public abstract ConversationDao getConversationDao();
}
