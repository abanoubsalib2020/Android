package com.example.tracker.User_MVVM;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tracker.MainActivity;
import com.example.tracker.MapsActivity;
import com.example.tracker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    public class UserViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username ;
        public TextView status ;
        public Button request ;
        public TextView usernumber;
        public UserViewHolder(View view)
        {
            super(view);
            username = view.findViewById(R.id.username);
            status = view.findViewById(R.id.status);
            request = view.findViewById(R.id.request);
            usernumber = view.findViewById(R.id.usernumber);
            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User current = (User) username.getTag();
                    if(current.Request_status.equals("not_requested") || current.Request_status.equals("rejected")) {
                        if(current.Status.equals("online") == true )
                            MainActivity.userViewModel.Request_track_some_one(current.Number);
                        else
                            Toast.makeText(v.getContext(), "user is offline you can't request tracking this user", Toast.LENGTH_SHORT).show();
                    }
                    if(current.Request_status.equals("accepted")) {
                        Intent intent = new Intent(v.getContext(), MapsActivity.class);
                        List<String> users_data = new ArrayList<String>();
                        users_data.add(current.toString());
                        intent.putStringArrayListExtra("data_list", (ArrayList<String>) users_data);
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    private List<User> Users = Arrays.asList();
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.username.setText(Users.get(position).Name);
        holder.status.setText(Users.get(position).Status);
        String status_color = (Users.get(position).Status.equals("online")) ? "#32b032": "#ff0000" ;
        holder.status.setTextColor(Color.parseColor(status_color));
        holder.username.setTag(Users.get(position));
        holder.usernumber.setText(Users.get(position).Number);

        if (Users.get(position).Request_status != null) {
            if (Users.get(position).Request_status.equals("not_requested")) {
                holder.request.setText("request");
                holder.request.setBackgroundColor(Color.parseColor("#8597ab"));
                holder.request.setActivated(true);
                if (Users.get(position).Status.equals("offline") == true )
                    holder.request.setActivated(false);

            } else if (Users.get(position).Request_status.equals("requested")) {

                holder.request.setActivated(false);
                holder.request.setText("please wait");
                holder.request.setBackgroundColor(Color.parseColor("#939591"));
            } else if (Users.get(position).Request_status.equals("accepted")) {
                holder.request.setText("start Tracking");
                holder.request.setActivated(true);
                holder.request.setBackgroundColor(Color.parseColor("#1c7a13"));

            } else if (Users.get(position).Request_status.equals("rejected")) {
                holder.request.setText("request again");
                holder.request.setActivated(true);
                holder.request.setBackgroundColor(Color.parseColor("#7a1313"));
            }
        }
    }


    @Override
    public int getItemCount() {
        return Users.size();
    }

    public void setUsers (List<User> users)
    {
        this.Users = users;
        notifyDataSetChanged();
    }



}
