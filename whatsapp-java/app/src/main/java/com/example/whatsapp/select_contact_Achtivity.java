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
import androidx.lifecycle.ViewModelProviders;

import com.example.whatsapp.MVVM_conversations.Conversation;
import com.example.whatsapp.MVVM_conversations.ConversationViewModel;
import com.example.whatsapp.MVVM_Chat.Chat_Activity;
import com.example.whatsapp.MVVM_Chat.Message;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class select_contact_Achtivity extends AppCompatActivity {
    private ConversationViewModel viewmodel;
    ConstraintLayout new_contact ;
    ViewGroup viewGroup;
    View user;
    TextView conversation_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);

        viewmodel = ViewModelProviders.of(this).get(ConversationViewModel.class);
        viewmodel.set_my_number(getIntent().getStringExtra("mynumber"));

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
                        EditText userNameEditText = view.findViewById(R.id.username_new_user);
                        EditText userNumberEditText = view.findViewById(R.id.usernumber_new_user);
                        final String UserNumber = userNumberEditText.getText().toString();
                        final String UserName = userNameEditText.getText().toString();
                        if(UserNumber.equals("") == false && UserName.equals("") == false)
                            if(viewmodel.Get_Conversation(UserNumber) == null) {
                                Conversation conversation = new Conversation(UserNumber,"",UserName,"");
                                viewmodel.Create_Conversation(conversation);
                                Message.create_message_table(UserNumber);
                            }
                        conversation_name.setText(UserName);
                        user.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(v.getContext(), Chat_Activity.class);
                                intent.putExtra("conversationNumber",UserNumber);
                                intent.putExtra("conversationName",UserName);

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
