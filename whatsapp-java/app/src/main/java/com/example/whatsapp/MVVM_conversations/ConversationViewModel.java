package com.example.whatsapp.MVVM_conversations;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.whatsapp.MVVM_conversations.Conversation;
import com.example.whatsapp.MVVM_conversations.ConversationRepository;

import java.util.List;

public class ConversationViewModel extends AndroidViewModel {
    private ConversationRepository conversationRepository;
    private LiveData<List<Conversation>> conversations;


    public ConversationViewModel(@NonNull Application application)
    {
        super(application);
        this.conversationRepository = new ConversationRepository(application);
        this.conversations = conversationRepository.getConversations();

    }

    public LiveData<List<Conversation>> getConversations() {
        return conversations;
    }


    public void Create_Conversation(Conversation conversation)
    {
        conversationRepository.Create_Conversation(conversation);
    }
    public Conversation Get_Conversation(String Conversation_Number)
    {
        return conversationRepository.Get_Conversation(Conversation_Number);
    }
    public void set_my_number(String my_number)
    {
        conversationRepository.set_my_number(my_number);
    }


}
