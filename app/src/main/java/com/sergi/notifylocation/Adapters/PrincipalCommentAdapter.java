package com.sergi.notifylocation.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sergi.notifylocation.Models.PrincipalComment;
import com.sergi.notifylocation.R;


import java.util.ArrayList;

/**
 * Created by victorserrate on 16/3/17.
 */

public class PrincipalCommentAdapter extends RecyclerView.Adapter<PrincipalCommentAdapter.PrincipalCommentViewHolder> {

    private ArrayList<PrincipalComment> items;

    public PrincipalCommentAdapter(ArrayList<PrincipalComment> items) {
        this.items = items;
    }

    @Override
    public PrincipalCommentAdapter.PrincipalCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PrincipalCommentAdapter.PrincipalCommentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.principal_comment, parent, false));
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public void onBindViewHolder(PrincipalCommentAdapter.PrincipalCommentViewHolder holder, int position) {
        PrincipalComment pC = items.get(position);

        holder.comment.setText(pC.getComment());
        holder.place.setText(pC.getPlace());
    }

    public void addItem (PrincipalComment item) {
        items.add(0, item);
        notifyItemInserted(0);
    }

    static class PrincipalCommentViewHolder extends RecyclerView.ViewHolder {

        TextView comment;
        TextView place;

        public PrincipalCommentViewHolder(View itemView) {
            super(itemView);
            comment = (TextView) itemView.findViewById(R.id.commentP);
            place = (TextView) itemView.findViewById(R.id.placeP);
        }
    }

}
