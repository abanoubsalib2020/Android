package com.example.whatsapp.MVVM_Chat;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {
    private MessageRepository messageRepository;
    private LiveData<List<Message>> Messages;

    public MessageViewModel (Application application)
    {
        super(application);
        messageRepository = new MessageRepository(application);
    }



    public LiveData<List<Message>> getMessages(String TargetNumber) {
        this.Messages = messageRepository.getMessages(TargetNumber);
        return Messages;
    }

    public void Send_Message(String msg)
    {
        messageRepository.Send_Message(msg);
    }

    public void set_my_number(String my_number)
    {
        messageRepository.set_my_number(my_number);
    }

    public void set_Target_number(String TargetNumber){
        messageRepository.set_Target_number(TargetNumber);

    }


}
