package com.group32.homework07;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements MessageRecyclerViewAdapter.IMessageListHandler{

    private String conversationId;
    private String toUser;

    private ArrayList<Message> messageList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference messagesDatabase;

    private RecyclerView recyclerViewMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageList = new ArrayList<>();

        conversationId = getIntent().getStringExtra("conversationId");
        toUser = getIntent().getStringExtra("toUser");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        messagesDatabase = mDatabase.getReference(mAuth.getCurrentUser().getUid())
                .child(conversationId)
                .child("messages");

        messagesDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messageList.add(dataSnapshot.getValue(Message.class));
                recyclerViewMessages.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                messageList.remove(dataSnapshot.getValue(Message.class));
                recyclerViewMessages.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Setup recyclerView
        recyclerViewMessages = (RecyclerView) findViewById(R.id.recyclerMessageList);
        recyclerViewMessages.setAdapter(new MessageRecyclerViewAdapter(messageList,this));
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.imageButtonChatSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void sendMessage(){
        String messageText = ((EditText) findViewById(R.id.editChatMessageText)).getText().toString();
        // If there is no message simply ignore the click
        if (messageText.length() == 0){
            return;
        }

        // Construct the message to be sent
        final Message newMessage = new Message();
        newMessage.setMessageText(messageText);
        newMessage.setSenderUserUid(mAuth.getCurrentUser().getUid());

        // Add the message into the conversation for the current user
        String messageId = messagesDatabase.push().getKey();
        newMessage.setMessageId(messageId);
        messagesDatabase.child(messageId).setValue(newMessage);

        // Add the message to the conversation for the user receiving the message
        String receiverMessageId = FirebaseDatabase.getInstance().getReference(toUser).child(conversationId).child("messages").push().getKey();
        newMessage.setMessageId(receiverMessageId);
        FirebaseDatabase.getInstance().getReference(toUser).child(conversationId).child("messages").child(receiverMessageId).setValue(newMessage);

        // Create the corresponding conversation to the user who receives the message in case it does not exist.
        FirebaseDatabase.getInstance().getReference(toUser).child(conversationId).child("withUser").setValue(mAuth.getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference(toUser).child(conversationId).child("conversationId").setValue(conversationId);
        FirebaseDatabase.getInstance().getReference(toUser).child(conversationId).child("lastMessageDate").setValue(new Date());

    }

    @Override
    public void deleteMessage(String messageId) {
        // Delete the message ONLY on the users end
        messagesDatabase.child(messageId).setValue(null);
    }
}
