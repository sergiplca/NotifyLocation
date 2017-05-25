package com.sergi.notifylocation;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergi.notifylocation.Models.User;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendsActivity extends AppCompatActivity
                                   implements SearchView.OnQueryTextListener {

    ListView listView;
    SearchView searchView;

    ArrayAdapter<String> adapter;
    List<String> names = new ArrayList<>();
    List<User> itemname = new ArrayList<>();
    List<String> userUids = new ArrayList<>();

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Search friends");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        listView = (ListView) findViewById(R.id.friendslist);

        new DownloadUsersTask().execute();
        adapter = new ArrayAdapter<>(
                this, R.layout.row, R.id.friend_name, names);
        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

                mDatabase.child("users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        String addedUid = "";
                        for (int i = 0; i < itemname.size(); i++) {
                            if (itemname.get(i).getUsername().equals(names.get(position))) {
                                addedUid = userUids.get(i);
                            }
                        }

                        if (user.getFriends() != null) {
                            user.getFriends().add(addedUid);
                        } else {
                            ArrayList<String> list = new ArrayList<>();
                            list.add(addedUid);
                            user.setFriends(list);
                        }

                        mDatabase.child("users").child(fUser.getUid()).setValue(user);
                        mDatabase.child("users").child(fUser.getUid()).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onQueryTextChange (String query) {
        if (TextUtils.isEmpty(query)) {
            new SearchFriendsTask().execute(query);
        } else {
            new SearchFriendsTask().execute(query);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit (String query) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_search, menu);

        MenuItem searchViewItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchViewItem.getActionView();
        searchView.setQueryHint(getString(R.string.enter_name));
        searchView.setIconifiedByDefault(false);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(this);
        return true;
    }


    public class SearchFriendsTask extends AsyncTask<String, Void, String> {

        User[] arr = itemname.toArray(new User[itemname.size()]);

        @Override
        protected String doInBackground (String... params) {
            if (TextUtils.isEmpty(params[0])) {
                names.clear();
            } else {
                for (int i = 0; i < arr.length; i++) {
                    if (params[0].equals(arr[i].getUsername()))
                        names.add(arr[i].getUsername());
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute (String result) {
            super.onPostExecute(result);

            adapter.notifyDataSetChanged();
        }
    }

    private class DownloadUsersTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground (Void... params) {
            mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        itemname.add(snapshot.getValue(User.class));
                        userUids.add(snapshot.getKey());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

        }
    }
}
