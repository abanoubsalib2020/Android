package com.example.whatsapp.MVVM_conversations;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Conversation {
    @PrimaryKey (autoGenerate = true)
    private int id ;
    @Nullable
    private String lastMessage;
    private String conversationNumber  ;
    @Nullable
    private String conversationName  ;
    @Nullable
    private String lastMessageTime ;



    public Conversation( String conversationNumber,@Nullable String lastMessage, @Nullable String conversationName, @Nullable String lastMessageTime) {
        this.lastMessage = lastMessage;
        this.conversationNumber = conversationNumber;
        this.conversationName = conversationName;
        this.lastMessageTime = lastMessageTime;
    }

    public void setLastMessage(@Nullable String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setConversationNumber(String conversationNumber) {
        this.conversationNumber = conversationNumber;
    }

    public void setConversationName(@Nullable String conversationName) {
        this.conversationName = conversationName;
    }

    public void setLastMessageTime(@Nullable String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Nullable
    public String getLastMessage() {
        return lastMessage;
    }

    public String getConversationNumber() {
        return conversationNumber;
    }

    @Nullable
    public String getConversationName() {
        return conversationName;
    }

    @Nullable
    public String getLastMessageTime() {
        return lastMessageTime;
    }


}
