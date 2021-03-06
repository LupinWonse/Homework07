package com.group32.homework07;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;


public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Message> messages;
    private IMessageListHandler handler;

    public MessageRecyclerViewAdapter(ArrayList<Message> messages, IMessageListHandler handler) {
        this.messages = messages;
        this.handler = handler;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textSender;
        private TextView textMessageText;
        private TextView textTime;
        private ImageView imageMessagePhoto;

        ViewHolder(View itemView) {
            super(itemView);

            textSender = (TextView) itemView.findViewById(R.id.textMessageSender);
            textMessageText = (TextView) itemView.findViewById(R.id.textMessageMessage);
            imageMessagePhoto = (ImageView) itemView.findViewById(R.id.imageMessagePhoto);
            textTime = (TextView) itemView.findViewById(R.id.textMessageTime);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Message currentMessage = messages.get(position);

        // If this message is a SENT message we have to adapt the view
        if (currentMessage.getSenderUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.textSender.setGravity(Gravity.END);
            holder.textMessageText.setGravity(Gravity.END);
            holder.textTime.setGravity(Gravity.END);
        } else {
            holder.textSender.setGravity(Gravity.START);
            holder.textMessageText.setGravity(Gravity.START);
            holder.textTime.setGravity(Gravity.START);
        }

        // Instead we should try to get the user name from the database
        FirebaseDatabase.getInstance().getReference("users").child(currentMessage.getSenderUserUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String firstname = dataSnapshot.child("firstName").getValue(String.class);
                String lastname = dataSnapshot.child("lastName").getValue(String.class);

                holder.textSender.setText(holder.itemView.getContext().getString(R.string.full_name,firstname,lastname));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.textMessageText.setText(currentMessage.getMessageText());
        holder.textTime.setText(currentMessage.getPrettyTime());

        if (currentMessage.getMessageImageUrl() != null){
            Picasso.with(holder.itemView.getContext()).load(currentMessage.getMessageImageUrl()).into(holder.imageMessagePhoto);
            holder.imageMessagePhoto.setVisibility(View.VISIBLE);
        } else {
            holder.imageMessagePhoto.setVisibility(View.GONE);
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                handler.deleteMessage(currentMessage.getMessageId());
                return true;
            }
        });

    }



    @Override
    public int getItemCount() {
        return messages.size();
    }

    interface IMessageListHandler{
        void deleteMessage (String messageId);
    }
}
