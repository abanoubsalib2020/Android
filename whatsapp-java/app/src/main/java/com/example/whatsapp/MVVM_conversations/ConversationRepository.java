package com.example.whatsapp.MVVM_conversations;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.whatsapp.MVVM_Chat.Message;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class ConversationRepository {
    private ConversationDao conversationDao;
    private LiveData<List<Conversation>> conversations;
    private  FirebaseFirestore db ;
    SimpleDateFormat formatter = new SimpleDateFormat("h:m a");
    private String Mynumber;
    public static Database instance;

    public ConversationRepository(Application application)
    {
         instance = Database.getInstance(application);
        this.conversationDao = instance.getConversationDao();
        conversations = conversationDao.getAllConversations();
        db =  FirebaseFirestore.getInstance();
    }

    public LiveData<List<Conversation>> getConversations() {
        return conversations;
    }



    public void Create_Conversation(Conversation conversation)
    {
        conversationDao.Create_Conversation(conversation);
    }
    public void Update_Conversation(Conversation conversation)
    {
        conversationDao.Update_Conversation(conversation);
    }

    public Conversation Get_Conversation(String Conversation_Number)
    {
        return conversationDao.get_Certain_conversations(Conversation_Number);
    }



    public void set_my_number(String my_number)
    {
        this.Mynumber = my_number ;
        Firebase_create_or_update_conversations();
    }



    private void Firebase_create_or_update_conversations()
    {
        db.collection("users").document(Mynumber)
                .collection("messages").orderBy("sentTime")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {

                            switch (dc.getType()) {
                                case ADDED:
                                    String conversationNumber = dc.getDocument().getString("conversationNumber");
                                    String lastMessage = dc.getDocument().getString("msg");
                                    Date Time                 = dc.getDocument().getTimestamp("sentTime").toDate();
                                    Conversation temp = Get_Conversation(conversationNumber);

                                    if (temp == null)
                                    {
                                        Log.d("temp", "temp");
                                        Message.create_message_table(conversationNumber);
                                        temp = new Conversation(conversationNumber,lastMessage,"",formatter.format(Time));
                                        Create_Conversation(temp);
                                    }else
                                    {
                                        temp.setLastMessage( lastMessage );
                                        temp.setLastMessageTime(formatter.format(Time));
                                        Update_Conversation(temp);
                                    }
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }




                    }

                });
    }



}
