package com.sergi.notifylocation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.sergi.notifylocation.Adapters.PrincipalCommentAdapter;
import com.sergi.notifylocation.LocationReceiver.AutoStart;
import com.sergi.notifylocation.Models.PrincipalComment;
import com.sergi.notifylocation.Models.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    private RecyclerView recyclerView;
    private static PrincipalCommentAdapter adapter;
    private ArrayList<PrincipalComment> items = new ArrayList<>();

    private FirebaseDatabase dB;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("NotifyLocation");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        dB = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null)
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                else {
                    dB.getReference("tokensdevices").child(FirebaseInstanceId.getInstance().getToken()).setValue(true);
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);


        recyclerView = (RecyclerView) findViewById(R.id.pCommentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        new GetCommentsFromFirebaseTask().execute();

        loadPreferencesFromFirebase();

        Intent i = new Intent(this, AutoStart.class);
        sendBroadcast(i);
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

    public void loadPreferencesFromFirebase() {
        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("name", user.getUsername());
                editor.putString("radius", String.valueOf(user.getRadius()));
                editor.putStringSet("locations_available", new HashSet<>(user.getPlacesOfInterest()));

                editor.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private class GetCommentsFromFirebaseTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground (String... params) {
            mDatabase.child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        items.add(snapshot.getValue(PrincipalComment.class));
                        if (adapter == null)
                            adapter = new PrincipalCommentAdapter(items);
                        recyclerView.setAdapter(adapter);
                    }

                    mDatabase.child("comments").removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return "";
        }

        @Override
        protected void onPostExecute (String result) {
            super.onPostExecute(result);
        }
    }
}
