package com.example.whatsapp.MVVM_conversations;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ConversationDao {

    @Insert
    public void Create_Conversation(Conversation conversation);
    @Update
    public void Update_Conversation(Conversation conversation);
    @Query("SELECT * FROM conversation")
    public LiveData<List<Conversation>> getAllConversations();

    @Query("SELECT * FROM Conversation WHERE conversationNumber = :conversationNumber")
    public Conversation get_Certain_conversations(String conversationNumber);
}
