package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tracker.User_MVVM.User;
import com.example.tracker.User_MVVM.UserViewModel;
import com.example.tracker.User_MVVM.UsersAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    public static UserViewModel userViewModel;
    private SwitchMaterial online_status ;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private UsersAdapter usersAdapter;

    private ExtendedFloatingActionButton add_new_user ;
    private ExtendedFloatingActionButton request_all ;
    private Button track_all;


    private  Intent Tracking_service ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.user_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        usersAdapter = new UsersAdapter();
        recyclerView.setAdapter(usersAdapter);
        recyclerView.setLayoutManager(layoutManager);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.set_my_number( getIntent().getStringExtra("mynumber"));
        Tracking_service = userViewModel.getTracking_service();
        userViewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                usersAdapter.setUsers(users);
                boolean one_user_accept = false;
                for (int i = 0 ; i < users.size();i++)
                {
                    if(users.get(i).Request_status.equals("accepted") == true)
                    {
                        one_user_accept = true;
                        break;
                    }
                }
                if (one_user_accept)
                    track_all.setVisibility(View.VISIBLE);
                else
                {
                    track_all.setVisibility(View.INVISIBLE);
                    request_all.setActivated(true);
                    request_all.setText("Request all");
                    request_all.setTextColor(Color.parseColor("#dedfdc"));
                    request_all.setTextColor(Color.parseColor("#000000"));
                }

            }
        });
        userViewModel.getRequest().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
            Toast.makeText(MainActivity.this,s,Toast.LENGTH_SHORT).show();
                on_receiving_request(s);
            }
        });


        add_new_user = findViewById(R.id.add_new_user);
        add_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_new_user("");
            }});


        request_all =findViewById(R.id.Request_all);
        request_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_all_button();
            }});


        track_all = findViewById(R.id.track_all);
        track_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                track_all_button(v);
            }
        });

        online_status = findViewById(R.id.online_status);
        online_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    userViewModel.set_my_online_status(true);
                    online_status.setText("Online");
                    online_status.setTextColor(Color.parseColor("#7ed984"));
                }
                else {
                    userViewModel.set_my_online_status(false);
                    online_status.setText("Offline");
                    online_status.setTextColor(Color.parseColor("#db004d"));
                    try
                    {
                        stopService(Tracking_service);
                        userViewModel.set_Is_service_running(false);
                    }catch (Exception e) {}
                }}});

    }



    void add_new_user(String number)
    {
        LayoutInflater inflater = getLayoutInflater();

        final View view =  inflater.inflate(R.layout.add_new_user, null);
        EditText userNumber = view.findViewById(R.id.usernumber_new_user);
        userNumber.setText(number);

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle("Add New User to Track")
                .setMessage("Enter the User's Information ")

                .setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText userName = view.findViewById(R.id.username_new_user);
                        EditText userNumber = view.findViewById(R.id.usernumber_new_user);
                        User user = new User( userNumber.getText().toString() , userName.getText().toString());
                        if(userViewModel.Add_User(user))
                            Toast.makeText(MainActivity.this, "user has been added", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, "user is already exist", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }


    void on_receiving_request(final String requested_number)
    {
        String title ;
        User user = userViewModel.get_user(requested_number);
        if(user == null)
            title ="some one want to track you";
        else
            title = user.Name + " want to track you";

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle(title)
                .setMessage(requested_number + " want to track you")
                .setPositiveButton("accept", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userViewModel.Accept_or_Refuce_Request(requested_number,true);
                if ( checkPermissions()) {
                    if (userViewModel.Get_Is_service_running() == false) {
                        userViewModel.set_Is_service_running(true);
                        startService(Tracking_service);
                    }
                    if(user == null)
                        do_you_want_to_add_this_number(requested_number);
                } else if (!checkPermissions()) {
                    requestPermissions();
                }
            }
        }).setNegativeButton("decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userViewModel.Accept_or_Refuce_Request(requested_number,false);
            }
        }).show();

    }



    void do_you_want_to_add_this_number(final String number)
    {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle("do you want to save this number")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        add_new_user(number);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }


    void track_all_button(View v)
    {
        List<String> users_data = userViewModel.Track_all();
        Intent intent = new Intent(v.getContext(), MapsActivity.class);
        intent.putStringArrayListExtra("data_list", (ArrayList<String>) users_data);
        startActivity(intent);
    }


    void request_all_button()
    {
        userViewModel.Request_track_all();
        request_all.setActivated(false);
        request_all.setText("please wait");
        request_all.setTextColor(Color.parseColor("#939591"));
    }


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

}


