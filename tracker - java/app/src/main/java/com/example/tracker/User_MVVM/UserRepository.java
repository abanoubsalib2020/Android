package com.example.tracker.User_MVVM;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
private FirebaseFirestore firestoredb ;
private DocumentReference my_number ;

private UserDao userDao ;
private  List<User> users;
private MutableLiveData<List<User>> LiveUsers;

private MutableLiveData<String> request;
private String My_Number ;

private MutableLiveData<Boolean> online_Status;

public UserRepository(Application application)
{
    DATABASE db = DATABASE.getInstance(application);
    firestoredb = FirebaseFirestore.getInstance();
    this.userDao = db.getUserDao();
    users = userDao.get_all_users();

    request = new MutableLiveData<String>();
    online_Status = new MutableLiveData<Boolean>();
    LiveUsers = new MutableLiveData<List<User>>();
    LiveUsers.setValue(users);

}



    public LiveData<List<User>> getUsers() {
    return LiveUsers;
    }
    public MutableLiveData<String> getRequest() {return request;}


    public boolean Add_User(User user)
    {
        if(get_user_index_by_user_number(user.Number) == -1) {
            users.add(user);
            update_changes();
            userDao.create_new_user(user);
             set_listner_to_user_online_state(user.Number);
            return true;
        }else
            return false;
    }
    public User get_user(String user_number)
    {
        int user_index = get_user_index_by_user_number(user_number);
        if(user_index== -1)
            return null;
        else
            return users.get(user_index);
    }

    public void track_all()
    {

    }

    public void set_my_number(String my_number)
    {
        this.My_Number = my_number;
        this.my_number = firestoredb.collection("users").document(my_number);
        set_my_online_status(true);
        delete_old_requests();
        set_listner_to_users_online_state();
        set_requests_listeners();
    }


    private int get_user_index_by_user_number(String user_number)
    {
        for (int i = 0 ; i < users.size() ; i++)
        {
            if (users.get(i).Number.equals(user_number) )
                return i;
        }
        return -1;  // incase user is not found
    }




    void set_listner_to_users_online_state()
    {
        for (int i = 0; i < users.size(); i++) {
             User current = users.get(i);
            String user_number = current.Number;
            set_listner_to_user_online_state(user_number);
        }
    }


    private void set_listner_to_user_online_state(String user_number)
    {
        User user = get_user(user_number);
        firestoredb.collection("users").document(user.Number)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {

                            Boolean online = snapshot.getBoolean("online");
                           // user.Status = (online) ? "online": "offline" ;
                            user.Is_online(online);
                            update_changes();
                        }
                    }
                });

    }



    private void set_requests_listeners()
    {
        my_number.collection("requests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {

                    return;
                }
                String source = snapshots != null && snapshots.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
                if(source.equals("Server") && online_Status.getValue() == true) {
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
    private void on_receiving_request(final String requested_number, final String request_document_id)
    {
        request.setValue(requested_number);
        delete_request(request_document_id);
    }

    private void on_receiving_request_result(String phone_number,  boolean permission)
    {
        int user_index = get_user_index_by_user_number(phone_number);
        if (user_index != -1)
        {
           users.get(user_index).set_Request_status(permission);
           update_changes();
        }

    }

    private void delete_request(String id)
    {
        my_number.collection("requests").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }});
    }

    private void delete_old_requests()
    {
        my_number.collection("requests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        delete_request(document.getId());
                    }
                } else {
                } }});
    }


    public void set_my_online_status(final boolean Is_online) {
        online_Status.setValue(Is_online);
        Map<String, Object> data = new HashMap<>();
        data.put("online", Is_online);
        my_number.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) { }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { }
                });
    }

   public void Accept_or_Refuce_Request(String requested_number,Boolean permission)
    {
        final Map<String, Object> data = new HashMap<>();
        data.put("number", My_Number);
        data.put("type","permission");
        data.put("permission",permission);
        firestoredb.collection("users").document(requested_number).collection("requests")
                .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
            }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }});
    }


    public  void request_tracking_some_one(String phone_number)
    {
        int user_index = get_user_index_by_user_number(phone_number);
        if (user_index != -1)
        {
            User user = users.get(user_index);
            if(user.Request_status.equals("rejected") || user.Request_status.equals("not_requested") ) {
                user.Request_status = "requested";

                Map<String, Object> data = new HashMap<>();
                data.put("number", My_Number);
                data.put("type","request");
                firestoredb.collection("users").document(phone_number).collection("requests")
                        .add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                    }}).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }});

                update_changes();
            }
        }
    }


    public void Request_track_all() {
        for (int i = 0; i < users.size(); i++)
            request_tracking_some_one(users.get(i).Number);
    }



    private void update_changes()
    {
        LiveUsers.setValue(users);
    }

}



