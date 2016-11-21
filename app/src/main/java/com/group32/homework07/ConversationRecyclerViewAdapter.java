package com.group32.homework07;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Conversation> conversationList;
    private IConversationListHandler conversationListHandler;
    private DatabaseReference userDatabase;

    public ConversationRecyclerViewAdapter(IConversationListHandler handler) {
        this.conversationListHandler = handler;
        conversationList = new ArrayList<>();

        userDatabase = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                conversationList.add(dataSnapshot.getValue(Conversation.class));
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                conversationList.set(conversationList.indexOf(dataSnapshot.getValue(Conversation.class)),dataSnapshot.getValue(Conversation.class));
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                conversationList.remove(dataSnapshot.getValue(Conversation.class));
                notifyDataSetChanged();
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

        private TextView textMessage, textSender, textTime;
        private ImageView imagePicture, imageNew;

        public ViewHolder(View itemView) {
            super(itemView);

            textMessage = (TextView) itemView.findViewById(R.id.textConversationMessage);
            textSender = (TextView) itemView.findViewById(R.id.textConversationSender);
            textTime = (TextView) itemView.findViewById(R.id.textConversationTime);
            imagePicture = (ImageView) itemView.findViewById(R.id.imageConversationPicture);
            imageNew = (ImageView) itemView.findViewById(R.id.imageConversationNew);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_row,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Conversation currentConversation = conversationList.get(position);

        if (currentConversation.getMessages().size()>0) {
            //holder.textMessage.setText(currentConversation.getMessages().get(0).getMessageText());
        } else {
            holder.textMessage.setText("No messages in this conversation");
        }

        holder.textSender.setText(currentConversation.getWithUser());
        holder.textTime.setText(currentConversation.getLastMessageDate().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationListHandler.displayConversation(currentConversation.getConversationId(), currentConversation.getWithUser());
            }
        });

        if (currentConversation.getHasNewMessages()){
            holder.imageNew.setVisibility(View.VISIBLE);
        } else {
            holder.imageNew.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        userDatabase.child(currentConversation.getConversationId()).setValue(null);
                    }
                });
                builder.show();
                return true;
            }
        });

        FirebaseStorage.getInstance().getReference("profilePictures").child(currentConversation.getWithUser()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(holder.itemView.getContext()).load(uri).into(holder.imagePicture);
            }
        });
        FirebaseDatabase.getInstance().getReference("users").child(currentConversation.getWithUser()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.textSender.setText(dataSnapshot.child("firstName").getValue(String.class) + " " + dataSnapshot.child("lastName").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    interface IConversationListHandler{
        void displayConversation(String conversationId, String toUserId);
    }
}
