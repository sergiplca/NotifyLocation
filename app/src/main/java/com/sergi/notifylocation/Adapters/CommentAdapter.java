package com.sergi.notifylocation.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergi.notifylocation.R;

import java.util.ArrayList;

/**
 * Created by Sergi on 13/3/17.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.StringViewHolder> {

    private ArrayList<String> items;

    public CommentAdapter(ArrayList<String> items) {
        this.items = items;
    }

    @Override
    public StringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StringViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row, parent, false));
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public void onBindViewHolder(StringViewHolder holder, int position) {
        holder.comment.setText(items.get(position));
    }

    public void addItem (String item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    static class StringViewHolder extends RecyclerView.ViewHolder {

        TextView comment;

        public StringViewHolder(View itemView) {
            super(itemView);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }
}
