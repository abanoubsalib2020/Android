package com.example.whatsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.whatsapp.message.Chat_Activity;
import com.example.whatsapp.message.Message;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class select_contact extends AppCompatActivity {
    ConstraintLayout new_contact ;
    ViewGroup viewGroup;
    View user;
    TextView conversation_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        new_contact = findViewById(R.id.new_content);
        new_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_new_contact();
            }
        });

        LayoutInflater inflater = getLayoutInflater();
        user =  inflater.inflate(R.layout.conversatione_entry, null);
        user.findViewById(R.id.text).setVisibility(View.INVISIBLE);
        conversation_name = user.findViewById(R.id.Conversation_Name);
        user.findViewById(R.id.Conversation_LastMessage).setVisibility(View.INVISIBLE);
         viewGroup = findViewById(R.id.ViewGroup);


    }


    void add_new_contact()
    {
        LayoutInflater inflater = getLayoutInflater();

        final View view =  inflater.inflate(R.layout.add_new_user, null);


        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle("Create new contact")
                .setMessage("Enter the contact's Information ")

                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText userName = view.findViewById(R.id.username_new_user);
                        final EditText userNumber = view.findViewById(R.id.usernumber_new_user);

                        if(userNumber.getText().toString().equals("") == false && userName.getText().toString().equals("") == false)
                            if(MainActivity.database.getConversationDao().get_Certain_conversations(userNumber.getText().toString()) == null) {
                                MainActivity.database.getConversationDao().Create_Conversation(userName.getText().toString(), userNumber.getText().toString(), null);
                                Message.create_message_table(userNumber.getText().toString());
                            }
                        conversation_name.setText(userName.getText().toString());
                        user.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), Chat_Activity.class);
                                intent.putExtra("conversationNumber",userNumber.getText().toString());
                                v.getContext().startActivity(intent);
                            }
                        });
                        viewGroup.addView(user);


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
}
