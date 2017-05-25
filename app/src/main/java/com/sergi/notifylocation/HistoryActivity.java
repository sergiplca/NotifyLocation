package com.sergi.notifylocation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.sergi.notifylocation.Fragments.HistoryFragment;
import com.sergi.notifylocation.Fragments.QueryFragment;

public class HistoryActivity extends AppCompatActivity implements QueryFragment.OnQueryListener {

    private QueryFragment queryFragment;
    private HistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        queryFragment = (QueryFragment)getSupportFragmentManager().findFragmentById(R.id.query_frag);
        historyFragment = (HistoryFragment)getSupportFragmentManager().findFragmentById(R.id.history_frag);

        queryFragment.setOnQueryListener(this);
    }

    @Override
    public void onQuerySubmitted(int dateFrom, int dateTo) {
        historyFragment.onQueryReceived(dateFrom, dateTo);
    }

}
