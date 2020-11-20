package com.example.tracker.User_MVVM;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;
@Entity()
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String Number;
    public String Name;

    @Ignore
    public String Status;   //online    or offline
    @Ignore
    public String Request_status;   //not_requested or  requested or rejected or accepted
    @Ignore
    public Boolean Request_Result;



    public User(String number, String name) {
        Number = number;
        Name = name;
        this.Status = "offline";
        Request_status = "not_requested";
    }

    public User() {
        this.Status = "offline";
        this.Request_status = "not_requested";
    }

    public void set_Request_status(Boolean request_Result)
    {
        this.Request_Result = request_Result;
        Request_status = (request_Result) ?  "accepted": "rejected" ;
    }

    public void Is_online(Boolean Is_online)
    {
        this.Status = (Is_online) ? "online": "offline" ;
        if(Is_online == false)
         this.Request_status = "not_requested";
    }


    @NonNull
    @Override
    public String toString() {
        return this.Number + "/" + this.Name + "/" + this.Status ;
    }

    public static User toObject(String StringObject)
    {
        List<String> s = Arrays.asList(StringObject.split("/"));
        return new User(s.get(0), s.get(1));
    }
}
