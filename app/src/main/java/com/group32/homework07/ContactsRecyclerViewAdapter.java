package com.group32.homework07;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Ahmet on 11/18/2016.
 */

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<User> userList;

    public ContactsRecyclerViewAdapter() {
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

        public ViewHolder(View itemView) {
            super(itemView);
            imagePicture = (ImageView) itemView.findViewById(R.id.imageContactPicture);
            textFullname = (TextView) itemView.findViewById(R.id.textContactFullname);
        }
    }

    @Override
    public ContactsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_row,parent,false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        User currentUser = userList.get(position);
        holder.textFullname.setText(currentUser.fullName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}