package com.example.whatsapp.MVVM_Chat;


import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;

import com.example.whatsapp.MVVM_conversations.Database;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class MessageRepository {
    private String Mynumber;
    private String TargetNumber;
    private MutableLiveData<List<Message>> Live_Messages;
    private List<Message> Messages;
    private FirebaseFirestore db ;
    private Database instance;
    SimpleDateFormat formatter = new SimpleDateFormat("h:m a");

    public MessageRepository(Application application)
    {
        instance = Database.getInstance(application);
        db =  FirebaseFirestore.getInstance();
        Live_Messages = new MutableLiveData<List<Message>>();
    }

    public MutableLiveData<List<Message>> getMessages(String TargetNumber) {
        Messages = get_all_messages(TargetNumber);
        update_changes();
        return Live_Messages;
    }

    public void set_my_number(String my_number)
    {
        this.Mynumber = my_number ;

    }

    public void set_Target_number(String TargetNumber){
        this.TargetNumber = TargetNumber;
        receive_messages();
    }


    void receive_messages() {
        db.collection("users")
                .document(Mynumber).collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            QueryDocumentSnapshot doc = dc.getDocument();

                            String conversationNumber = doc.getString("conversationNumber");
                            String msg = doc.getString("msg");
                            Date Time = doc.getTimestamp("sentTime").toDate();

                            switch (dc.getType()) {
                                case ADDED:
                                    if (conversationNumber.equals(TargetNumber) && doc.get("seen") == null) {
                                        Message message = new Message(msg, TargetNumber, formatter.format(Time));

                                       new_message(message);
                                        Insert_message_to_table(TargetNumber, message);

                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("seen", FieldValue.serverTimestamp());
                                        db.collection("users")
                                                .document(Mynumber).collection("messages")
                                                .document(doc.getId()).update(updates);

                                    }
                                    break;
                            } } }});
    }

    public void Send_Message(String msg)
    {

        DocumentReference new_message = db.collection("users").document(TargetNumber)
                .collection("messages").document();

        Message message = new Message(msg,"me",formatter.format(new Date()));

        Map<String, Object> data = new HashMap<>();
        data.put("msg", msg);
        data.put("sentTime", FieldValue.serverTimestamp());
        data.put("conversationNumber", Mynumber);
        new_message.set(data);
        new_message(message);
        Insert_message_to_table(TargetNumber,message);

        //MainActivity.database.getConversationDao().Update_the_last_message(msg, TargetNumber,formatter.format(new Date()));


    }

    private void Insert_message_to_table(String conversationNumber, Message message)
    {
        new Insert_Message_asyncTask(conversationNumber).execute(message);
    }

    private static class Insert_Message_asyncTask extends AsyncTask<Message, Void, Void>
    {
        private  String conversationNumber;
        public Insert_Message_asyncTask(String Target_number)
        {
            this. conversationNumber = Target_number;
        }

        @Override
        protected Void doInBackground(Message... messages) {
            Message.Insert_message_to_table( conversationNumber,messages[0]);
            return null;
        }
    }

    private List<Message>  get_all_messages(String conversationNumber)
    {
        return  new get_all_messages_asyncTask(conversationNumber,instance).doInBackground();
    }

    private static class get_all_messages_asyncTask extends AsyncTask<Void,Void,List<Message>>
    {
        private  String conversationNumber;
        private Database instance ;
        public get_all_messages_asyncTask(String Target_number,Database instance )
        {
            this. conversationNumber = Target_number;
        }

        @Override
        protected List<Message> doInBackground(Void... voids) {
            return Message.get_all_messages(conversationNumber);
        }
    }


      private void  new_message(Message message)
      {
          Messages.add(message);
          update_changes();
      }


      private  void update_changes()
      {
          Live_Messages.postValue(Messages);
      }




}
