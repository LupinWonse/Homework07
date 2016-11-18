package com.group32.homework07;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mDatabase = FirebaseDatabase.getInstance();
    }

    private void sendMessage(){
        // Construct the message to be sent
        Message newMessage = new Message();
        newMessage.setMessageText("TEXT MESSAGE");
        newMessage.setSenderUserUid(mAuth.getCurrentUser().getUid());
        newMessage.setToUserUid("testToUserUid");

    }
}
