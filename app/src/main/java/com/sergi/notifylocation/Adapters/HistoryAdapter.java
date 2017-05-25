package com.sergi.notifylocation.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergi.notifylocation.Models.HistoryEntry;
import com.sergi.notifylocation.R;

import java.util.ArrayList;

/**
 * Created by Sergi on20/5/17.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<HistoryEntry> items;

    public HistoryAdapter(ArrayList<HistoryEntry> items) {
        this.items = items;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.history_row, parent, false));
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        HistoryEntry hE = items.get(position);

        holder.place.setText(hE.getPlace());
        holder.time.setText(hE.getTime());
    }

    public void addItem (HistoryEntry item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        View item;
        TextView place;
        TextView time;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            item = (View) itemView;
            place = (TextView) itemView.findViewById(R.id.place);
            time = (TextView) itemView.findViewById(R.id.time);
        }


    }
}
