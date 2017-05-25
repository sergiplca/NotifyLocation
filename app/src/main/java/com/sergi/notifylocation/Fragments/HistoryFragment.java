package com.sergi.notifylocation.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sergi.notifylocation.Adapters.HistoryAdapter;
import com.sergi.notifylocation.Models.HistoryEntry;
import com.sergi.notifylocation.Models.Log;
import com.sergi.notifylocation.R;
import com.sergi.notifylocation.Remote.APIService;
import com.sergi.notifylocation.Remote.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HistoryFragment extends Fragment {

    private View fragmentView;

    private RecyclerView historyRecyclerView;
    private static HistoryAdapter historyAdapter;
    private static ArrayList<HistoryEntry> entries = new ArrayList<>();

    private APIService mAPIService;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        mAPIService = ApiUtils.getAPIService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_history, container, false);
        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        historyRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if (historyAdapter != null) {
            historyRecyclerView.setAdapter(historyAdapter);
        }
    }

    public void onQueryReceived (int dateFrom, int dateTo) {
        entries.clear();
        getHistory(dateFrom, dateTo);
    }

    private void getHistory(final int dateFrom, final int dateTo) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = prefs.getString("name", "Default username");
        mAPIService.getLog(name).enqueue(new Callback<List<Log>>() {
            @Override
            public void onResponse(Call<List<Log>> call, Response<List<Log>> response) {
                for (Log l : response.body()) {
                    if (checkDates(dateFrom, dateTo, l.getUserTime())) {
                        String formattedDate = l.getUserTime().substring(6,8) + '/'
                                             + l.getUserTime().substring(4,6) + '/'
                                             + l.getUserTime().substring(0,4);
                        entries.add(new HistoryEntry(l.getLocation(), formattedDate));
                    }
                }
                historyAdapter = new HistoryAdapter(entries);
                historyRecyclerView.setAdapter(historyAdapter);
            }

            @Override
            public void onFailure(Call<List<Log>> call, Throwable t) {
            }
        });
    }

    private boolean checkDates (int dateFrom, int dateTo, String logDate) {
        String incomingDate = logDate.substring(0,8);
        return (Integer.parseInt(incomingDate) <= dateTo &&  Integer.parseInt(incomingDate) >= dateFrom);
    }
}
