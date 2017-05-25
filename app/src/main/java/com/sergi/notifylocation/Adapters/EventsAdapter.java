package com.sergi.notifylocation.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sergi.notifylocation.Models.Event;
import com.sergi.notifylocation.R;

import java.util.ArrayList;

/**
 * Created by victorserrate on 16/3/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {


    public Context context;
    private ArrayList<Event> items;

    public EventsAdapter(Context context, ArrayList<Event> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public EventsAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new EventsAdapter.EventViewHolder(this.context, LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false));
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public void onBindViewHolder(EventsAdapter.EventViewHolder holder, int position) {
        Event event = items.get(position);

        //holder.imageView.setImageBitmap(event.getImage());
        holder.description.setText(event.getDescription());
        holder.time.setText(event.getTime());
    }

    public void addItem (Event event) {
        items.add(0, event);
        notifyItemInserted(0);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ImageView imageView;
        TextView description;
        TextView time;

        public EventViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            imageView = (ImageView) itemView.findViewById(R.id.imgE);
            description = (TextView) itemView.findViewById(R.id.descriptionE);
            time = (TextView) itemView.findViewById(R.id.timeE);
        }
    }
}
