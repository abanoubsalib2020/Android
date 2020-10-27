package com.example.whatsapp.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.R;

import java.util.Arrays;
import java.util.List;

public class chatAdapter extends RecyclerView.Adapter<chatAdapter.messageViewHolder> {

    public static class  messageViewHolder extends RecyclerView.ViewHolder
    {

        public LinearLayout left;
        public LinearLayout right;

        public TextView messageL ;
        public TextView messageR ;
        public TextView timeL ;
        public TextView timeR ;


        public messageViewHolder(@NonNull View itemView) {
            super(itemView);

            left = itemView.findViewById(R.id.leftPart);
            right = itemView.findViewById(R.id.rightPart);

            messageL = itemView.findViewById(R.id.messageL);
            messageR = itemView.findViewById(R.id.messageR);

            timeL = itemView.findViewById(R.id.timeL);
            timeR = itemView.findViewById(R.id.timeR);

        }
    }

    List<Message> Messages = Arrays.asList();


    @NonNull
    @Override
    public messageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatentry, parent, false);
        return new messageViewHolder(view );

    }

    @Override
    public void onBindViewHolder(@NonNull messageViewHolder holder, int position) {

        if(Messages.get(position).MessageWriter.equals("me"))
        {
            holder.left.setVisibility(View.INVISIBLE);
            holder.messageR.setText( Messages.get(position).content);
            holder.timeR.setText(Messages.get(position).Date);
        }else
        {
            holder.right.setVisibility(View.INVISIBLE);
            holder.messageL.setText( Messages.get(position).content);
            holder.timeL.setText(Messages.get(position).Date);
        }

    }

    @Override
    public int getItemCount() {
        return Messages.size();
    }


    public void reload(String ConversationId)
    {
        Messages = Message.get_all_messages(ConversationId);
        notifyDataSetChanged();

    }

    public void new_message(Message message)
    {
        Messages.add(message);
        notifyDataSetChanged();
    }



}
