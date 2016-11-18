package com.group32.homework07;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Conversation> conversationList;
    private IConversationListHandler conversationListHandler;

    public ConversationRecyclerViewAdapter(IConversationListHandler handler) {
        this.conversationListHandler = handler;
        conversationList = new ArrayList<>();

        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                conversationList.add(dataSnapshot.getValue(Conversation.class));
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Conversation currentConversation = conversationList.get(position);
        if (currentConversation.getMessages().size()>0) {
            holder.textMessage.setText(currentConversation.getMessages().get(0).getMessageText());
        } else {
            holder.textMessage.setText("No messages in this conversation");
        }

        holder.textSender.setText(currentConversation.getWithUser());
        holder.textTime.setText(currentConversation.getLastMessageDate().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conversationListHandler.displayConversation(currentConversation.getConversationId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    interface IConversationListHandler{
        void displayConversation(String conversationId);
    }
}
