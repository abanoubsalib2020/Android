package com.example.whatsapp.conversation;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.MainActivity;
import com.example.whatsapp.R;
import com.example.whatsapp.message.Chat_Activity;

import java.util.Arrays;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    public static class ConversationViewHolder extends RecyclerView.ViewHolder
    {
        public ConstraintLayout conversationEntry;
        public TextView Name  ;
        public TextView LastMessage;
        public TextView LastMessageTime;
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            conversationEntry = itemView.findViewById(R.id.Conversation_Layout);
            Name =  itemView.findViewById(R.id.Conversation_Name);
            LastMessage =  itemView.findViewById(R.id.Conversation_LastMessage);
            LastMessageTime =  itemView.findViewById(R.id.text);

            conversationEntry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Conversation current = (Conversation)v.getTag();
                    Intent intent = new Intent(v.getContext(), Chat_Activity.class);
                    intent.putExtra("conversationNumber",current.conversationNumber);
                    v.getContext().startActivity(intent);
                }
            });
        }

    }
    List<Conversation> conversations =  Arrays.asList(

    );

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversatione_entry, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.itemView.setTag(conversations.get(position));
        if(conversations.get(position).conversationName == null )
            holder.Name.setText( conversations.get(position).conversationNumber);
        else holder.Name.setText( conversations.get(position).conversationName);

        if (conversations.get(position).lastMessage.length() > 20)
            holder.LastMessage.setText(conversations.get(position).lastMessage.substring(0,20));
            else
            holder.LastMessage.setText(conversations.get(position).lastMessage);

        holder.LastMessageTime.setText(conversations.get(position).lastMessageTime);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }
    public void reload()
    {
        conversations =  MainActivity.database.getConversationDao().getAllConversations();
        notifyDataSetChanged();
    }

}
