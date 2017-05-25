package com.sergi.notifylocation.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sergi.notifylocation.Adapters.CommentAdapter;
import com.sergi.notifylocation.Adapters.ImageAdapter;
import com.sergi.notifylocation.Models.Location;
import com.sergi.notifylocation.Models.PrincipalComment;
import com.sergi.notifylocation.R;

import java.util.ArrayList;

public class LocationInfoFragment extends Fragment {

    private static final int CAMERA_PIC_REQUEST = 1337;

    private static View view;

    private OnFragmentInteractionListener mListener;

    private Button postComment, postImage;
    private EditText comment;

    private double latitude;
    private double longitude;
    private String name, address, phone;

    private RecyclerView commentRecyclerView;
    private RecyclerView imageRecyclerView;

    private static CommentAdapter commentAdapter;
    private static ImageAdapter imageAdapter;

    private DatabaseReference mDatabase;
    private StorageReference mStorage;


    public LocationInfoFragment() {
        // Required empty public constructor
    }

    public static LocationInfoFragment newInstance(double latitude, double longitude, String name,
                                                   String address, String phone) {
        LocationInfoFragment fragment = new LocationInfoFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", latitude);
        args.putDouble("long", longitude);
        args.putString("name", name);
        args.putString("address", address);
        args.putString("phone", phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            latitude = getArguments().getDouble("lat");
            longitude = getArguments().getDouble("long");
            name = getArguments().getString("name");
            address = getArguments().getString("address");
            phone = getArguments().getString("phone");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_location_info, container, false);

            postComment = (Button) view.findViewById(R.id.postcomment);
            postComment.setOnClickListener(new PostCommentListener());

            postImage = (Button) view.findViewById(R.id.postimage);
            postImage.setOnClickListener(new PostImageListener());

            comment = (EditText) view.findViewById(R.id.comment);

            setUpRecyclerViews(view);
        } catch (InflateException e) {
        }
        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setUpRecyclerViews(View fragmentView) {

        SnapHelper helper = new LinearSnapHelper();

        commentRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        ArrayList<String> comments = new ArrayList<>();
        if (commentAdapter == null) {
            comments = getCommentsFromFirebase();
        }
        commentAdapter = new CommentAdapter(comments);
        commentRecyclerView.setAdapter(commentAdapter);

        imageRecyclerView = (RecyclerView)fragmentView.findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        helper.attachToRecyclerView(imageRecyclerView);
        if (imageAdapter == null) {
            ArrayList<String> images = getImagesFromFirebase();
        }
        imageRecyclerView.setAdapter(imageAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final Intent dataUri = data;

        if (requestCode == CAMERA_PIC_REQUEST && data != null) {
            StorageReference imageStorage = mStorage.child(String.valueOf(data.getData().hashCode()));
            imageStorage.putFile(data.getData());

            //Add comment to location
            mDatabase.child("locations").child(name).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Location l = dataSnapshot.getValue(Location.class);

                    if (l.getImages() != null) {
                        l.getImages().add(String.valueOf(dataUri.getData().hashCode()));
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(String.valueOf(dataUri.getData().hashCode()));
                        l.setImages(list);
                    }

                    mDatabase.child("locations").child(name).setValue(l);

                    mDatabase.child("locations").child(name).removeEventListener(this);

                    imageAdapter = new ImageAdapter(getContext(), l.getImages());
                    imageRecyclerView.setAdapter(imageAdapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            imageRecyclerView.smoothScrollToPosition(0);
        }
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    // ClickListener to post comments

    protected class PostCommentListener implements View.OnClickListener {

        private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);


        public PostCommentListener() {
            super();
        }

        @Override
        public void onClick (View view) {
            view.startAnimation(buttonClick);
            if (!TextUtils.isEmpty(comment.getText().toString())) {
                final String c = comment.getText().toString();
                commentAdapter.addItem(c);
                PrincipalComment pC = new PrincipalComment(c, name);
                mDatabase.child("comments").child(String.valueOf(c.hashCode())).setValue(pC);

                //Add comment to location
                mDatabase.child("locations").child(name).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Location l = dataSnapshot.getValue(Location.class);

                        if (l.getComments() != null) {
                            l.getComments().add(String.valueOf(c.hashCode()));
                        } else {
                            ArrayList<String> list = new ArrayList<>();
                            list.add(String.valueOf(c.hashCode()));
                            l.setComments(list);
                        }

                        mDatabase.child("locations").child(name).setValue(l);

                        mDatabase.child("locations").child(name).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            commentRecyclerView.smoothScrollToPosition(0);
        }
    }

    // ClickListener to post images

    protected class PostImageListener implements View.OnClickListener {

        private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        public PostImageListener() {
            super();
        }

        @Override
        public void onClick (View view) {

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] {Manifest.permission.CAMERA}, 1);
            } else {
                view.startAnimation(buttonClick);
                Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
                getActivity().startActivityFromFragment(LocationInfoFragment.this, i, CAMERA_PIC_REQUEST);
            }
        }
    }

    private ArrayList<String> getCommentsFromFirebase() {
        final ArrayList<String> comments = new ArrayList<>();

        mDatabase.child("locations").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location location = dataSnapshot.getValue(Location.class);
                if (location.getComments() != null) {
                    for (final String s : location.getComments()) {
                        mDatabase.child("comments").child(s).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                PrincipalComment pC = dataSnapshot.getValue(PrincipalComment.class);
                                comments.add(pC.getComment());
                                commentAdapter = new CommentAdapter(comments);
                                commentRecyclerView.setAdapter(commentAdapter);
                                mDatabase.child("comments").child(s).removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
                mDatabase.child("locations").child(name).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return comments;
    }

    private ArrayList<String> getImagesFromFirebase() {
        final ArrayList<String> images = new ArrayList<>();

        mDatabase.child("locations").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Location location = dataSnapshot.getValue(Location.class);
                if (location.getImages() != null) {
                    for (final String s : location.getImages()) {
                        images.add(s);
                        imageAdapter = new ImageAdapter(getContext(), images);
                        imageRecyclerView.setAdapter(imageAdapter);
                    }
                }
                mDatabase.child("locations").child(name).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return images;
    }
}
