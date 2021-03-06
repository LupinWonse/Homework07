package com.group32.homework07;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements MessageRecyclerViewAdapter.IMessageListHandler{

    public static final int MESSAGE_PHOTO_REQUEST_CODE = 10;

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

       mDatabase.getReference(mAuth.getCurrentUser().getUid()).child(conversationId).child("hasNewMessages").setValue(false);

        messagesDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                messageList.add(dataSnapshot.getValue(Message.class));
                recyclerViewMessages.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                int indexChanged = messageList.indexOf(dataSnapshot.getValue(Message.class));
                messageList.set(indexChanged,dataSnapshot.getValue(Message.class));
                recyclerViewMessages.getAdapter().notifyDataSetChanged();
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
                sendTextMessage();
            }
        });

        findViewById(R.id.imageButtonChatPhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPicture();
            }
        });
    }

    private void sendTextMessage(){
        String messageText = ((EditText) findViewById(R.id.editChatMessageText)).getText().toString();
        // If there is no message simply ignore the click
        if (messageText.length() == 0){
            return;
        }

        // Construct the message to be sent
        Message newMessage = new Message();
        newMessage.setMessageText(messageText);
        newMessage.setSenderUserUid(mAuth.getCurrentUser().getUid());
        sendMessage(newMessage);
    }

    private void sendMessage(Message message){
        // Add the message into the conversation for the current user
        String messageId = messagesDatabase.push().getKey();
        message.setMessageId(messageId);
        messagesDatabase.child(messageId).setValue(message);

        // Create the conversation in case it does not exist
        Map<String, Object> conversationUpdate = new HashMap<>();
        conversationUpdate.put("/withUser/", mAuth.getCurrentUser().getUid());
        conversationUpdate.put("/conversationId/", conversationId);
        conversationUpdate.put("/lastMessageDate/", new Date());
        conversationUpdate.put("/hasNewMessages/", true);

        FirebaseDatabase.getInstance().getReference(toUser).child(conversationId).updateChildren(conversationUpdate);

        // Add the message to the conversation for the user receiving the message
        String receiverMessageId = FirebaseDatabase.getInstance().getReference(toUser).child(conversationId).child("messages").push().getKey();
        message.setMessageId(receiverMessageId);

        // Add the message
        FirebaseDatabase.getInstance().getReference(toUser).child(conversationId).child("messages").child(receiverMessageId).setValue(message);
    }

    private void sendPicture(){
        Intent getPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getPhotoIntent.setType("image/*");
        startActivityForResult(getPhotoIntent,MESSAGE_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MESSAGE_PHOTO_REQUEST_CODE && resultCode == RESULT_OK){

            final String messageId = messagesDatabase.push().getKey();


            // Upload the photo to the firebase storage
            InputStream photoInputStream = null;
            try {
                photoInputStream = getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid()).child(messageId).putStream(photoInputStream).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Construct the message to be sent
                    final Message newMessage = new Message();
                    newMessage.setMessageText(null);
                    newMessage.setSenderUserUid(mAuth.getCurrentUser().getUid());
                    newMessage.setMessageImageUrl(taskSnapshot.getDownloadUrl().toString());

                    sendMessage(newMessage);
                }
            });
        }
    }

    @Override
    public void deleteMessage(String messageId) {
        // Delete the message ONLY on the users end
        messagesDatabase.child(messageId).setValue(null);
    }

    @Override
    protected void onStop() {
        mDatabase.getReference(mAuth.getCurrentUser().getUid()).child(conversationId).child("hasNewMessages").setValue(false);
        super.onStop();
    }
}
