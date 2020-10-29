package com.example.tracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.example.tracker.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private UsersAdapter usersAdapter;

    public  static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public  static DATABASE database  ;

    public static DocumentReference my_number ;
    public static String My_Number ;

    private ExtendedFloatingActionButton add_new_user ;
    private ExtendedFloatingActionButton request_all ;
    private Button track_all;
    private String track_all_button_state = "Not_Requested";

    private SwitchMaterial online_status ;
    private boolean online_Status;
    private Intent Tracking_service ;
    boolean Is_service_running ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.user_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        usersAdapter = new UsersAdapter();
        recyclerView.setAdapter(usersAdapter);
        recyclerView.setLayoutManager(layoutManager);


        database = Room.databaseBuilder(getApplicationContext(),DATABASE.class,"users").allowMainThreadQueries().build();

        add_new_user = findViewById(R.id.add_new_user);
        add_new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_new_user_button();
            }});

        request_all =findViewById(R.id.Request_all);
        request_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request_all_button(v);
            }});

        track_all = findViewById(R.id.track_all);
        track_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                track_all_button(v);
            }
        });


        My_Number = getIntent().getStringExtra("mynumber");
        my_number =  db.collection("users").document(My_Number);
        delete_old_requests();
        set_online_status(true);
        online_Status = true;
        online_status = findViewById(R.id.online_status);
        online_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    set_online_status(true);
                    online_Status = true;
                    online_status.setText("Online");
                    online_status.setTextColor(Color.parseColor("#7ed984"));
                }
                else {
                    set_online_status(false);
                    online_Status = false;
                    online_status.setText("Offline");
                    online_status.setTextColor(Color.parseColor("#db004d"));
                    Is_service_running = false;
                    try
                    {
                        stopService(Tracking_service);
                    }catch (Exception e) {}
                }
            }
        });

        usersAdapter.first_reload();

        set_listner_to_users_online_state();
        Tracking_service = new Intent(MainActivity.this, tracker.class);
    }


    @Override
    protected void onResume() {
        super.onResume();

        request_all.setActivated(true);
        request_all.setText("Request all");
        request_all.setBackgroundColor(Color.parseColor("#dedfdc"));
        request_all.setTextColor(Color.parseColor("#000000"));
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        set_online_status(false);
    }

    void set_online_status(final boolean Is_online)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("online", Is_online);

        my_number.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {

        }
    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


        if(Is_online == true) {
            my_number.collection("requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {

                        return;
                    }
                    String source = snapshots != null && snapshots.getMetadata().hasPendingWrites()
                            ? "Local" : "Server";
                    if(source.equals("Server") && online_Status == true) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    String type = dc.getDocument().getString("type");
                                    String number = dc.getDocument().getString("number");
                                    if (number != null && type != null) {
                                        if (type.equals("request")) {
                                            on_receiving_request(number, dc.getDocument().getId());
                                        } else if (type.equals("permission")) {
                                            Boolean permission = dc.getDocument().getBoolean("permission");
                                            on_receiving_request_result(number, permission);
                                            delete_request(dc.getDocument().getId());
                                        }
                                    }
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                    }
                }
            });
        }

    }
    public static void request_tracking_some_one(final String phone_number)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("number", My_Number);
        data.put("type","request");


        db.collection("users").document(phone_number).collection("requests")
                .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }
    void on_receiving_request(String requested_number,Boolean permission)
    {


        final Map<String, Object> data = new HashMap<>();
        data.put("number", My_Number);
        data.put("type","permission");
        data.put("permission",permission);



        db.collection("users").document(requested_number).collection("requests")
                .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    void on_receiving_request_result(String phone_number,  boolean permission)
    {
        usersAdapter.on_receiving_request_result(phone_number,permission);
        if (permission == true)
            track_all.setVisibility(View.VISIBLE);

        else
        {
            request_all.setActivated(true);
            request_all.setText("Request Again");
            request_all.setTextColor(Color.parseColor("#dedfdc"));
            request_all.setTextColor(Color.parseColor("#000000"));
        }
    }


    void on_receiving_request(final String requested_number, final String request_document_id)
    {
        String title ;
        if(does_this_number_exist(requested_number) == true)
        {
            User temp = database.getUserDao().Is_this_number_exis(requested_number).get(0);
            title = temp.Name + " want to track you";
        }
        else
        {
            title ="some one want to track you";
        }
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle(title)
                .setMessage(requested_number + "want to track you").setPositiveButton("accept", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                on_receiving_request(requested_number,true);
                delete_request(request_document_id);
                if ( checkPermissions()) {
                    //Intent intent = new Intent(MainActivity.this, tracker.class);
                    if (Is_service_running == false) {
                        Is_service_running = true;
                        startService(Tracking_service);
                    }
                     if(does_this_number_exist(requested_number) == false)
                             do_you_want_to_add_this_number(requested_number);
                } else if (!checkPermissions()) {
                    requestPermissions();
                }
            }
        }).setNegativeButton("decline", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                on_receiving_request(requested_number,false);
                delete_request(request_document_id);
            }
        }).show();

    }

    void delete_request(String id)
    {
        my_number.collection("requests").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        ;
    }

    void delete_old_requests()
    {
        my_number.collection("requests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        delete_request(document.getId());
                    }
                } else {
                }
            }
        });
    }





    boolean does_this_number_exist(String number)
    {
        if(database.getUserDao().Is_this_number_exis(number).size() == 0)
            return false;
        else
            return true;
    }

    void add_new_user_button()
    {
        LayoutInflater inflater = getLayoutInflater();

        final View view =  inflater.inflate(R.layout.add_new_user, null);


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
                        if(does_this_number_exist(userNumber.getText().toString()))
                        {
                            Toast.makeText(MainActivity.this, "user is already exist", Toast.LENGTH_SHORT).show();
                        }else {
                            MainActivity.database.getUserDao().create_new_user(userName.getText().toString(), userNumber.getText().toString());
                            set_listner_to_user_online_state(userNumber.getText().toString());
                            usersAdapter.first_reload();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
                        save_user(number);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    void save_user(final String number)
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
                        userNumber.setText(number);
                        if(does_this_number_exist(userNumber.getText().toString()))
                        {
                            Toast.makeText(MainActivity.this, "user is already exist", Toast.LENGTH_SHORT).show();
                        }else {
                            MainActivity.database.getUserDao().create_new_user(userName.getText().toString(), userNumber.getText().toString());
                            set_listner_to_user_online_state(userNumber.getText().toString());
                            usersAdapter.first_reload();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }



    void request_all_button(View v)
    {

            for (int i = 0; i < usersAdapter.Users.size(); i++) {
                User current = usersAdapter.Users.get(i);
                if(current.Request_status.equals("rejected") || current.Request_status.equals("not_requested") ) {
                    current.Request_status = "requested";
                    request_tracking_some_one(current.Number);
                }

            }
            usersAdapter.notifyDataSetChanged();
            request_all.setActivated(false);
            request_all.setText("please wait");
            request_all.setTextColor(Color.parseColor("#939591"));
        }



    void track_all_button(View v)
    {

            List<String> users_data = new ArrayList<String>();
            for (int i = 0; i < usersAdapter.Users.size(); i++) {
                User current = usersAdapter.Users.get(i);
                if (current.Request_status.equals("accepted")) {
                    users_data.add(current.toString());
                }
            }

            Intent intent = new Intent(v.getContext(), MapsActivity.class);
            intent.putStringArrayListExtra("data_list", (ArrayList<String>) users_data);
            startActivity(intent);

    }



    void set_listner_to_users_online_state()
    {
        for (int i = 0; i < usersAdapter.Users.size(); i++) {
            final User current = usersAdapter.Users.get(i);
            String user_number = current.Number;

            db.collection("users").document(user_number).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {

                        Boolean online = snapshot.getBoolean("online");
                        current.Status = (online) ? "online": "offline" ;
                        usersAdapter.notifyDataSetChanged();

                    } else { }
                }
            });

        }



    }
    void set_listner_to_user_online_state(final String user_number)
    {
        db.collection("users").document(user_number)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    Boolean online = snapshot.getBoolean("online");
                    User current = usersAdapter.get_user_by_user_number(user_number);
                    if(current != null) {
                        current.Status = (online) ? "online" : "offline";
                        usersAdapter.notifyDataSetChanged();
                    }
                } else { }
            }
        });

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
