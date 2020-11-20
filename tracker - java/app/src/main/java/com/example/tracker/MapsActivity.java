package com.example.tracker;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.tracker.User_MVVM.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback
{
    double max_lat = 0 ;
    double min_lat  = 0;
    double max_long = 0 ;
    double min_long = 0 ;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MarkerOptions mylocation ;
    private LatLng MylatLng;
    private GoogleMap mMap;
    private ExtendedFloatingActionButton stop_tracking ;
    private Circle [] circles;
    private Marker [] markers;
    private  ArrayList<String> users_data ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        stop_tracking = findViewById(R.id.stop_tracking);
        stop_tracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });
        if (getIntent().getStringArrayListExtra("data_list") != null ) {
            users_data = getIntent().getStringArrayListExtra("data_list");
            markers = new Marker[users_data.size()];
            for (int i = 0 ; i < users_data.size() ; i++)
            {
                User user = User.toObject(users_data.get(i));
                set_listener_for_list(user.Number,i);
            }
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap ;
        LatLng sydney = new LatLng(-33.852, 151.211);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        generate_markers_for_list();
    }





    void generate_markers_for_list()
    {
        for (int i = 0 ; i < markers.length ;i++)
        {
            User user = User.toObject(users_data.get(i));
            markers[i] =  mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(37.4, -122.1))
                    .title(user.Name)
                    .icon(BitmapDescriptorFactory.defaultMarker(i*20))
                    .draggable(true));
           mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(markers[i].getPosition().latitude, markers[i].getPosition().longitude)));
        }
    }


    public void set_listener_for_list (String user_number , final int index) {
        db.collection("users").document(user_number).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {

                    GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");

                    if (geoPoint != null) {

                        MylatLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        markers[index].setPosition(MylatLng);
                        calculate_max();
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(calculate_center()));
                        //LatLngBounds latLngBounds = new LatLngBounds(new LatLng(min_lat,min_long),new LatLng(max_lat,max_long));
                        //mMap.setLatLngBoundsForCameraTarget(latLngBounds);

                    }

                } else {

                }
            }
        });
    }


    void calculate_max()
    {
        max_long = markers[0].getPosition().longitude;
        max_lat = markers[0].getPosition().latitude;

        min_long = markers[0].getPosition().longitude;
        min_lat = markers[0].getPosition().latitude;

        for (int i = 1 ; i < markers.length ; i++)
        {
            if(markers[i].getPosition().latitude >= max_lat){max_lat = markers[i].getPosition().latitude ;}
            if(markers[i].getPosition().latitude <= min_lat){min_lat = markers[i].getPosition().latitude ;}
            if(markers[i].getPosition().longitude >= max_long){max_long = markers[i].getPosition().longitude; }
            if(markers[i].getPosition().longitude <= min_long){min_long = markers[i].getPosition().longitude ;}
        }

    }
    LatLng calculate_center()
    {
        Log.d("calculate_center",new LatLng((max_lat+min_lat)/2, (max_long+min_long)/2).toString());
        return new LatLng((max_lat+min_lat)/2, (max_long+min_long)/2 );

    }
}


