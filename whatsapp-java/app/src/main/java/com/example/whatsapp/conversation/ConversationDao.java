package com.example.whatsapp.conversation;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ConversationDao {

    @Query("SELECT * FROM Conversations")
    public List<Conversation> getAllConversations();
    @Query("SELECT * FROM Conversations WHERE conversationNumber = :conversationNumber")
    public Conversation get_Certain_conversations(String conversationNumber);

    @Query("UPDATE conversations SET lastMessage =:lastmessage , lastMessageTime = :lastmessageTime  WHERE conversationNumber = :conversationNumber ")
    public void Update_the_last_message(String lastmessage, String conversationNumber, String lastmessageTime);

    @Query ("INSERT INTO Conversations ('conversationNumber','lastMessage') VALUES (:conversationNumber,:lastmessage)")
    public void Create_Conversation(String conversationNumber, String lastmessage);

    @Query ("INSERT INTO Conversations ('conversationName','conversationNumber','lastMessage') VALUES (:conversationName,:conversationNumber,:lastmessage)")
    public void Create_Conversation(String conversationName, String conversationNumber, String lastmessage);



}
