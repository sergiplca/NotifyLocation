package com.sergi.notifylocation.Fragments;


import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergi.notifylocation.Models.Location;
import com.sergi.notifylocation.Models.Log;
import com.sergi.notifylocation.R;
import com.sergi.notifylocation.Remote.APIService;
import com.sergi.notifylocation.Remote.ApiUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationFragment extends Fragment
                              implements OnMapReadyCallback {

    private static View view;
    private TextView tName, tAddress, tPhone;

    private double latitude;
    private double longitude;
    private String name, address, phone;

    private GoogleMap mMap;

    private OnFragmentInteractionListener mListener;

    private DatabaseReference mDatabase;

    private APIService mAPIService;

    public LocationFragment() {
    }

    public static LocationFragment newInstance(double latitude, double longitude, String name,
                                               String address, String phone) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", latitude);
        args.putDouble("long", longitude);
        args.putString("name", name);
        args.putString("address", address);
        args.putString("phone", phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble("lat");
            longitude = getArguments().getDouble("long");
            name = getArguments().getString("name");
            address = getArguments().getString("address");
            phone = getArguments().getString("phone");
        }

        mAPIService = ApiUtils.getAPIService();
    }

    @Override
    public void onStart () {
        super.onStart();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        writeLocation();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_location, container, false);

            tName = (TextView)view.findViewById(R.id.name);
            tAddress = (TextView)view.findViewById(R.id.address);
            tPhone = (TextView)view.findViewById(R.id.phone);

            tName.setText(name);
            tAddress.setText(address);
            tPhone.setText(phone);
        } catch (InflateException e) {
        }

        postLocationToHistory();

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker to the location and move the camera
        LatLng myLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Marker in my location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));
    }

    private void writeLocation() {
        mDatabase.child("locations").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location location = dataSnapshot.getValue(Location.class);
                if (location == null)
                    location = new Location(longitude, latitude, name, address, phone, new ArrayList<String>(), new ArrayList<String>());
                    mDatabase.child("locations").child(name).setValue(location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postLocationToHistory() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = prefs.getString("name", "Default username");
        String place = this.name;
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(Calendar.getInstance().getTime());

        mAPIService.saveLog(name, place, date).enqueue(new Callback<Log>() {
            @Override
            public void onResponse(Call<Log> call, Response<Log> response) {
            }

            @Override
            public void onFailure(Call<Log> call, Throwable t) {
            }
        });
    }
}
