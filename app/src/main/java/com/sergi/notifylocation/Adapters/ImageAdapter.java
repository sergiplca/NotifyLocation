package com.sergi.notifylocation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.sergi.notifylocation.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Sergi on 14/03/2017.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    public Context context;
    private ArrayList<String> items;
    private Bitmap bmp;

    private StorageReference mStorage;

    public ImageAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public ImageAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageAdapter.ImageViewHolder(this.context, LayoutInflater.from(parent.getContext()).inflate(R.layout.image_row, parent, false));
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    @Override
    public void onBindViewHolder(final ImageAdapter.ImageViewHolder holder, int position) {
        Log.d("Item", items.get(position));
        mStorage.child(items.get(position)).getStream(
                new StreamDownloadTask.StreamProcessor() {
                    @Override
                    public void doInBackground(StreamDownloadTask.TaskSnapshot taskSnapshot, InputStream inputStream) throws IOException {
                        bmp = BitmapFactory.decodeStream(inputStream);

                    }
                }
        ).addOnSuccessListener(new OnSuccessListener<StreamDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(StreamDownloadTask.TaskSnapshot taskSnapshot) {
                holder.img.setImageBitmap(Bitmap.createScaledBitmap(bmp, 1280, 720, false));
            }
        });
    }

    public void addItem (String image) {
        items.add(0, image);
        notifyItemInserted(0);
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        Context context;
        ImageView img;

        public ImageViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;

            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }

}
