package com.example.tracker;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface userDao {
    @Query("INSERT INTO USERS (Name,Number) VALUES (:Name,:number)")
    public void create_new_user(String Name ,String number);

    @Query("SELECT * FROM USERS")
    public List<User> get_all_users();

    @Query("SELECT * FROM USERS WHERE Number = :user_number")
    public List<User>  Is_this_number_exis(String user_number);

}
