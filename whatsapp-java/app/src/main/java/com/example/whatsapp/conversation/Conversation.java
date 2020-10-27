package com.example.whatsapp.conversation;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "Conversations")
public class Conversation {
    @PrimaryKey
    public int id ;
    @ColumnInfo(name = "lastMessage" ) @Nullable
    public String lastMessage;
    @ColumnInfo(name = "conversationNumber" ) @Nullable
    public String conversationNumber  ;
    @ColumnInfo(name = "conversationName" )
    public String conversationName  ;
    @ColumnInfo(name = "lastMessageTime" )
    public String lastMessageTime ;
    // store conversation id  and create map between id and numbers

    public Conversation(String conversationNumber) {
        this.conversationNumber = conversationNumber;
    }
    public Conversation(){}
}
