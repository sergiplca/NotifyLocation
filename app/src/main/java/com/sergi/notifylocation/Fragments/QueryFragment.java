package com.sergi.notifylocation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.sergi.notifylocation.R;


public class QueryFragment extends Fragment {

    private Button querySubmitter;

    private EditText ddFrom, ddTo, mmFrom, mmTo, yyyyFrom, yyyyTo;

    private OnQueryListener mListener;

    public QueryFragment() {
        // Required empty public constructor
    }

    public static QueryFragment newInstance() {
        QueryFragment fragment = new QueryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_query, container, false);

        ddFrom = (EditText)view.findViewById(R.id.ddFrom);
        mmFrom = (EditText)view.findViewById(R.id.mmFrom);
        yyyyFrom = (EditText)view.findViewById(R.id.yyyyFrom);
        ddTo = (EditText)view.findViewById(R.id.ddTo);
        mmTo = (EditText)view.findViewById(R.id.mmTo);
        yyyyTo = (EditText)view.findViewById(R.id.yyyyTo);

        querySubmitter = (Button)view.findViewById(R.id.querySubmitter);
        querySubmitter.setOnClickListener(new QuerySubmitter());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnQueryListener {
        void onQuerySubmitted(int dateFrom, int dateTo);
    }

    public void setOnQueryListener (OnQueryListener listener) {
        this.mListener = listener;
    }

    private void submitQuery(int dateFrom, int dateTo) {
        mListener.onQuerySubmitted(dateFrom, dateTo);
    }

    private class QuerySubmitter implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (checkCorrectDates()) {
                int dateFrom = Integer.parseInt(yyyyFrom.getText().toString() + mmFrom.getText().toString() + ddFrom.getText().toString());
                int dateTo = Integer.parseInt(yyyyTo.getText().toString() + mmTo.getText().toString() + ddTo.getText().toString());
                submitQuery(dateFrom, dateTo);
            }
        }

        private boolean checkCorrectDates() {

            if (TextUtils.isEmpty(ddFrom.getText().toString()) ||
                    Integer.parseInt(ddFrom.getText().toString()) > 31) {
                ddFrom.requestFocus();
                ddFrom.setError("Insert a correct day");
                return false;
            }
            if (TextUtils.isEmpty(mmFrom.getText().toString()) ||
                    Integer.parseInt(mmFrom.getText().toString()) > 12) {
                mmFrom.requestFocus();
                mmFrom.setError("Insert a correct month");
                return false;
            }
            if (TextUtils.isEmpty(yyyyFrom.getText().toString())) {
                yyyyFrom.requestFocus();
                yyyyFrom.setError("Insert a correct year");
                return false;
            }
            if (TextUtils.isEmpty(ddTo.getText().toString()) ||
                    Integer.parseInt(ddTo.getText().toString()) > 31) {
                ddTo.requestFocus();
                ddTo.setError("Insert a correct day");
                return false;
            }
            if (TextUtils.isEmpty(mmTo.getText().toString()) ||
                    Integer.parseInt(mmTo.getText().toString()) > 12) {
                mmTo.requestFocus();
                mmTo.setError("Insert a correct month");
                return false;
            }
            if (TextUtils.isEmpty(yyyyTo.getText().toString())) {
                yyyyTo.requestFocus();
                yyyyTo.setError("Insert a correct year");
                return false;
            }

            String joinedDateFrom = yyyyFrom.getText().toString() +
                                    mmFrom.getText().toString() +
                                    ddFrom.getText().toString();
            String joinedDateTo =   yyyyTo.getText().toString() +
                                    mmTo.getText().toString() +
                                    ddTo.getText().toString();

            if (Integer.parseInt(joinedDateFrom) > Integer.parseInt(joinedDateTo)) {
                ddTo.requestFocus();
                ddTo.setError("Date to must be bigger than date from");
                return false;
            }

            return true;
        }
    }
}
