package com.example.whatsapp.message;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.MainActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.conversation.Conversation;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class Chat_Activity extends AppCompatActivity {
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public chatAdapter ChatAdapter;


    private MaterialToolbar materialToolbar;
    private String TargetNumber  ;
    private EditText editText;
    private FloatingActionButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        recyclerView = findViewById(R.id.ChatRecyclerview);
        ChatAdapter = new chatAdapter();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(ChatAdapter);


        TargetNumber = getIntent().getStringExtra("conversationNumber");

        materialToolbar = findViewById(R.id.topAppBar);
        materialToolbar.setSubtitle("");

        Conversation temp = MainActivity.database.getConversationDao().get_Certain_conversations(TargetNumber);
        if(temp.conversationName == null)
             materialToolbar.setTitle(TargetNumber);
        else materialToolbar.setTitle(temp.conversationName);



        ChatAdapter.reload(TargetNumber);
        recyclerView.smoothScrollToPosition(20000);


        receive_messages();
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                if(msg.equals("") == false) {
                    send_message(msg);
                    editText.setText("");
                }

            }});


    }

    void send_message(String msg)
    {

        DocumentReference new_message = MainActivity.db.collection("users").document(TargetNumber)
                .collection("messages").document();

        Message message = new Message(msg,"me",formatter.format(new Date()));
        Message.Insert_message_to_table(TargetNumber,message);
        ChatAdapter.new_message(message);

        Map<String, Object> data = new HashMap<>();
        data.put("msg", msg);
        data.put("sentTime", FieldValue.serverTimestamp());
        data.put("conversationNumber", MainActivity.Mynumber);
        new_message.set(data);

        MainActivity.database.getConversationDao().Update_the_last_message(msg, TargetNumber,formatter.format(new Date()));



        recyclerView.smoothScrollToPosition(20000);

    }
    void receive_messages()
    {
        MainActivity.db.collection("users")
                .document(MainActivity.Mynumber).collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }


                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    QueryDocumentSnapshot doc = dc.getDocument();

                    String conversationNumber =  doc.getString("conversationNumber");
                    String msg                =  doc.getString("msg");
                    Date Time                 = doc.getTimestamp("sentTime").toDate();


                    Log.d("abanoub" , dc.getType().toString());
                    switch (dc.getType()){
                        case ADDED:
                            if(conversationNumber.equals(TargetNumber) && doc.get("seen") == null)
                            {
                                Message message = new Message(msg,TargetNumber,formatter.format(Time));
                                Message.Insert_message_to_table(TargetNumber,message);
                                ChatAdapter.new_message(message);
                                recyclerView.smoothScrollToPosition(20000);

                                Map<String,Object> updates = new HashMap<>();
                                updates.put("seen", FieldValue.serverTimestamp());
                                MainActivity.db.collection("users")
                                        .document(MainActivity.Mynumber).collection("messages")
                                        .document(doc.getId()).update(updates);


                            }
                            break;


                    }

                }



            }

        });

    }
}
