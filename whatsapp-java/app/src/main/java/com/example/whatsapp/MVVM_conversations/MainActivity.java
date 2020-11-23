package com.example.whatsapp.MVVM_conversations;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.R;
import com.example.whatsapp.select_contact_Achtivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;



    public static FirebaseFirestore db ;
    public static String Mynumber;
    private FloatingActionButton floatingActionButton;
    private ConversationViewModel viewmodel;
    public static Database database ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mynumber = getIntent().getStringExtra("mynumber");

        recyclerView = findViewById(R.id.ConversationRecyclerview);
        adapter = new ConversationAdapter();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        viewmodel = ViewModelProviders.of(this).get(ConversationViewModel.class);
        viewmodel.set_my_number(Mynumber);
        viewmodel.getConversations().observe(this, new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                adapter.setConversations(conversations);
            }
        });

        floatingActionButton = findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, select_contact_Achtivity.class);
                intent.putExtra("mynumber",Mynumber);
                startActivity(intent);
            }
        });





    }
}
