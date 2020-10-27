package com.example.whatsapp;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.whatsapp.R;
import com.example.whatsapp.conversation.Conversation;
import com.example.whatsapp.conversation.ConversationAdapter;
import com.example.whatsapp.conversation.Database;
import com.example.whatsapp.message.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    SimpleDateFormat formatter = new SimpleDateFormat("h:m a");

    public static Database database ;
    public static FirebaseFirestore db ;
    public static String Mynumber;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.ConversationRecyclerview);
        adapter = new ConversationAdapter();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);


        floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, select_contact.class);
                startActivity(intent);
            }
        });



        Mynumber = getIntent().getStringExtra("mynumber");

        database = Room.databaseBuilder(getApplicationContext(), Database.class,"messages").allowMainThreadQueries().build();
        adapter.reload();
        db =  FirebaseFirestore.getInstance();
        create_or_update_conversations();




    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.reload();
    }


    void create_or_update_conversations()
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
                                    Conversation temp = database.getConversationDao()
                                            .get_Certain_conversations(conversationNumber);
                                    if (temp == null)
                                    {
                                        Message.create_message_table(conversationNumber);
                                        database.getConversationDao()
                                                .Create_Conversation(conversationNumber,lastMessage);
                                        adapter.reload();
                                    }else
                                    {
                                        database.getConversationDao()
                                                .Update_the_last_message(lastMessage,conversationNumber,formatter.format(Time));
                                        adapter.reload();
                                    }
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }

                        adapter.reload();


                    }

                });
    }
}
