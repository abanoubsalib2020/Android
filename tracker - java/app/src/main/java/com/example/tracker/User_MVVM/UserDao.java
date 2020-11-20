package com.example.tracker.User_MVVM;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    public void create_new_user(User user);

    @Query("SELECT * FROM user")
    public List<User> get_all_users();

    @Query("SELECT * FROM user WHERE Number = :user_number")
    public List<User>  Is_this_number_exis(String user_number);

}
