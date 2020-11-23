package com.example.whatsapp.MVVM_conversations;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.R;
import com.example.whatsapp.MVVM_Chat.Chat_Activity;

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
                    intent.putExtra("conversationNumber",current.getConversationNumber());
                    intent.putExtra("conversationName",current.getConversationName());
                    v.getContext().startActivity(intent);
                }
            });
        }

    }
    private List<Conversation> conversations =  Arrays.asList();

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversatione_entry, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        holder.itemView.setTag(conversations.get(position));
        if(conversations.get(position).getConversationName() == null || conversations.get(position).getConversationName().equals("") )
            holder.Name.setText( conversations.get(position).getConversationNumber());
        else holder.Name.setText( conversations.get(position).getConversationName());


            holder.LastMessage.setText(conversations.get(position).getLastMessage());
            holder.LastMessageTime.setText(conversations.get(position).getLastMessageTime());
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void setConversations(List<Conversation> conversations)
    {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

}
