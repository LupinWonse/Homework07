package com.group32.homework07;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Ahmet on 11/18/2016.
 */

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {

    private IContactsListHandler listHandler;
    private ArrayList<User> userList;

    public ContactsRecyclerViewAdapter(IContactsListHandler listHandler) {
        this.listHandler = listHandler;
        userList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userList.add(dataSnapshot.getValue(User.class));
                ContactsRecyclerViewAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imagePicture;
        TextView textFullname;
        View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imagePicture = (ImageView) itemView.findViewById(R.id.imageContactPicture);
            textFullname = (TextView) itemView.findViewById(R.id.textContactFullname);
        }
    }

    @Override
    public ContactsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_row,parent,false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(final ViewHolder holder, int position) {
        final User currentUser = userList.get(position);
        holder.textFullname.setText(currentUser.fullName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listHandler.onContactChosen(currentUser.getUid());
            }
        });

        // Load the profile picture
        StorageReference profilePictureReference = FirebaseStorage.getInstance().getReference(User.STORAGE_PROFILE_PICTURES_REFERENCE).child(currentUser.getUid());
        profilePictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(holder.itemView.getContext()).load(uri).into(holder.imagePicture);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    interface IContactsListHandler{
        void onContactChosen(String uid);
    }
}
