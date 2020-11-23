package com.example.whatsapp.MVVM_Chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.MVVM_conversations.MainActivity;
import com.example.whatsapp.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.List;


public class Chat_Activity extends AppCompatActivity {
    private MessageViewModel Chatviewmodel;
    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public chatAdapter ChatAdapter;


    private MaterialToolbar materialToolbar;
    private String TargetNumber  ;
    private String TargetName;
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
        TargetName = getIntent().getStringExtra("conversationName");

        Chatviewmodel = ViewModelProviders.of(this).get(MessageViewModel.class);
        Chatviewmodel.set_my_number(MainActivity.Mynumber);
        Chatviewmodel.set_Target_number(TargetNumber);
        Chatviewmodel.getMessages(TargetNumber).observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                ChatAdapter.set_Messages(messages);
                recyclerView.smoothScrollToPosition(20000);
            }
        });


        materialToolbar = findViewById(R.id.topAppBar);
        materialToolbar.setSubtitle("");
        if(TargetName.equals("") == true || TargetName == null )
             materialToolbar.setTitle(TargetNumber);
        else materialToolbar.setTitle(TargetName);



        editText = findViewById(R.id.editText);
        button = findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editText.getText().toString();
                if(msg.equals("") == false) {
                    Chatviewmodel.Send_Message(msg);
                    editText.setText("");
                } }}); }


}
