package com.example.tracker.User_MVVM;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tracker.trackerService;

import java.util.ArrayList;
import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<List<User>> users ;
    private MutableLiveData<String> request;
    private Intent Tracking_service;
    private boolean Is_service_running ;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        users = userRepository.getUsers();
        Tracking_service  = new Intent(application.getApplicationContext(), trackerService.class);
        Is_service_running = false;
    }
    public LiveData<List<User>> getUsers() {
        return users;
    }
    public MutableLiveData<String> getRequest() {return request;}

    public boolean Add_User(User user)
    {
        return userRepository.Add_User(user);
    }
    public User get_user(String user_number)
    {
        return userRepository.get_user(user_number);
    }
    public void set_my_number(String my_number)
    {
        userRepository.set_my_number(my_number);
        request = userRepository.getRequest();
        Tracking_service.putExtra("mynumber",my_number);

    }

    public void set_Is_service_running(boolean is_service_running) {
        Is_service_running = is_service_running;
    }

    public boolean Get_Is_service_running() {
        return Is_service_running;
    }

    public Intent getTracking_service()
    {
        return Tracking_service;
    }

    public void Accept_or_Refuce_Request(String requested_number,Boolean permission)
    {
        userRepository.Accept_or_Refuce_Request(requested_number,permission);
    }

    public void set_my_online_status(final boolean Is_online) {
        userRepository.set_my_online_status(Is_online);
    }

    public void Request_track_some_one(String number)
    {
        userRepository.request_tracking_some_one(number);
    }

    public void Request_track_all()
    {
        userRepository.Request_track_all();
    }


    public List<String> Track_all()
    {
        List <User> temp = users.getValue();

        List<String> users_data = new ArrayList<String>();
        for (int i = 0; i < temp.size(); i++) {
            User current = temp.get(i);
            if (current.Request_status.equals("accepted")) {
                users_data.add(current.toString());
            }
        }
        return users_data;
    }
}
