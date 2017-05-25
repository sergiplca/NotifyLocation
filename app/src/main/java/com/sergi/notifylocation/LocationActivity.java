package com.sergi.notifylocation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergi.notifylocation.Adapters.PagerAdapter;
import com.sergi.notifylocation.Models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LocationActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;

    private SharedPreferences prefs;

    private double latitude;
    private double longitude;
    private String name;
    private String address;
    private String phone;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        latitude = this.getIntent().getDoubleExtra("lat", 0);
        longitude = this.getIntent().getDoubleExtra("long", 0);
        name = this.getIntent().getStringExtra("name");
        address = this.getIntent().getStringExtra("address");
        phone = this.getIntent().getStringExtra("phone");

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(this.name);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new PagerAdapter(getSupportFragmentManager(), latitude, longitude, name,
                                                                         address, phone);
        vpPager.setAdapter(adapterViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(vpPager);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_friends:
                Intent i1 = new Intent (this, SearchFriendsActivity.class);
                startActivity(i1);
                return true;
            case R.id.settings:
                Intent i2 = new Intent (this, PreferencesActivity.class);
                startActivityForResult(i2, 100);
                return true;
            case R.id.friends:
                Intent i3 = new Intent (this, FriendsActivity.class);
                startActivity(i3);
                return true;
            case R.id.history:
                Intent i4 = new Intent (this, HistoryActivity.class);
                startActivity(i4);
                return true;
            case R.id.logoff:
                FirebaseAuth.getInstance().signOut();
                Intent i5 = new Intent (this, LoginActivity.class);
                startActivity(i5);
            case R.id.exit:
                finish();
                return true;
            default:
                super.onOptionsItemSelected(item);
                return true;
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == 0) {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);

            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

            String userId = fUser.getUid();

            String name = prefs.getString("name", "");
            String email = fUser.getEmail().toString();
            Integer radius = Integer.parseInt(prefs.getString("radius", ""));
            Set<String> selections = prefs.getStringSet("locations_available", null);
            List<String> list = new ArrayList<>();
            list.addAll(selections);

            final List<String> friends = new ArrayList<>();

            mDatabase.child("users").child(userId).child("friends").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot != null)
                            friends.add(snapshot.getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            User user = new User(name, email, radius, list, friends);
            mDatabase.child("users").child(userId).setValue(user);
        }
    }
}
