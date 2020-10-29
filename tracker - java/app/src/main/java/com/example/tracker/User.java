package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;
@Entity(tableName = "USERS")
public class User {
    @PrimaryKey
    public int id;
    @ColumnInfo(name = "Number")
    public String Number;
    @ColumnInfo(name = "Name")
    public String Name;
    public String Status;   //online    or offline
    public String Request_status;   //not_requested or  requested or rejected or accepted
    public Boolean Request_Result;



    public User(String number, String name, String status) {
        Number = number;
        Name = name;
        this.Status = status;
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



    @NonNull
    @Override
    public String toString() {
        return this.Number + "/" + this.Name + "/" + this.Status ;
    }

    public static User toObject(String StringObject)
    {
        List<String> s = Arrays.asList(StringObject.split("/"));
        return new User(s.get(0), s.get(1), s.get(2));
    }
}
